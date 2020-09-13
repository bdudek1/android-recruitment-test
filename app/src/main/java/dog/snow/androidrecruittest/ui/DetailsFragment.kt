package dog.snow.androidrecruittest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import dog.snow.androidrecruittest.R
import dog.snow.androidrecruittest.SplashActivity.Companion.getBitmapList
import dog.snow.androidrecruittest.ui.model.ListItem

class DetailsFragment : Fragment(R.layout.details_fragment){
    protected lateinit var rootView: View

    companion object {
        const val POSITION: String = "position"
        var TAG = DetailsFragment::class.java.simpleName
        lateinit var clickedItem: ListItem

        fun setSelectedItem(item:ListItem){
            clickedItem = item
        }

        fun newInstance(): DetailsFragment {
            val fragment = DetailsFragment();
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
        rootView = inflater.inflate(R.layout.details_fragment, container, false);
        val img: ImageView = rootView.findViewById(R.id.iv_photo)
        val imgTitle:TextView = rootView.findViewById(R.id.tv_photo_title)
        val albumTitle:TextView = rootView.findViewById(R.id.tv_album_title)
        val userName:TextView = rootView.findViewById(R.id.tv_username)
        val email:TextView = rootView.findViewById(R.id.tv_email)
        val phone:TextView = rootView.findViewById(R.id.tv_phone)

        imgTitle.setText(clickedItem.title)
        albumTitle.setText(clickedItem.albumTitle)
        //img.setImageBitmap(getBitmapList()?.get(clickedItem.id))

        return rootView
    }



}