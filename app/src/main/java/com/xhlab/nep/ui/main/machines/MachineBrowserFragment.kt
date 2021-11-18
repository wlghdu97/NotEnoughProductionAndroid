package com.xhlab.nep.ui.main.machines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentMachineBrowserBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.ui.main.machines.MachineBrowserViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.machines.details.MachineResultActivity.Companion.navigateToMachineResultActivity
import com.xhlab.nep.ui.main.machines.details.MachineResultFragment
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class MachineBrowserFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentMachineBrowserBinding
    private lateinit var viewModel: MachineBrowserViewModel
    private val machineAdapter by lazy { MachineAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMachineBrowserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.isDBLoaded.asLiveData().observe(this) {
            binding.machineList.isGone = !it
            binding.dbNotLoadedText.isGone = it
        }

        viewModel.machineList.flatMapLatest {
            it.pagingData
        }.asLiveData().observe(this) {
            machineAdapter.submitData(lifecycle, it)
        }

        viewModel.navigateToMachineResult.asLiveData().observe(this) {
            if (resources.getBoolean(R.bool.isTablet)) {
                // clear all fragments, then add new fragment
                with(childFragmentManager) {
                    popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_bottom)
                        .add(R.id.container, MachineResultFragment.getFragment(it))
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                context?.navigateToMachineResultActivity(it)
            }
        }
    }

    override fun initView() {
        binding.machineList.adapter = machineAdapter

        if (resources.getBoolean(R.bool.isTablet)) {
            val fragmentManager = childFragmentManager
            fragmentManager.addOnBackStackChangedListener {
                binding.stackEmptyText?.isGone = fragmentManager.backStackEntryCount != 0
            }
        }
    }
}
