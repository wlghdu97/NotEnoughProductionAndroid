package com.xhlab.nep.ui.main.machines.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.paging.LoadState
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentMachineResultBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.ui.main.machines.details.MachineResultViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.ElementDetailActivity.Companion.navigateToElementDetailActivity
import com.xhlab.nep.ui.element.ElementDetailFragment
import com.xhlab.nep.ui.main.items.RecipeElementDetailAdapter
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class MachineResultFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentMachineResultBinding
    private lateinit var viewModel: MachineResultViewModel
    private val adapter by lazy { RecipeElementDetailAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMachineResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(arguments?.getInt(MACHINE_ID) ?: -1)

        viewModel.machine.asLiveData().observe(this) {
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = it.name
        }

        viewModel.resultList.flatMapLatest {
            it.pagingData
        }.asLiveData().observe(this) {
            adapter.submitData(lifecycle, it)
        }

        viewModel.isIconLoaded.asLiveData().observe(this) { isLoaded ->
            adapter.setIconVisibility(isLoaded)
        }

        viewModel.navigateToDetail.asLiveData().observe(this) { elementId ->
            if (resources.getBoolean(R.bool.isTablet)) {
                // clear all fragments, then add new fragment
                requireParentFragment().childFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_bottom)
                    .add(R.id.container, ElementDetailFragment.getFragment(elementId))
                    .addToBackStack(null)
                    .commit()
            } else {
                context?.navigateToElementDetailActivity(elementId)
            }
        }
    }

    override fun initView() {
        (activity as? AppCompatActivity)?.let {
            it.setSupportActionBar(binding.toolbarLayout.toolbar)
            it.supportActionBar?.let { actionBar ->
                actionBar.title = getString(R.string.title_machine_result_list)
                if (resources.getBoolean(R.bool.isTablet)) {
                    setHasOptionsMenu(true)
                    actionBar.setDisplayHomeAsUpEnabled(true)
                }
            }
        }

        with(binding.searchView) {
            setIconifiedByDefault(false)
            queryHint = getString(R.string.hint_search_element)

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.searchResults(newText ?: "")
                    return true
                }
            })
        }

        binding.resultList.adapter = adapter

        adapter.addLoadStateListener {
            if (it.refresh is LoadState.NotLoading) {
                binding.totalText.text =
                    String.format(getString(R.string.form_total), adapter.itemCount)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireFragmentManager().popBackStack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val MACHINE_ID = "machine_id"

        fun getFragment(machineId: Int): Fragment {
            return MachineResultFragment().apply {
                arguments = Bundle().apply {
                    putInt(MACHINE_ID, machineId)
                }
            }
        }
    }
}
