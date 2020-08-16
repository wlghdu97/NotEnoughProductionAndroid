package com.xhlab.nep.ui.main.machines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.machines.details.MachineResultFragment
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_machine_browser.*
import javax.inject.Inject

class MachineBrowserFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MachineBrowserViewModel
    private val machineAdapter by lazy { MachineAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_machine_browser, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.isDBLoaded.observe(this) {
            machine_list.isGone = !it
            db_not_loaded_text.isGone = it
        }

        viewModel.machineList.observe(this) {
            machineAdapter.submitList(it)
        }

        viewModel.navigateToMachineResult.observe(this) {
            if (resources.getBoolean(R.bool.isTablet)) {
                // clear all fragments, then add new fragment
                with (childFragmentManager) {
                    popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_bottom)
                        .add(R.id.container, MachineResultFragment.getFragment(it))
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                viewModel.navigateToMachineResult(it)
            }
        }
    }

    override fun initView() {
        machine_list.adapter = machineAdapter

        if (resources.getBoolean(R.bool.isTablet)) {
            val fragmentManager = childFragmentManager
            fragmentManager.addOnBackStackChangedListener {
                stack_empty_text?.isGone = fragmentManager.backStackEntryCount != 0
            }
        }
    }
}