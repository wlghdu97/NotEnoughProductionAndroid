package com.xhlab.nep.ui.main.process.editor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_process_edit.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import javax.inject.Inject

class ProcessEditActivity :
    DaggerAppCompatActivity(),
    ProcessTreeAdapter.ProcessTreeListener,
    ViewInit
{
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ProcessEditViewModel

    private val processTreeAdapter by lazy { ProcessTreeAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_edit)
        initViewModel()
        initView()
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_process_editor)

        process_tree.adapter = processTreeAdapter
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(intent?.getStringExtra(PROCESS_ID))

        viewModel.process.observeNotNull(this) {
            supportActionBar?.subtitle = it.name
            processTreeAdapter.submitProcess(it)
        }

        viewModel.isIconLoaded.observe(this) {
            processTreeAdapter.setIconVisible(it)
        }

        viewModel.iconMode.observe(this) {
            processTreeAdapter.setShowConnection(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.process_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_swap_icons -> {
                viewModel.toggleIconMode()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onProcessTreeExpanded(position: Int) {
        process_tree.smoothScrollToPosition(position)
    }

    companion object {
        const val PROCESS_ID = "process_id"
    }
}