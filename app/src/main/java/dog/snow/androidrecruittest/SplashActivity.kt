package dog.snow.androidrecruittest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dog.snow.androidrecruittest.repository.model.RawAlbum
import dog.snow.androidrecruittest.repository.model.RawPhoto
import dog.snow.androidrecruittest.repository.model.RawUser
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

    private var rawPhotosList:MutableList<RawPhoto>? = mutableListOf()
    private var rawAlbumList:MutableList<RawAlbum>? = mutableListOf()
    private var rawUsersList:MutableList<RawUser>? = mutableListOf()


    //private lateinit var viewAdapter: dog.snow.androidrecruittest.ui.adapter.ListAdapter
    //private lateinit var viewManager: androidx.recyclerview.widget.LinearLayoutManager

    companion object{
        private var albumIdLimit:Int = 0
        private var userIdLimit:Int = 0
        private var itemsList:MutableList<ListItem>? = mutableListOf()
        private var detailsList:MutableList<Detail>? = mutableListOf()

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
    }


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        loadJSONDataUsingExecutorsService()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_place, ListFragment.newInstance(), ListFragment.TAG).commit();
//        viewManager = LinearLayoutManager(this)
//        viewAdapter = dog.snow.androidrecruittest.ui.adapter.ListAdapter{
//                item: ListItem, position: Int, view: View -> println("clicked $position")
//        }
//
//        viewAdapter.submitList(itemsList)
//        viewAdapter.notifyDataSetChanged()

//        val searchText = findViewById<TextInputEditText>(R.id.et_search)
//        var photosView = findViewById<RecyclerView>(R.id.rv_items).apply{
//            layoutManager = viewManager
//            adapter = viewAdapter
//        }




//        searchText.addTextChangedListener(object : TextWatcher {
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence, start: Int,
//                                       before: Int, count: Int) {
//                Toast.makeText(this@SplashActivity, searchText.text, Toast.LENGTH_SHORT).show()
//                viewAdapter.submitList(itemsList)
//                viewAdapter.notifyDataSetChanged()
//                println(viewAdapter.currentList)
//                println(viewAdapter.currentList.size)
//            }
//        })
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