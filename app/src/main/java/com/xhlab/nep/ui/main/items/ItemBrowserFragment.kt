package com.xhlab.nep.ui.main.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.asLiveData
import androidx.paging.LoadState
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentItemBrowserBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.ui.main.items.ElementListener
import com.xhlab.nep.shared.ui.main.items.ItemBrowserViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.ElementDetailActivity.Companion.navigateToElementDetailActivity
import com.xhlab.nep.ui.element.ElementDetailFragment
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ItemBrowserFragment : DaggerFragment, ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentItemBrowserBinding
    private lateinit var viewModel: ItemBrowserViewModel

    private var listener: ElementListener? = null
    private val elementAdapter by lazy { RecipeElementDetailAdapter(listener) }

    constructor() : super()

    constructor(listener: ElementListener) : super() {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemBrowserBinding.inflate(inflater, container, false)
        return binding.root
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

        viewModel.isDBLoaded.asLiveData().observe(this) {
            with(binding) {
                elementList.isGone = !it
                totalText.isGone = !it
                dbNotLoadedText.isGone = it

                with(searchView) {
                    isEnabled = it
                    findViewById<EditText>(R.id.search_src_text).isEnabled = it

                    if (!it) {
                        setQuery("", false)
                        clearFocus()
                    }
                }
            }
            // init
            viewModel.searchElements("")
        }

        viewModel.isIconLoaded.asLiveData().observe(this) { isLoaded ->
            elementAdapter.setIconVisibility(isLoaded)
        }

        viewModel.elementSearchResult.flatMapLatest {
            it.pagingData
        }.asLiveData().observe(this) {
            elementAdapter.submitData(lifecycle, it)
        }

        viewModel.navigateToDetail.asLiveData().observe(this) { elementId ->
            if (resources.getBoolean(R.bool.isTablet)) {
                // clear all fragments, then add new fragment
                with(childFragmentManager) {
                    popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_bottom)
                        .add(R.id.container, ElementDetailFragment.getFragment(elementId))
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                context?.navigateToElementDetailActivity(elementId)
            }
        }
    }

    override fun initView() {
        with(binding.searchView) {
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

        binding.elementList.adapter = elementAdapter

        elementAdapter.addLoadStateListener {
            if (it.refresh is LoadState.NotLoading) {
                binding.totalText.text =
                    String.format(getString(R.string.form_matched_total), elementAdapter.itemCount)
            }
        }

        if (resources.getBoolean(R.bool.isTablet)) {
            val fragmentManager = childFragmentManager
            fragmentManager.addOnBackStackChangedListener {
                binding.stackEmptyText?.isGone = fragmentManager.backStackEntryCount != 0
            }
        }
    }
}
