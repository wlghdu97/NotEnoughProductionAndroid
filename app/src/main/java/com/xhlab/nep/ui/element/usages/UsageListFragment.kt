package com.xhlab.nep.ui.element.usages

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import androidx.paging.LoadState
import com.google.android.material.tabs.TabLayout
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentUsageListBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.ElementDetailFragment
import com.xhlab.nep.ui.main.items.RecipeElementDetailAdapter
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class UsageListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentUsageListBinding
    private lateinit var viewModel: UsageListViewModel

    private val usageAdapter by lazy { RecipeElementDetailAdapter(viewModel) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsageListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(arguments?.getLong(ElementDetailFragment.ELEMENT_ID))

        viewModel.usageList.flatMapLatest {
            it.pagingData
        }.asLiveData().observe(this) {
            usageAdapter.submitData(lifecycle, it)
        }

        viewModel.isIconLoaded.asLiveData().observe(this) { isLoaded ->
            usageAdapter.setIconVisibility(isLoaded)
        }

        viewModel.navigateToDetail.asLiveData().observe(this) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val parent = requireParentFragment().requireParentFragment()
                parent.childFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_bottom)
                    .add(R.id.container, ElementDetailFragment.getFragment(it))
                    .addToBackStack(null)
                    .commit()
            } else {
                viewModel.navigateToElementDetail(it)
            }
        }
    }

    override fun initView() {
        binding.usageList.adapter = usageAdapter

        usageAdapter.addLoadStateListener {
            if (it.refresh is LoadState.NotLoading) {
                val tabLayout = parentFragment?.view?.findViewById<TabLayout>(R.id.tab_layout)
                tabLayout?.getTabAt(1)?.text = requireContext().formatString(
                    R.string.form_tab_usages,
                    usageAdapter.itemCount
                )
            }
        }
    }
}
