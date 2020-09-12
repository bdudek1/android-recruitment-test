package dog.snow.androidrecruittest.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import dog.snow.androidrecruittest.R
import dog.snow.androidrecruittest.SplashActivity
import dog.snow.androidrecruittest.ui.model.ListItem

class ListFragment : Fragment(R.layout.list_fragment){
    private lateinit var rootView: View

    private lateinit var viewAdapter: dog.snow.androidrecruittest.ui.adapter.ListAdapter
    private lateinit var viewManager: androidx.recyclerview.widget.LinearLayoutManager

    companion object {
        val POSITION: String = "position"
        var TAG = ListFragment::class.java.simpleName

        fun newInstance(): ListFragment {
            val fragment = ListFragment()
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
        rootView = inflater.inflate(R.layout.list_fragment, container, false)
        viewManager = LinearLayoutManager(this.context)
        viewAdapter = dog.snow.androidrecruittest.ui.adapter.ListAdapter{
                item: ListItem, position: Int, view: View -> println("clicked $position")
        }
        val searchText = rootView.findViewById<TextInputEditText>(R.id.et_search)
        val photosView = rootView.findViewById<RecyclerView>(R.id.rv_items)
        photosView.layoutManager = LinearLayoutManager(activity)
        photosView.adapter = viewAdapter
        photosView.itemAnimator = DefaultItemAnimator()
        photosView.setHasFixedSize(false)
        viewAdapter.submitList(SplashActivity.getItemList())
        searchText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                //if(SplashActivity.itemsList!!.size > 0)
                //SplashActivity.itemsList?.removeAt(1);
               //viewAdapter.submitList(null)
                viewAdapter.submitList(SplashActivity.getItemList())
                println(viewAdapter.currentList)
                println(viewAdapter.currentList.size)
            }
        })
        return rootView
    }
}