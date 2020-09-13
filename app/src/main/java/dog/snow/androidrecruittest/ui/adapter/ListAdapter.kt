package dog.snow.androidrecruittest.ui.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dog.snow.androidrecruittest.R
import dog.snow.androidrecruittest.SplashActivity
import dog.snow.androidrecruittest.SplashActivity.Companion.getRawPhotosList
import dog.snow.androidrecruittest.SplashActivity.Companion.getThumbnailBitmapList
import dog.snow.androidrecruittest.ui.model.ListItem
import java.io.IOException
import java.lang.IndexOutOfBoundsException
import java.net.URL


class ListAdapter(private val onClick: (item: ListItem, position: Int, view: View) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<ListItem, ListAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(itemView, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class ViewHolder(
        itemView: View,
        private val onClick: (item: ListItem, position: Int, view: View) -> Unit
    ) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(item: ListItem) = with(itemView) {
            val ivThumb: ImageView = findViewById(R.id.iv_thumb)
            val tvTitle: TextView = findViewById(R.id.tv_photo_title)
            val tvAlbumTitle: TextView = findViewById(R.id.tv_album_title)
            tvTitle.text = item.title
            tvAlbumTitle.text = item.albumTitle
            val index:Int = getRawPhotosList()?.filter{a -> a.title.equals(item.title)}?.first()!!.id-1
                try{
                    ivThumb.setImageBitmap(getThumbnailBitmapList()?.get(index))
                }catch(e:IndexOutOfBoundsException){
                    println(e.message)
                }

                setOnClickListener { onClick(item, adapterPosition, this) }
        }
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                oldItem == newItem
        }
    }
}