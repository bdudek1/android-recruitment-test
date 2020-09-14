package dog.snow.androidrecruittest

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.AppBarLayout
import dog.snow.androidrecruittest.SplashActivity.Companion.getLimitOfPhotos
import dog.snow.androidrecruittest.SplashActivity.Companion.getThumbnailBitmapList
import dog.snow.androidrecruittest.ui.ConnectivityReceiver
import dog.snow.androidrecruittest.ui.ListFragment


class MainActivity : AppCompatActivity(R.layout.main_activity), ConnectivityReceiver.ConnectivityReceiverListener{
    private lateinit var bannerOffline:TextView
    private lateinit var showErrorTimer: CountDownTimer
    private val TIME_TO_RETRY_CONNECTION:Long = 6000
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        val toolbar = findViewById<AppBarLayout>(R.id.toolbar)
        bannerOffline = toolbar.findViewById<TextView>(R.id.banner)
        initShowErrorTimer()

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ListFragment.newInstance(), ListFragment.TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    fun initShowErrorTimer(){
        showErrorTimer = object: CountDownTimer(TIME_TO_RETRY_CONNECTION, TIME_TO_RETRY_CONNECTION) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                Toast.makeText(this@MainActivity, "SHOW ERROR", Toast.LENGTH_LONG).show()
                //TODO:: showError
            }
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(isConnected){
            bannerOffline.visibility = View.GONE
            showErrorTimer.cancel()
            if(getThumbnailBitmapList()?.size!! < getLimitOfPhotos()){}
                //TODO:: loadJSONDataUsingExecutors

        }else{
            bannerOffline.visibility = View.VISIBLE
            showErrorTimer.start()
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }


}