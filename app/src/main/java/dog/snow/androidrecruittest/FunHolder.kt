package dog.snow.androidrecruittest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import com.google.gson.Gson
import dog.snow.androidrecruittest.SplashActivity.Companion.getAlbumIdLimit
import dog.snow.androidrecruittest.SplashActivity.Companion.getDetailList
import dog.snow.androidrecruittest.SplashActivity.Companion.getItemList
import dog.snow.androidrecruittest.SplashActivity.Companion.getLimitOfPhotos
import dog.snow.androidrecruittest.SplashActivity.Companion.getPhotosUrl
import dog.snow.androidrecruittest.SplashActivity.Companion.getRawAlbumsList
import dog.snow.androidrecruittest.SplashActivity.Companion.getRawPhotosList
import dog.snow.androidrecruittest.SplashActivity.Companion.getRawUsersList
import dog.snow.androidrecruittest.SplashActivity.Companion.getUserIdLimit
import dog.snow.androidrecruittest.SplashActivity.Companion.setAlbumIdLimit
import dog.snow.androidrecruittest.SplashActivity.Companion.setBitmapList
import dog.snow.androidrecruittest.SplashActivity.Companion.setDetailsList
import dog.snow.androidrecruittest.SplashActivity.Companion.setItemList
import dog.snow.androidrecruittest.SplashActivity.Companion.setRawAlbumsList
import dog.snow.androidrecruittest.SplashActivity.Companion.setRawPhotosList
import dog.snow.androidrecruittest.SplashActivity.Companion.setRawUsersList
import dog.snow.androidrecruittest.SplashActivity.Companion.setThumbnailBitmapList
import dog.snow.androidrecruittest.SplashActivity.Companion.setUserIdLimit
import dog.snow.androidrecruittest.repository.model.RawAlbum
import dog.snow.androidrecruittest.repository.model.RawPhoto
import dog.snow.androidrecruittest.repository.model.RawUser
import dog.snow.androidrecruittest.ui.ListFragment
import dog.snow.androidrecruittest.ui.model.Detail
import dog.snow.androidrecruittest.ui.model.ListItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class FunHolder{
    companion object{
        private fun getJsonStringFromURL(wantedURL: String) : String {
            return URL(wantedURL).readText()
        }

        @Throws(IOException::class)
        fun getRawGeoFromURL(index:Int): RawUser.RawAddress.RawGeo{
            val url = "https://jsonplaceholder.typicode.com/users/$index"

            return RawUser.RawAddress.RawGeo(
                JSONObject(getJsonStringFromURL(url)).getJSONObject("address")
                                                     .getJSONObject("geo")
                                                     .getString("lat"),
                JSONObject(getJsonStringFromURL(url)).getJSONObject("address")
                                                     .getJSONObject("geo")
                                                     .getString("lng"))
        }

        @Throws(IOException::class)
        fun getRawAddressFromURL(index:Int): RawUser.RawAddress{
            val url = "https://jsonplaceholder.typicode.com/users/$index"

            return RawUser.RawAddress(
                JSONObject(getJsonStringFromURL(url)).getJSONObject("address").getString("street"),
                JSONObject(getJsonStringFromURL(url)).getJSONObject("address").getString("suite"),
                JSONObject(getJsonStringFromURL(url)).getJSONObject("address").getString("city"),
                JSONObject(getJsonStringFromURL(url)).getJSONObject("address").getString("zipcode"),
                getRawGeoFromURL(index)
            )
        }

        @Throws(IOException::class)
        fun getRawCompanyFromURL(index:Int): RawUser.RawCompany {
            val url = "https://jsonplaceholder.typicode.com/users/$index"

            return RawUser.RawCompany(
                JSONObject(getJsonStringFromURL(url)).getJSONObject("company").getString("name"),
                JSONObject(getJsonStringFromURL(url)).getJSONObject("company").getString("catchPhrase"),
                JSONObject(getJsonStringFromURL(url)).getJSONObject("company").getString("bs"))
        }

        @Throws(IOException::class)
        fun getRawUsersFromURL(size:Int):MutableList<RawUser>{
            val rawUsersList = mutableListOf<RawUser>()
            for(i in 1..size){
                val url = "https://jsonplaceholder.typicode.com/users/$i"
                rawUsersList.add(RawUser(
                    JSONObject(getJsonStringFromURL(url)).getInt("id"),
                    JSONObject(getJsonStringFromURL(url)).getString("name"),
                    JSONObject(getJsonStringFromURL(url)).getString("username"),
                    JSONObject(getJsonStringFromURL(url)).getString("email"), getRawAddressFromURL(i),
                    JSONObject(getJsonStringFromURL(url)).getString("phone"),
                    JSONObject(getJsonStringFromURL(url)).getString("website"), getRawCompanyFromURL(i)))
            }

            return rawUsersList
        }

        @Throws(IOException::class)
        fun getRawAlbumsFromURL(size:Int):MutableList<RawAlbum>{
            val rawAlbumsList = mutableListOf<RawAlbum>()
            for(i in 1..size){
                val url = "https://jsonplaceholder.typicode.com/albums/$i"
                rawAlbumsList.add(RawAlbum(
                        JSONObject(getJsonStringFromURL(url)).getInt("id"),
                        JSONObject(getJsonStringFromURL(url)).getInt("userId"),
                        JSONObject(getJsonStringFromURL(url)).getString("title")))

                if(getUserIdLimit() < JSONObject(getJsonStringFromURL(url)).getInt("userId"))
                    setUserIdLimit(JSONObject(getJsonStringFromURL(url)).getInt("userId"))
            }

            return rawAlbumsList
        }

        private fun initItemsList(photos:MutableList<RawPhoto>,
                                  albums:MutableList<RawAlbum>):MutableList<ListItem>{
            val itemsList:MutableList<ListItem> = mutableListOf()
            for(photo in photos){
                val albumTitle:String = albums.single { a -> a.id.equals(photo.albumId) }.title
                itemsList.add(ListItem(photo.id, photo.title, albumTitle, photo.thumbnailUrl))
            }

            return itemsList
        }

        private fun initDetailsList(photos:MutableList<RawPhoto>,
                                    albums:MutableList<RawAlbum>,
                                    users:MutableList<RawUser>):MutableList<Detail>{
            val detailsList:MutableList<Detail> = mutableListOf()
            for(photo in photos){
                val albumTitle:String = albums.single { a -> a.id.equals(photo.albumId) }.title
                val userId:Int = albums.single { a -> a.id.equals(photo.albumId) }.userId
                val user:RawUser = users.single { u -> u.id.equals(userId) }
                val username:String = user.username
                val email:String = user.email
                val phone:String = user.phone
                val url: String = user.website

                detailsList.add(Detail(photo.id, photo.title, albumTitle, username, email, phone, url))
            }

            return detailsList
        }

        private fun extractRawPhotosFromJSONArray(jsonArray: JSONArray?):MutableList<RawPhoto>{
            val rawPhotoList = mutableListOf<RawPhoto>()
            for(i in 0 until jsonArray!!.length()){
                rawPhotoList.add(RawPhoto(jsonArray.getJSONObject(i).getInt("id"),
                        jsonArray.getJSONObject(i).getInt("albumId"),
                        jsonArray.getJSONObject(i).getString("title"),
                        jsonArray.getJSONObject(i).getString("url"),
                        jsonArray.getJSONObject(i).getString("thumbnailUrl")))

                if(getAlbumIdLimit() < jsonArray.getJSONObject(i).getInt("albumId"))
                    setAlbumIdLimit(jsonArray.getJSONObject(i).getInt("albumId"))
            }

            return rawPhotoList
        }

        @Throws(IOException::class)
        fun extractBitmapsFromRawPhotos(rawPhotos:MutableList<RawPhoto>, ifThumbnail:Boolean):MutableList<Bitmap>{
            val bitmapList:MutableList<Bitmap> = mutableListOf()
            for(photo in rawPhotos){
                val url:URL = if(ifThumbnail) URL(photo.thumbnailUrl) else URL(photo.url)
                    val connection = url.openConnection() as HttpsURLConnection
                    connection.doInput = true
                    connection.setRequestProperty("User-Agent","Test-app")
                    connection.connect()
                    val input = connection.inputStream
                    val placeholderBitmap = BitmapFactory.decodeStream(input)
                    bitmapList.add(placeholderBitmap!!)
                    connection.disconnect()
                    if(ifThumbnail){
                        ListFragment.submitListIncludingFilter()
                    }
                }
            return bitmapList
        }

        @Throws(IOException::class)
        fun writeRawAlbumToCache(s:String, jsonObject:RawAlbum) {
            val gson = Gson()
            val jsonString:String = gson.toJson(jsonObject)
            val file= File(s)
            file.writeText(jsonString)
        }

        @Throws(IOException::class)
        fun writeRawPhotoToCache(s:String, jsonObject:RawPhoto) {
            val gson = Gson()
            val jsonString:String = gson.toJson(jsonObject)
            val file= File(s)
            file.writeText(jsonString)
        }

        @Throws(IOException::class)
        fun writeRawUserToCache(s:String, jsonObject:RawUser) {
            val gson = Gson()
            val jsonString:String = gson.toJson(jsonObject)
            val file= File(s)
            file.writeText(jsonString)
        }

        @Throws(IOException::class)
        fun writeListItemToCache(s:String, jsonObject:ListItem) {
            val gson = Gson()
            val jsonString:String = gson.toJson(jsonObject)
            val file= File(s)
            file.writeText(jsonString)
        }

        @Throws(IOException::class)
        fun writeDetailToCache(s:String, jsonObject:Detail) {
            val gson = Gson()
            val jsonString:String = gson.toJson(jsonObject)
            val file= File(s)
            file.writeText(jsonString)
        }

        @Throws(IOException::class)
        fun readRawAlbumsFromCache(cacheDir:String, howMuch:Int):MutableList<RawAlbum> {
            val gson = Gson()
            val rawAlbumList:MutableList<RawAlbum> = mutableListOf()
            for(i in 1..howMuch){
                val filePath = "$cacheDir/album$i"
                val bufferedReader: BufferedReader = File(filePath).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                val rawAlbum:RawAlbum = gson.fromJson(inputString, RawAlbum::class.java)
                if(getUserIdLimit() < rawAlbum.userId)
                    setUserIdLimit(rawAlbum.userId)

                rawAlbumList.add(rawAlbum)
            }
            return rawAlbumList
        }

        @Throws(IOException::class)
        fun readRawPhotosFromCache(cacheDir:String, howMuch:Int):MutableList<RawPhoto> {
            val gson = Gson()
            val rawPhotoList:MutableList<RawPhoto> = mutableListOf()
            for(i in 1..howMuch){
                val filePath = "$cacheDir/photo$i"
                val bufferedReader: BufferedReader = File(filePath).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                val rawPhoto:RawPhoto= gson.fromJson(inputString, RawPhoto::class.java)

                if(getAlbumIdLimit() < rawPhoto.albumId)
                    setAlbumIdLimit(rawPhoto.albumId)
                rawPhotoList.add(rawPhoto)
            }
            return rawPhotoList
        }

        @Throws(IOException::class)
        fun readRawUsersFromCache(cacheDir:String, howMuch:Int):MutableList<RawUser> {
            val gson = Gson()
            val rawUserList:MutableList<RawUser> = mutableListOf()
            for(i in 1..howMuch){
                val filePath = "$cacheDir/user$i"
                val bufferedReader: BufferedReader = File(filePath).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                val rawUser:RawUser= gson.fromJson(inputString, RawUser::class.java)
                rawUserList.add(rawUser)
            }
            return rawUserList
        }

        @Throws(IOException::class)
        fun readListItemsFromCache(cacheDir:String, howMuch:Int):MutableList<ListItem> {
            val gson = Gson()
            val listItemList:MutableList<ListItem> = mutableListOf()
            for(i in 1..howMuch){
                val filePath = "$cacheDir/listItem$i"
                val bufferedReader: BufferedReader = File(filePath).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                val listItem:ListItem= gson.fromJson(inputString, ListItem::class.java)
                listItemList.add(listItem)
            }
            return listItemList
        }

        @Throws(IOException::class)
        fun readDetailsFromCache(cacheDir:String, howMuch:Int):MutableList<Detail> {
            val gson = Gson()
            val detailList:MutableList<Detail> = mutableListOf()
            for(i in 1..howMuch){
                val filePath = "$cacheDir/detail$i"
                val bufferedReader: BufferedReader = File(filePath).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                val detail:Detail= gson.fromJson(inputString, Detail::class.java)
                detailList.add(detail)
            }
            return detailList
        }

        @Throws(IOException::class)
        fun getDataFromURL(){
            setRawPhotosList(extractRawPhotosFromJSONArray(JSONArray(getJsonStringFromURL(getPhotosUrl()))))
            setRawAlbumsList(getRawAlbumsFromURL(getAlbumIdLimit()))
            setRawUsersList(getRawUsersFromURL(getUserIdLimit()))
            setItemList(initItemsList(getRawPhotosList()!!, getRawAlbumsList()!!))
            setDetailsList(initDetailsList(getRawPhotosList()!!, getRawAlbumsList()!!, getRawUsersList()!!))
            setThumbnailBitmapList(extractBitmapsFromRawPhotos(getRawPhotosList()!!, true))
            setBitmapList(extractBitmapsFromRawPhotos(getRawPhotosList()!!, false))
        }

        @Throws(IOException::class)
        fun saveJSONDataToCache(cacheDir:String){
            getRawPhotosList()?.forEach{ a -> writeRawPhotoToCache(cacheDir + "/photo" + a.id.toString(), a) }
            getRawAlbumsList()?.forEach{ a -> writeRawAlbumToCache(cacheDir + "/album" + a.id.toString(), a) }
            getRawUsersList()?.forEach{ a -> writeRawUserToCache(cacheDir + "/user" + a.id.toString(), a) }
            getItemList()?.forEach{ a -> writeListItemToCache(cacheDir + "/listItem" + a.id.toString(), a) }
            getDetailList()?.forEach{ a -> writeDetailToCache(cacheDir + "/detail" + a.photoId.toString(), a) }
        }

        @Throws(IOException::class)
        fun readJSONDataFromCache(cacheDir:String){
            setRawPhotosList(readRawPhotosFromCache(cacheDir, getLimitOfPhotos()))
            setRawAlbumsList(readRawAlbumsFromCache(cacheDir, getAlbumIdLimit()))
            setRawUsersList(readRawUsersFromCache(cacheDir, getUserIdLimit()))
            setItemList(readListItemsFromCache(cacheDir, getLimitOfPhotos()))
            setDetailsList(readDetailsFromCache(cacheDir, getLimitOfPhotos()))
        }

        @Throws(IOException::class)
        fun saveBitmapListToCache(cacheDir:String, bitmapList:MutableList<Bitmap>, isThumbnail:Boolean){
            for (i in 1.. bitmapList.size){
                val path:String = if(isThumbnail)"$cacheDir/thumbnailBitmap$i" else "$cacheDir/bitmap$i"
                val file = File(path)
                val stream: OutputStream = FileOutputStream(file)
                bitmapList[i-1].compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            }
        }

        @Throws(IOException::class)
        fun readBitmapListFromCache(cacheDir:String, howMuch:Int, isThumbnail:Boolean):MutableList<Bitmap>{
            val bitmapList:MutableList<Bitmap> = mutableListOf()
            var path:String
            for(i in 1.. howMuch){
                path = if(isThumbnail) "$cacheDir/thumbnailBitmap$i" else "$cacheDir/bitmap$i"
                val bitmap = BitmapFactory.decodeFile(path)
                bitmapList.add(bitmap)
            }
            return bitmapList
        }

        fun readDataFromCache(cacheDir:String){
            try{
                readJSONDataFromCache(cacheDir)
                setBitmapList(readBitmapListFromCache(cacheDir, getLimitOfPhotos(), false))
                setThumbnailBitmapList(readBitmapListFromCache(cacheDir, getLimitOfPhotos(), true))
            }catch(e:IOException){
                println(e.message)
            }
        }

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo
                .isConnected
        }

        fun checkIfCacheDataAvailable(cacheDir:String):Boolean{
            val path = "$cacheDir/bitmap${getLimitOfPhotos()}"
            val file = File(path)
            return file.exists()
        }
    }
}