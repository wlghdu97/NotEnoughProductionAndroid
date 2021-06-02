package com.xhlab.nep.ui.element.usages

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import com.google.android.material.tabs.TabLayout
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentUsageListBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.ElementDetailFragment
import com.xhlab.nep.ui.main.items.ElementDetailAdapter
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class UsageListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentUsageListBinding
    private lateinit var viewModel: UsageListViewModel

    private val usageAdapter by lazy { ElementDetailAdapter(viewModel) }

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

        // update parent tab item title
        viewModel.usageList.observe(this) {
            val tabLayout = parentFragment?.view?.findViewById<TabLayout>(R.id.tab_layout)
            tabLayout?.getTabAt(1)?.text = requireContext().formatString(
                R.string.form_tab_usages,
                it?.size ?: 0
            )
        }

        viewModel.usageList.observe(this) {
            usageAdapter.submitList(it)
        }

        viewModel.isIconLoaded.observe(this) { isLoaded ->
            usageAdapter.setIconVisibility(isLoaded)
        }

        viewModel.navigateToDetail.observe(this) {
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
    }
}