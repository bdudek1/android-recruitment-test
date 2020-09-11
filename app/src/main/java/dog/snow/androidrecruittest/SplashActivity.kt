package dog.snow.androidrecruittest

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dog.snow.androidrecruittest.repository.model.RawAlbum
import dog.snow.androidrecruittest.repository.model.RawPhoto
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class SplashActivity : AppCompatActivity(R.layout.splash_activity) {

    var LIMIT_OF_PHOTOS:Int = 100
    var PHOTOS_URL:String = "https://jsonplaceholder.typicode.com/photos?_limit=$LIMIT_OF_PHOTOS"

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_fragment)
        var jsonArray:JSONArray? = null
        var rawAlbumList:MutableList<RawAlbum>? = mutableListOf()
        Thread({
            jsonArray = JSONArray(getJsonFromURL(PHOTOS_URL))
            rawAlbumList = getRawAlbumsFromURL(10)
        }).start()
        val text = findViewById<TextInputEditText>(R.id.et_search)
        text.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                Toast.makeText(this@SplashActivity, text.text, Toast.LENGTH_SHORT).show()
                println(extractRawPhotosFromJSONArray(jsonArray))
                println(extractRawPhotosFromJSONArray(jsonArray).size)
            }
        })
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

    fun getJsonFromURL(wantedURL: String) : String {
        return URL(wantedURL).readText()
    }

    fun extractRawPhotosFromJSONArray(jsonArray:JSONArray?):MutableList<RawPhoto>{
        val rawPhotoList = mutableListOf<RawPhoto>()
        for(i in 0 until jsonArray!!.length()){
            rawPhotoList.add(RawPhoto(jsonArray.getJSONObject(i).getInt("id"),
                jsonArray.getJSONObject(i).getInt("albumId"),
                jsonArray.getJSONObject(i).getString("title"),
                jsonArray.getJSONObject(i).getString("url"),
                jsonArray.getJSONObject(i).getString("thumbnailUrl")))
        }

        return rawPhotoList
    }

    fun getRawAlbumsFromURL(size:Int):MutableList<RawAlbum>{
        val rawAlbumsList = mutableListOf<RawAlbum>()
        for(i in 1..size){
            val URL:String = "https://jsonplaceholder.typicode.com/albums/$i"
            rawAlbumsList.add(RawAlbum(JSONObject(getJsonFromURL(URL)).getInt("id"),
                JSONObject(getJsonFromURL(URL)).getInt("userId"),
                JSONObject(getJsonFromURL(URL)).getString("title")))
        }

        return rawAlbumsList
    }



}