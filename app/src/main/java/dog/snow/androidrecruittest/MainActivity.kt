package dog.snow.androidrecruittest

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.AppBarLayout
import dog.snow.androidrecruittest.ui.ConnectivityReceiver
import dog.snow.androidrecruittest.ui.ListFragment


class MainActivity : AppCompatActivity(R.layout.main_activity), ConnectivityReceiver.ConnectivityReceiverListener{
    lateinit var bannerOffline:TextView
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        val toolbar = findViewById<AppBarLayout>(R.id.toolbar)
        bannerOffline = toolbar.findViewById<TextView>(R.id.banner)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ListFragment.newInstance(), ListFragment.TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(isConnected){
            bannerOffline.visibility = View.GONE
        }else{
            bannerOffline.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }
}