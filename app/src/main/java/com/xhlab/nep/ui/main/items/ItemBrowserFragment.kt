package com.xhlab.nep.ui.main.items

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.lifecycle.observe
import androidx.paging.PagedList
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.domain.item.model.ElementView
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_item_browser.*
import javax.inject.Inject

class ItemBrowserFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ItemBrowserViewModel

    private val elementAdapter by lazy { ElementDetailAdapter(viewModel) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initViewModel()
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
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

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
    }

    private fun submitSearchResultList(list: PagedList<ElementView>?) {
        total_text.text = String.format(getString(R.string.form_matched_total), list?.size ?: 0)
        elementAdapter.submitList(list)
    }
}