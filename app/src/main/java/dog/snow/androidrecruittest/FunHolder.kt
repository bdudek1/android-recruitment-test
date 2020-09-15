package dog.snow.androidrecruittest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import com.google.gson.Gson
import dog.snow.androidrecruittest.SplashActivity.Companion.PHOTOS_URL
import dog.snow.androidrecruittest.SplashActivity.Companion.getAlbumIdLimit
import dog.snow.androidrecruittest.SplashActivity.Companion.getBitmapList
import dog.snow.androidrecruittest.SplashActivity.Companion.getDetailList
import dog.snow.androidrecruittest.SplashActivity.Companion.getItemList
import dog.snow.androidrecruittest.SplashActivity.Companion.getLimitOfPhotos
import dog.snow.androidrecruittest.SplashActivity.Companion.getRawAlbumsList
import dog.snow.androidrecruittest.SplashActivity.Companion.getRawPhotosList
import dog.snow.androidrecruittest.SplashActivity.Companion.getRawUsersList
import dog.snow.androidrecruittest.SplashActivity.Companion.getThumbnailBitmapList
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
        public fun getJsonFromURL(wantedURL: String) : String {
            return URL(wantedURL).readText()
        }

        fun getRawGeoFromURL(index:Int): RawUser.RawAddress.RawGeo{
            val URL:String = "https://jsonplaceholder.typicode.com/users/$index"
            val rawGeo: RawUser.RawAddress.RawGeo =
                RawUser.RawAddress.RawGeo(
                    JSONObject(getJsonFromURL(URL)).getJSONObject("address").getJSONObject("geo").getString("lat"),
                    JSONObject(getJsonFromURL(URL)).getJSONObject("address").getJSONObject("geo").getString("lng"))

            return rawGeo
        }

        fun getRawAddressFromURL(index:Int): RawUser.RawAddress{
            val URL:String = "https://jsonplaceholder.typicode.com/users/$index"
            val rawAddress:RawUser.RawAddress =
                RawUser.RawAddress(
                    JSONObject(getJsonFromURL(URL)).getJSONObject("address").getString("street"),
                    JSONObject(getJsonFromURL(URL)).getJSONObject("address").getString("suite"),
                    JSONObject(getJsonFromURL(URL)).getJSONObject("address").getString("city"),
                    JSONObject(getJsonFromURL(URL)).getJSONObject("address").getString("zipcode"), getRawGeoFromURL(index))

            return rawAddress
        }

        fun getRawCompanyFromURL(index:Int): RawUser.RawCompany {
            val URL:String = "https://jsonplaceholder.typicode.com/users/$index"
            val rawCompany:RawUser.RawCompany =
                RawUser.RawCompany(
                    JSONObject(getJsonFromURL(URL)).getJSONObject("company").getString("name"),
                    JSONObject(getJsonFromURL(URL)).getJSONObject("company").getString("catchPhrase"),
                    JSONObject(getJsonFromURL(URL)).getJSONObject("company").getString("bs"))

            return rawCompany
        }

        fun getRawUsersFromURL(size:Int):MutableList<RawUser>{
            val rawUsersList = mutableListOf<RawUser>()
            for(i in 1..size){
                val URL:String = "https://jsonplaceholder.typicode.com/users/$i"
                rawUsersList.add(RawUser(
                    JSONObject(getJsonFromURL(URL)).getInt("id"),
                    JSONObject(getJsonFromURL(URL)).getString("name"),
                    JSONObject(getJsonFromURL(URL)).getString("username"),
                    JSONObject(getJsonFromURL(URL)).getString("email"), getRawAddressFromURL(i),
                    JSONObject(getJsonFromURL(URL)).getString("phone"),
                    JSONObject(getJsonFromURL(URL)).getString("website"), getRawCompanyFromURL(i)))
            }

            return rawUsersList
        }

        fun getRawAlbumsFromURL(size:Int):MutableList<RawAlbum>{
            val rawAlbumsList = mutableListOf<RawAlbum>()
            for(i in 1..size){
                val URL:String = "https://jsonplaceholder.typicode.com/albums/$i"
                rawAlbumsList.add(RawAlbum(
                        JSONObject(getJsonFromURL(URL)).getInt("id"),
                        JSONObject(getJsonFromURL(URL)).getInt("userId"),
                        JSONObject(getJsonFromURL(URL)).getString("title")))

                if(getUserIdLimit() < JSONObject(getJsonFromURL(URL)).getInt("userId"))
                    setUserIdLimit(JSONObject(getJsonFromURL(URL)).getInt("userId"))
            }

            return rawAlbumsList
        }

        fun initItemsList(photos:MutableList<RawPhoto>,
                          albums:MutableList<RawAlbum>):MutableList<ListItem>{
            val itemsList:MutableList<ListItem> = mutableListOf()
            for(photo in photos){
                val albumTitle:String = albums.filter{a -> a.id.equals(photo.albumId)}.single().title
                itemsList.add(ListItem(photo.id, photo.title, albumTitle, photo.thumbnailUrl))
            }

            return itemsList
        }

        fun initDetailsList(photos:MutableList<RawPhoto>,
                            albums:MutableList<RawAlbum>,
                            users:MutableList<RawUser>):MutableList<Detail>{
            val detailsList:MutableList<Detail> = mutableListOf()
            for(photo in photos){
                val albumTitle:String = albums.filter{a -> a.id.equals(photo.albumId)}.single().title
                val userId:Int = albums.filter{a -> a.id.equals(photo.albumId)}.single().userId
                val user:RawUser = users.filter {u -> u.id.equals(userId)}.single()
                val username:String = user.username
                val email:String = user.email
                val phone:String = user.phone
                val url: String = user.website

                detailsList.add(Detail(photo.id, photo.title, albumTitle, username, email, phone, url))
            }

            return detailsList
        }

        fun extractRawPhotosFromJSONArray(jsonArray: JSONArray?):MutableList<RawPhoto>{
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

        fun extractBitmapsFromRawPhotos(rawPhotos:MutableList<RawPhoto>, ifThumbnail:Boolean):MutableList<Bitmap>{
            val bitmapList:MutableList<Bitmap> = mutableListOf()
            for(photo in rawPhotos){
                var url:URL
                if(ifThumbnail) url = URL(photo.thumbnailUrl) else url = URL(photo.url)
                try {
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
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return bitmapList
        }

        fun writeRawAlbumToCache(s:String, jsonObject:RawAlbum) {
            val gson = Gson()
            val jsonString:String = gson.toJson(jsonObject)
            val file= File(s)
            file.writeText(jsonString)
        }

        fun writeRawPhotoToCache(s:String, jsonObject:RawPhoto) {
            val gson = Gson()
            val jsonString:String = gson.toJson(jsonObject)
            val file= File(s)
            file.writeText(jsonString)
        }

        fun writeRawUserToCache(s:String, jsonObject:RawUser) {
            val gson = Gson()
            val jsonString:String = gson.toJson(jsonObject)
            val file= File(s)
            file.writeText(jsonString)
        }

        fun writeListItemToCache(s:String, jsonObject:ListItem) {
            val gson = Gson()
            val jsonString:String = gson.toJson(jsonObject)
            val file= File(s)
            file.writeText(jsonString)
        }

        fun writeDetailToCache(s:String, jsonObject:Detail) {
            val gson = Gson()
            val jsonString:String = gson.toJson(jsonObject)
            val file= File(s)
            file.writeText(jsonString)
        }

        fun readRawAlbumsFromCache(cacheDir:String, howMuch:Int):MutableList<RawAlbum> {
            var gson = Gson()
            val rawAlbumList:MutableList<RawAlbum> = mutableListOf()
            for(i in 1..howMuch){
                val filePath:String = cacheDir + "/album" + i
                val bufferedReader: BufferedReader = File(filePath).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                val rawAlbum:RawAlbum = gson.fromJson(inputString, RawAlbum::class.java)
                if(getUserIdLimit() < rawAlbum.userId)
                    setUserIdLimit(rawAlbum.userId)

                rawAlbumList.add(rawAlbum)
            }
            return rawAlbumList
        }

        fun readRawPhotosFromCache(cacheDir:String, howMuch:Int):MutableList<RawPhoto> {
            val gson = Gson()
            val rawPhotoList:MutableList<RawPhoto> = mutableListOf()
            for(i in 1..howMuch){
                val filePath:String = cacheDir +"/photo" + i
                val bufferedReader: BufferedReader = File(filePath).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                val rawPhoto:RawPhoto= gson.fromJson(inputString, RawPhoto::class.java)

                if(getAlbumIdLimit() < rawPhoto.albumId)
                    setAlbumIdLimit(rawPhoto.albumId)
                rawPhotoList.add(rawPhoto)
            }
            return rawPhotoList
        }

        fun readRawUsersFromCache(cacheDir:String, howMuch:Int):MutableList<RawUser> {
            val gson = Gson()
            val rawUserList:MutableList<RawUser> = mutableListOf()
            for(i in 1..howMuch){
                val filePath:String = cacheDir + "/user" + i
                val bufferedReader: BufferedReader = File(filePath).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                val rawUser:RawUser= gson.fromJson(inputString, RawUser::class.java)
                rawUserList.add(rawUser)
            }
            return rawUserList
        }

        fun readListItemsFromCache(cacheDir:String, howMuch:Int):MutableList<ListItem> {
            val gson = Gson()
            val listItemList:MutableList<ListItem> = mutableListOf()
            for(i in 1..howMuch){
                val filePath:String = cacheDir + "/listItem" + i
                val bufferedReader: BufferedReader = File(filePath).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                val listItem:ListItem= gson.fromJson(inputString, ListItem::class.java)
                listItemList.add(listItem)
            }
            return listItemList
        }

        fun readDetailsFromCache(cacheDir:String, howMuch:Int):MutableList<Detail> {
            val gson = Gson()
            val detailList:MutableList<Detail> = mutableListOf()
            for(i in 1..howMuch){
                val filePath:String = cacheDir +"/detail" + i
                val bufferedReader: BufferedReader = File(filePath).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                val detail:Detail= gson.fromJson(inputString, Detail::class.java)
                detailList.add(detail)
            }
            return detailList
        }

        fun getDataFromURL(){
            setRawPhotosList(extractRawPhotosFromJSONArray(JSONArray(getJsonFromURL(PHOTOS_URL))))
            setRawAlbumsList(getRawAlbumsFromURL(getAlbumIdLimit()))
            setRawUsersList(getRawUsersFromURL(getUserIdLimit()))
            setItemList(initItemsList(getRawPhotosList()!!, getRawAlbumsList()!!))
            setDetailsList(initDetailsList(getRawPhotosList()!!, getRawAlbumsList()!!, getRawUsersList()!!))
            setThumbnailBitmapList(extractBitmapsFromRawPhotos(getRawPhotosList()!!, true))
            setBitmapList(extractBitmapsFromRawPhotos(getRawPhotosList()!!, false))
        }

        fun saveJSONDataToCache(cacheDir:String){
            getRawPhotosList()?.forEach{ a -> writeRawPhotoToCache(cacheDir + "/photo" + a.id.toString(), a) }
            getRawAlbumsList()?.forEach{ a -> writeRawAlbumToCache(cacheDir + "/album" + a.id.toString(), a) }
            getRawUsersList()?.forEach{ a -> writeRawUserToCache(cacheDir + "/user" + a.id.toString(), a) }
            getItemList()?.forEach{ a -> writeListItemToCache(cacheDir + "/listItem" + a.id.toString(), a) }
            getDetailList()?.forEach{ a -> writeDetailToCache(cacheDir + "/detail" + a.photoId.toString(), a) }
        }

        fun readJSONDataFromCache(cacheDir:String){
            setRawPhotosList(readRawPhotosFromCache(cacheDir, getLimitOfPhotos()))
            setRawAlbumsList(readRawAlbumsFromCache(cacheDir, getAlbumIdLimit()))
            setRawUsersList(readRawUsersFromCache(cacheDir, getUserIdLimit()))
            setItemList(readListItemsFromCache(cacheDir, getLimitOfPhotos()))
            setDetailsList(readDetailsFromCache(cacheDir, getLimitOfPhotos()))
        }

        fun saveBitmapListToCache(cacheDir:String, bitmapList:MutableList<Bitmap>, isThumbnail:Boolean){
            for (i in 1.. bitmapList.size){
                var path:String
                if(isThumbnail){
                    path = cacheDir + "/thumbnailBitmap" + i
                }else{
                    path = cacheDir + "/bitmap" + i
                }
                val file = File(path)
                val stream: OutputStream = FileOutputStream(file)
                bitmapList.get(i-1).compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            }
        }

        fun readBitmapListFromCache(cacheDir:String, howMuch:Int, isThumbnail:Boolean):MutableList<Bitmap>{
            val bitmapList:MutableList<Bitmap> = mutableListOf()
            var path:String
            for(i in 1.. howMuch){
                if(isThumbnail){
                    path = cacheDir + "/thumbnailBitmap" + i
                }else{
                    path = cacheDir + "/bitmap" + i
                }
                val bitmap = BitmapFactory.decodeFile(path)
                bitmapList.add(bitmap)
            }
            return bitmapList
        }

        fun readDataFromCache(cacheDir:String):Boolean{
            try{
                readJSONDataFromCache(cacheDir)
                setBitmapList(readBitmapListFromCache(cacheDir, getLimitOfPhotos(), false))
                setThumbnailBitmapList(readBitmapListFromCache(cacheDir, getLimitOfPhotos(), true))
            }catch(e:IOException){
                println(e.message)
            }

            return getThumbnailBitmapList()?.size == getLimitOfPhotos() &&
                    getBitmapList()?.size == getLimitOfPhotos() &&
                    getDetailList()?.size == getLimitOfPhotos()
        }

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo
                .isConnected
        }
    }
}