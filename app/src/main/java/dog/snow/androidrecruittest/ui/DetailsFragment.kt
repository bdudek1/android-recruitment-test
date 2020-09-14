package dog.snow.androidrecruittest.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import dog.snow.androidrecruittest.R
import dog.snow.androidrecruittest.SplashActivity
import dog.snow.androidrecruittest.SplashActivity.Companion.getBitmapList
import dog.snow.androidrecruittest.SplashActivity.Companion.getDetailList
import dog.snow.androidrecruittest.SplashActivity.Companion.getRawPhotosList
import dog.snow.androidrecruittest.ui.model.Detail
import dog.snow.androidrecruittest.ui.model.ListItem

class DetailsFragment : Fragment(R.layout.details_fragment){
    protected lateinit var rootView: View
    private lateinit var img: ImageView
    private lateinit var imgTitle:TextView
    private lateinit var albumTitle:TextView
    private lateinit var userName:TextView
    private lateinit var email:TextView
    private lateinit var phone:TextView

    companion object {
        const val POSITION: String = "position"
        var TAG = DetailsFragment::class.java.simpleName
        lateinit var clickedItem: ListItem

        fun setSelectedItem(item:ListItem){
            clickedItem = item
        }

        fun newInstance(): DetailsFragment {
            val fragment = DetailsFragment()
            val args = Bundle()
            args.putInt(POSITION, 1)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.details_fragment, container, false)
        initDetailsFragmet()
        return rootView
    }

    private fun initDetailsFragmet(){
        img = rootView.findViewById(R.id.iv_photo)
        imgTitle = rootView.findViewById(R.id.tv_photo_title)
        albumTitle = rootView.findViewById(R.id.tv_album_title)
        userName = rootView.findViewById(R.id.tv_username)
        email = rootView.findViewById(R.id.tv_email)
        phone = rootView.findViewById(R.id.tv_phone)

        val currentDetail: Detail = getDetailList()?.filter{a->a.photoTitle.equals(clickedItem.title)}!!.single()
        imgTitle.setText(clickedItem.title)
        albumTitle.setText(clickedItem.albumTitle)
        userName.setText(currentDetail.username)
        email.setText(currentDetail.email)
        phone.setText(currentDetail.phone)
        val index = getRawPhotosList()?.filter{a->a.title.equals(clickedItem.title)}?.single()!!.id-1
        try{
            img.setImageBitmap(getBitmapList()?.get(index))
        }catch(e:IndexOutOfBoundsException){
            img.setImageDrawable(getResources().getDrawable(R.drawable.ic_placeholder))
            println(e.message)
        }
    }
}