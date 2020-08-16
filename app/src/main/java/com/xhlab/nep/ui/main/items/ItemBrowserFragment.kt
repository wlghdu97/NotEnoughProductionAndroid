package com.xhlab.nep.ui.main.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.observe
import androidx.paging.PagedList
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.ElementDetailFragment
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_item_browser.*
import javax.inject.Inject

class ItemBrowserFragment : DaggerFragment, ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ItemBrowserViewModel

    private var listener: ElementListener? = null
    private val elementAdapter by lazy { ElementDetailAdapter(listener) }

    constructor() : super()

    constructor(listener: ElementListener) : super() {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_browser, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        if (listener == null) {
            this.listener = viewModel
        }

        viewModel.isDBLoaded.observe(this) {
            element_list.isGone = !it
            total_text.isGone = !it
            db_not_loaded_text.isGone = it

            search_view.isEnabled = it
            search_view.findViewById<EditText>(R.id.search_src_text).isEnabled = it

            if (!it) {
                search_view.setQuery("", false)
                search_view.clearFocus()
            }
            // init
            viewModel.searchElements("")
        }

        viewModel.isIconLoaded.observe(this) { isLoaded ->
            elementAdapter.setIconVisibility(isLoaded)
        }

        viewModel.elementSearchResult.observeNotNull(this) {
            submitSearchResultList(it)
        }

        viewModel.navigateToDetail.observe(this) {
            if (resources.getBoolean(R.bool.isTablet)) {
                // clear all fragments, then add new fragment
                with (childFragmentManager) {
                    popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_bottom)
                        .add(R.id.container, ElementDetailFragment.getFragment(it))
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                viewModel.navigateToElementDetail(it)
            }
        }
    }

    override fun initView() {
        with (search_view) {
            setIconifiedByDefault(false)
            queryHint = getString(R.string.hint_search_element)

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.searchElements(newText ?: "")
                    return true
                }
            })
        }

        element_list.adapter = elementAdapter

        if (resources.getBoolean(R.bool.isTablet)) {
            val fragmentManager = childFragmentManager
            fragmentManager.addOnBackStackChangedListener {
                stack_empty_text?.isGone = fragmentManager.backStackEntryCount != 0
            }
        }
    }

    private fun submitSearchResultList(list: PagedList<ElementView>?) {
        total_text.text = String.format(getString(R.string.form_matched_total), list?.size ?: 0)
        elementAdapter.submitList(list)
    }
}