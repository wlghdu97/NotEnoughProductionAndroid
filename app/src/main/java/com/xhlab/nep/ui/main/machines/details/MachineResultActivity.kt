package com.xhlab.nep.ui.main.machines.details

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.items.ElementDetailAdapter
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_machine_result.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import javax.inject.Inject

class MachineResultActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MachineResultViewModel
    private val adapter by lazy { ElementDetailAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(intent?.getIntExtra(MACHINE_ID, -1))

        viewModel.machine.observeNotNull(this) {
            supportActionBar?.subtitle = it.name
        }

        viewModel.resultList.observe(this) {
            total_text.text = String.format(getString(R.string.form_total), it?.size ?: 0)
            adapter.submitList(it)
        }
    }

    override fun initView() {
        setContentView(R.layout.activity_machine_result)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_machine_result_list)

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

    companion object {
        const val MACHINE_ID = "machine_id"
    }
}