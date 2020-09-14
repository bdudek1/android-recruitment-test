package dog.snow.androidrecruittest

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dog.snow.androidrecruittest.repository.model.RawAlbum
import dog.snow.androidrecruittest.repository.model.RawPhoto
import dog.snow.androidrecruittest.repository.model.RawUser
import dog.snow.androidrecruittest.ui.FunHolder.Companion.extractBitmapsFromRawPhotos
import dog.snow.androidrecruittest.ui.FunHolder.Companion.extractRawPhotosFromJSONArray
import dog.snow.androidrecruittest.ui.FunHolder.Companion.getJsonFromURL
import dog.snow.androidrecruittest.ui.FunHolder.Companion.getRawAlbumsFromURL
import dog.snow.androidrecruittest.ui.FunHolder.Companion.getRawUsersFromURL
import dog.snow.androidrecruittest.ui.FunHolder.Companion.initDetailsList
import dog.snow.androidrecruittest.ui.FunHolder.Companion.initItemsList
import dog.snow.androidrecruittest.ui.ListFragment
import dog.snow.androidrecruittest.ui.model.Detail
import dog.snow.androidrecruittest.ui.model.ListItem
import org.json.JSONArray
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class SplashActivity : AppCompatActivity(R.layout.splash_activity) {

    private val LIMIT_OF_PHOTOS:Int = 100
    private val PHOTOS_URL:String = "https://jsonplaceholder.typicode.com/photos?_limit=$LIMIT_OF_PHOTOS"
    private val executorService:ExecutorService = Executors.newSingleThreadExecutor()

    companion object{
        private var albumIdLimit:Int = 0
        private var userIdLimit:Int = 0
        private var itemsList:MutableList<ListItem>? = mutableListOf()
        private var detailsList:MutableList<Detail>? = mutableListOf()
        private var bitmapList:MutableList<Bitmap>? = mutableListOf()
        private var thumbnailBitmapList:MutableList<Bitmap>? = mutableListOf()
        private var rawPhotosList:MutableList<RawPhoto>? = mutableListOf()
        private var rawAlbumList:MutableList<RawAlbum>? = mutableListOf()
        private var rawUsersList:MutableList<RawUser>? = mutableListOf()

        fun getAlbumIdLimit():Int{
            return albumIdLimit
        }

        fun getUserIdLimit():Int{
            return userIdLimit
        }

        fun setAlbumIdLimit(limit:Int){
            albumIdLimit = limit
        }

        fun setUserIdLimit(limit:Int){
            userIdLimit = limit
        }

        fun getItemList():MutableList<ListItem>?{
            return itemsList
        }

        fun getDetailList():MutableList<Detail>?{
            return detailsList
        }

        fun getBitmapList():MutableList<Bitmap>?{
            return bitmapList
        }

        fun getThumbnailBitmapList():MutableList<Bitmap>?{
            return thumbnailBitmapList
        }

        fun getRawPhotosList():MutableList<RawPhoto>?{
            return rawPhotosList
        }
    }


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        Handler().postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }, 5000)
        loadJSONDataUsingExecutorsService()
        //thumbnailBitmapList.size
        //while(detailsList?.size!! < LIMIT_OF_PHOTOS){
        //    Thread.sleep(50)
        //}
        //setContentView(R.layout.main_activity)
        //supportFragmentManager.beginTransaction()
        //                      .replace(R.id.container, ListFragment.newInstance(), ListFragment.TAG)
         //                     .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
        //                      .commit()
    }

    private fun showError(errorMessage: String?) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.cant_download_dialog_title)
            .setMessage(getString(R.string.cant_download_dialog_message, errorMessage))
            .setPositiveButton(R.string.cant_download_dialog_btn_positive) { _, _ -> /*tryAgain()*/ }
            .setNegativeButton(R.string.cant_download_dialog_btn_negative) { _, _ -> finish() }
            .create()
            .apply { setCanceledOnTouchOutside(false) }
            .show()
    }


    private fun loadJSONData(){
            Thread {
                try{
                    rawPhotosList = extractRawPhotosFromJSONArray(JSONArray(getJsonFromURL(PHOTOS_URL)))
                    rawAlbumList = getRawAlbumsFromURL(albumIdLimit)
                    rawUsersList = getRawUsersFromURL(userIdLimit)
                    itemsList = initItemsList(rawPhotosList!!, rawAlbumList!!)
                    detailsList = initDetailsList(rawPhotosList!!, rawAlbumList!!, rawUsersList!!)
                    thumbnailBitmapList = extractBitmapsFromRawPhotos(rawPhotosList!!, true)
                    bitmapList = extractBitmapsFromRawPhotos(rawPhotosList!!, false)
                }catch(e:IOException){
                    println(e.message)
                    runOnUiThread { showError(e.message) }
                }
            }.start()
    }

    var loadJSONRunnable = Runnable()
    {
        run(){
            loadJSONData()
        }
    }

    fun loadJSONDataUsingExecutorsService(){
        try{
            executorService.execute(loadJSONRunnable)
            if (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                Toast.makeText(this@SplashActivity, "Loading data...", Toast.LENGTH_SHORT).show()
            }
        }catch(e:ExecutionException){
            println(e.message)
            showError(e.message)
        }finally{
            executorService.shutdown()
        }
    }

}