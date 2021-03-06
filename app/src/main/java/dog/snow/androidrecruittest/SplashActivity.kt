package dog.snow.androidrecruittest

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dog.snow.androidrecruittest.FunHolder.Companion.checkIfCacheDataAvailable
import dog.snow.androidrecruittest.FunHolder.Companion.getDataFromURL
import dog.snow.androidrecruittest.FunHolder.Companion.isNetworkAvailable
import dog.snow.androidrecruittest.FunHolder.Companion.readDataFromCache
import dog.snow.androidrecruittest.FunHolder.Companion.saveBitmapListToCache
import dog.snow.androidrecruittest.FunHolder.Companion.saveJSONDataToCache
import dog.snow.androidrecruittest.repository.model.RawAlbum
import dog.snow.androidrecruittest.repository.model.RawPhoto
import dog.snow.androidrecruittest.repository.model.RawUser
import dog.snow.androidrecruittest.ui.model.Detail
import dog.snow.androidrecruittest.ui.model.ListItem
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class SplashActivity : AppCompatActivity(R.layout.splash_activity) {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        cacheDirString = cacheDir.toString()
        setContentView(R.layout.splash_activity)
        tryToGetData(isNetworkAvailable(applicationContext))
    }

    private fun showError(errorMessage: String?,  lambda: () -> Unit) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.cant_download_dialog_title)
            .setMessage(getString(R.string.cant_download_dialog_message, errorMessage))
            .setPositiveButton(R.string.cant_download_dialog_btn_positive) { _, _ -> lambda() }
            .setNegativeButton(R.string.cant_download_dialog_btn_negative) { _, _ -> finish() }
            .create()
            .apply { setCanceledOnTouchOutside(false) }
            .show()
    }

    private fun tryToGetData(isUserConnected:Boolean){
        if(checkIfCacheDataAvailable(cacheDirString)){
            initLoadingScreen(2500)
            readDataFromCache(cacheDirString)
        }else if(isUserConnected){
            initLoadingScreen(4000)
            loadDataAndSaveToCacheUsingExecutorsService()
        }else if(!isUserConnected){
            showError("No internet connection detected.")
            { tryToGetData(isNetworkAvailable(applicationContext)) }
        }
    }

    private fun initLoadingScreen(milisToInitFragment:Long){
        Handler().postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
            overridePendingTransition( R.anim.fade_in, R.anim.fade_out )
            finish()
        }, milisToInitFragment)
    }

    companion object{
        private lateinit var cacheDirString:String
        private const val LIMIT_OF_PHOTOS:Int = 100
        private const val PHOTOS_URL:String = "https://jsonplaceholder.typicode.com/photos?_limit=$LIMIT_OF_PHOTOS"
        private val executorService:ExecutorService = Executors.newSingleThreadExecutor()
        private var albumIdLimit:Int = 0
        private var userIdLimit:Int = 0
        private var itemsList:MutableList<ListItem>? = mutableListOf()
        private var detailsList:MutableList<Detail>? = mutableListOf()
        private var bitmapList:MutableList<Bitmap>? = mutableListOf()
        private var thumbnailBitmapList:MutableList<Bitmap>? = mutableListOf()
        private var rawPhotosList:MutableList<RawPhoto>? = mutableListOf()
        private var rawAlbumList:MutableList<RawAlbum>? = mutableListOf()
        private var rawUsersList:MutableList<RawUser>? = mutableListOf()

        fun getPhotosUrl():String{
            return PHOTOS_URL
        }

        fun getLimitOfPhotos():Int{
            return LIMIT_OF_PHOTOS
        }

        fun getAlbumIdLimit():Int{
            return albumIdLimit
        }

        fun getUserIdLimit():Int{
            return userIdLimit
        }

        fun getRawPhotosList():MutableList<RawPhoto>?{
            return rawPhotosList
        }

        fun getRawAlbumsList():MutableList<RawAlbum>?{
            return rawAlbumList
        }

        fun getRawUsersList():MutableList<RawUser>?{
            return rawUsersList
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

        fun setAlbumIdLimit(limit:Int){
            albumIdLimit = limit
        }

        fun setUserIdLimit(limit:Int){
            userIdLimit = limit
        }

        fun setRawPhotosList(rawPhotosList:MutableList<RawPhoto>){
            this.rawPhotosList = rawPhotosList
        }

        fun setRawAlbumsList(rawAlbumsList:MutableList<RawAlbum>){
            this.rawAlbumList = rawAlbumsList
        }

        fun setRawUsersList(rawUsersList:MutableList<RawUser>){
            this.rawUsersList = rawUsersList
        }

        fun setItemList(itemsList:MutableList<ListItem>){
            this.itemsList = itemsList
        }

        fun setDetailsList(detailsList:MutableList<Detail>){
            this.detailsList = detailsList
        }

        fun setBitmapList(bitmapList:MutableList<Bitmap>){
            this.bitmapList = bitmapList
        }

        fun setThumbnailBitmapList(thumbnailBitmapList:MutableList<Bitmap>){
            this.thumbnailBitmapList = thumbnailBitmapList
        }

        @Throws(IOException::class)
        private fun saveDataToCache(){
            saveJSONDataToCache(cacheDirString)
            saveBitmapListToCache(cacheDirString, thumbnailBitmapList!!, true)
            saveBitmapListToCache(cacheDirString, bitmapList!!, false)
        }

        @Throws(InterruptedException::class)
        fun downloadAndSaveDataToCache(){
            Thread {
                try{
                    getDataFromURL()
                    saveDataToCache()
                }catch(e:IOException){
                    println(e.message)
                }
            }.start()
        }

        private var loadJSONRunnable = Runnable()
        {
            run {
                downloadAndSaveDataToCache()
            }
        }

        fun loadDataAndSaveToCacheUsingExecutorsService(){
            try{
                executorService.execute(loadJSONRunnable)
            }catch(e:ExecutionException){
                println(e.message)
            }finally{
                executorService.shutdown()
            }
        }
    }
}