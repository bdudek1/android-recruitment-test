package dog.snow.androidrecruittest.ui

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.*
import com.google.android.material.textfield.TextInputEditText
import dog.snow.androidrecruittest.R
import dog.snow.androidrecruittest.SplashActivity.Companion.getItemList
import dog.snow.androidrecruittest.ui.adapter.ListAdapter
import dog.snow.androidrecruittest.ui.model.ListItem


class ListFragment : Fragment(R.layout.list_fragment){
    private lateinit var rootView: View
    private lateinit var emptyString:TextView
    private lateinit var photosView:RecyclerView

    companion object {
        val POSITION: String = "position"
        var TAG = ListFragment::class.java.simpleName
        private lateinit var viewAdapter: dog.snow.androidrecruittest.ui.adapter.ListAdapter
        private lateinit var viewManager: androidx.recyclerview.widget.LinearLayoutManager
        private lateinit var searchText: TextInputEditText

        fun newInstance(): ListFragment {
            val fragment = ListFragment()
            val args = Bundle()
            args.putInt(POSITION, 1)
            fragment.arguments = args
            return fragment
        }

        fun submitListIncludingFilter(){
            val filterText:String = searchText.text.toString()
            viewAdapter.submitList(getItemList()?.filter{
                    a -> a.albumTitle.contains(filterText) ||
                    a.title.contains(filterText)})
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.list_fragment, container, false)
        initListFragment()
        initListeners()
        return rootView
    }

    private fun initListFragment(){
        viewManager = LinearLayoutManager(this.context)
        emptyString = rootView.findViewById<TextView>(R.id.tv_empty)
        emptyString.visibility = View.GONE
        searchText = rootView.findViewById<TextInputEditText>(R.id.et_search)
        photosView = rootView.findViewById<RecyclerView>(R.id.rv_items)
        photosView.layoutManager = LinearLayoutManager(activity)
        photosView.itemAnimator = DefaultItemAnimator()
        val itemDecorator:DividerItemDecoration = DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        photosView.addItemDecoration(itemDecorator)
        photosView.setHasFixedSize(false)
    }

    private fun initListeners(){
        viewAdapter = dog.snow.androidrecruittest.ui.adapter.ListAdapter{
                item: ListItem, position: Int, view: View ->
            DetailsFragment.setSelectedItem(item)
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, DetailsFragment.newInstance(), DetailsFragment.TAG)
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ?.addToBackStack(null)?.commit()

        }
        viewAdapter.submitList(getItemList())
        viewAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (photosView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })
        photosView.adapter = viewAdapter

        searchText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(viewAdapter.currentList.size == 0){
                    emptyString.visibility = View.VISIBLE
                }else{
                    emptyString.visibility = View.GONE
                }
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                submitListIncludingFilter()
            }
        })
    }
}