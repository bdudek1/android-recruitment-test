package dog.snow.androidrecruittest

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity(R.layout.main_activity){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarText = toolbar.findViewById<MaterialToolbar>(R.id.toolbar_title)
        setSupportActionBar(toolbar)
    }
}