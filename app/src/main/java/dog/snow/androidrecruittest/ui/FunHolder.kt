package dog.snow.androidrecruittest.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dog.snow.androidrecruittest.SplashActivity
import dog.snow.androidrecruittest.repository.model.RawAlbum
import dog.snow.androidrecruittest.repository.model.RawPhoto
import dog.snow.androidrecruittest.repository.model.RawUser
import dog.snow.androidrecruittest.ui.model.Detail
import dog.snow.androidrecruittest.ui.model.ListItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

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
                    JSONObject(getJsonFromURL(URL)).getJSONObject("address").getJSONObject("geo").getString("lng")
                )

            return rawGeo
        }

        fun getRawAddressFromURL(index:Int): RawUser.RawAddress{
            val URL:String = "https://jsonplaceholder.typicode.com/users/$index"
            val rawAddress:RawUser.RawAddress =
                RawUser.RawAddress(
                    JSONObject(getJsonFromURL(URL)).getJSONObject("address").getString("street"),
                    JSONObject(getJsonFromURL(URL)).getJSONObject("address").getString("suite"),
                    JSONObject(getJsonFromURL(URL)).getJSONObject("address").getString("city"),
                    JSONObject(getJsonFromURL(URL)).getJSONObject("address").getString("zipcode"),
                    getRawGeoFromURL(index)
                )

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
                rawUsersList.add(RawUser(JSONObject(getJsonFromURL(URL)).getInt("id"),
                    JSONObject(getJsonFromURL(URL)).getString("name"),
                    JSONObject(getJsonFromURL(URL)).getString("username"),
                    JSONObject(getJsonFromURL(URL)).getString("email"),
                    getRawAddressFromURL(i),
                    JSONObject(getJsonFromURL(URL)).getString("phone"),
                    JSONObject(getJsonFromURL(URL)).getString("website"),
                    getRawCompanyFromURL(i)))
            }

            return rawUsersList
        }

        fun getRawAlbumsFromURL(size:Int):MutableList<RawAlbum>{
            val rawAlbumsList = mutableListOf<RawAlbum>()
            for(i in 1..size){
                val URL:String = "https://jsonplaceholder.typicode.com/albums/$i"
                rawAlbumsList.add(
                    RawAlbum(JSONObject(getJsonFromURL(URL)).getInt("id"),
                        JSONObject(getJsonFromURL(URL)).getInt("userId"),
                        JSONObject(getJsonFromURL(URL)).getString("title"))
                )
                if(SplashActivity.getUserIdLimit() < JSONObject(getJsonFromURL(URL)).getInt("userId"))
                    SplashActivity.setUserIdLimit(JSONObject(getJsonFromURL(URL)).getInt("userId"))
            }

            return rawAlbumsList
        }

        fun initItemsList(photos:MutableList<RawPhoto>,
                          albums:MutableList<RawAlbum>):MutableList<ListItem>{
            val itemsList:MutableList<ListItem> = mutableListOf()
            for(photo in photos){
                val albumTitle:String = albums.filter{a -> a.id.equals(photo.albumId)}.first().title
                itemsList.add(ListItem(photo.id, photo.title,
                albumTitle, photo.thumbnailUrl))
            }

            return itemsList
        }

        fun initDetailsList(photos:MutableList<RawPhoto>,
                            albums:MutableList<RawAlbum>,
                            users:MutableList<RawUser>):MutableList<Detail>{
            val detailsList:MutableList<Detail> = mutableListOf()
            for(photo in photos){
                val albumTitle:String = albums.filter{a -> a.id.equals(photo.albumId)}.first().title
                val userId:Int = albums.filter{a -> a.id.equals(photo.albumId)}.first().userId
                val user:RawUser = users.filter {u -> u.id.equals(userId)}.first()
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
                rawPhotoList.add(
                    RawPhoto(jsonArray.getJSONObject(i).getInt("id"),
                        jsonArray.getJSONObject(i).getInt("albumId"),
                        jsonArray.getJSONObject(i).getString("title"),
                        jsonArray.getJSONObject(i).getString("url"),
                        jsonArray.getJSONObject(i).getString("thumbnailUrl"))
                )
                if(SplashActivity.getAlbumIdLimit() < jsonArray.getJSONObject(i).getInt("albumId"))
                    SplashActivity.setAlbumIdLimit(jsonArray.getJSONObject(i).getInt("albumId"))
            }

            return rawPhotoList
        }

        fun extractThumbnailBitmapsFromRawPhotos(rawPhotos:MutableList<RawPhoto>):MutableList<Bitmap>{
            val bitmapList:MutableList<Bitmap> = mutableListOf()
            for(photo in rawPhotos){
                val url = URL(photo.thumbnailUrl)
                try{
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    connection.setDoInput(true)
                    connection.connect()
                    val input: InputStream = connection.inputStream
                    val myBitmap = BitmapFactory.decodeStream(input)
                    bitmapList.add(myBitmap)
                    println("SUCCESS")
                }catch(e:IOException){
                    println(e.message)
                    println("FAILURE")
                }
            }

            return bitmapList
        }

        fun extractBitmapsFromRawPhotos(rawPhotos:MutableList<RawPhoto>):MutableList<Bitmap>{
            val bitmapList:MutableList<Bitmap> = mutableListOf()
            for(photo in rawPhotos){
                val url = URL(photo.url)
                try{
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    connection.setDoInput(true)
                    connection.connect()
                    val input: InputStream = connection.inputStream
                    val myBitmap = BitmapFactory.decodeStream(input)
                    bitmapList.add(myBitmap)
                    println("SUCCESS")
                }catch(e:IOException){
                    println(e.message)
                    println("FAILURE")
                }

            }

            return bitmapList
        }
    }
}