package com.xhlab.nep.ui.main.machines.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.domain.MachineResultNavigationUseCase
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.ElementDetailFragment
import com.xhlab.nep.ui.main.items.ElementDetailAdapter
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_machine_result.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import javax.inject.Inject

class MachineResultFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MachineResultViewModel
    private val adapter by lazy { ElementDetailAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_machine_result, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(arguments?.getInt(MACHINE_ID) ?: -1)

        viewModel.machine.observeNotNull(this) {
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = it.name
        }

        viewModel.resultList.observe(this) {
            total_text.text = String.format(getString(R.string.form_total), it?.size ?: 0)
            adapter.submitList(it)
        }

        viewModel.isIconLoaded.observe(this) { isLoaded ->
            adapter.setIconVisibility(isLoaded)
        }

        viewModel.navigateToDetail.observe(this) {
            if (resources.getBoolean(R.bool.isTablet)) {
                // clear all fragments, then add new fragment
                requireParentFragment().childFragmentManager.beginTransaction()
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
        (activity as? AppCompatActivity)?.let {
            it.setSupportActionBar(toolbar)
            it.supportActionBar?.let { actionBar ->
                actionBar.title = getString(R.string.title_machine_result_list)
                if (resources.getBoolean(R.bool.isTablet)) {
                    setHasOptionsMenu(true)
                    actionBar.setDisplayHomeAsUpEnabled(true)
                }
            }
        }

        with (search_view) {
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

        result_list.adapter = adapter
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

        fun getBundle(params: MachineResultNavigationUseCase.Parameter): Bundle {
            return Bundle().apply {
                putInt(MACHINE_ID, params.machineId)
            }
        }

        fun getFragment(params: MachineResultNavigationUseCase.Parameter): Fragment {
            return MachineResultFragment().apply { arguments = getBundle(params) }
        }
    }
}