package dog.snow.androidrecruittest

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.MaterialToolbar
import dog.snow.androidrecruittest.ui.ListFragment

class MainActivity : AppCompatActivity(R.layout.main_activity){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                    supportFragmentManager.beginTransaction()
                                  .replace(R.id.container, ListFragment.newInstance(), ListFragment.TAG)
                                  .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                  .commit()
    }
}