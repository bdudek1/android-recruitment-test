package dog.snow.androidrecruittest

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dog.snow.androidrecruittest.SplashActivity.Companion.getBitmapList
import dog.snow.androidrecruittest.SplashActivity.Companion.getLimitOfPhotos
import dog.snow.androidrecruittest.SplashActivity.Companion.loadAndSaveData
import dog.snow.androidrecruittest.ui.ListFragment
import java.util.concurrent.RejectedExecutionException


class MainActivity : AppCompatActivity(R.layout.main_activity), ConnectivityReceiver.ConnectivityReceiverListener{
    private lateinit var bannerOffline:TextView
    private lateinit var showErrorTimer: CountDownTimer
    private val TIME_TO_RETRY_CONNECTION:Long = 600000
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        val toolbar = findViewById<AppBarLayout>(R.id.toolbar)
        bannerOffline = toolbar.findViewById(R.id.banner)
        initShowErrorTimer()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ListFragment.newInstance(), ListFragment.TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(isConnected){
            bannerOffline.visibility = View.GONE
            showErrorTimer.cancel()
            reloadDataIfNotLoaded()
        }else{
            bannerOffline.visibility = View.VISIBLE
            showErrorTimer.start()
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun reloadDataIfNotLoaded(){
        if(getBitmapList()?.size!! < getLimitOfPhotos())
            try{
                loadAndSaveData()
            }catch(e:RejectedExecutionException){
                println(e.message)
            }
    }

    fun initShowErrorTimer(){
        showErrorTimer = object: CountDownTimer(TIME_TO_RETRY_CONNECTION, TIME_TO_RETRY_CONNECTION) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                showError("Please make sure that you are connected to the internet.",
                    { tryToReconnect() })
            }
        }
    }

    private fun showError(errorMessage: String?,  lambda: () -> Unit) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.cant_download_dialog_title)
            .setMessage(getString(R.string.cant_download_dialog_message, errorMessage))
            .setPositiveButton(R.string.cant_download_dialog_btn_positive) { _, _ -> lambda() }
            .setNegativeButton(R.string.cant_download_dialog_btn_negative) { _, _ -> finish() }
            .create()
            .apply { setCanceledOnTouchOutside(false) }
            .show()
    }

    private fun tryToReconnect(){
        if(!FunHolder.isNetworkAvailable(applicationContext))
            showError("Please make sure that you are connected to the internet.",
                { tryToReconnect() })
    }
}