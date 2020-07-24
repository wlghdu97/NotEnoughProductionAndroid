package com.xhlab.nep.ui.process.editor.selection.subprocess

import android.os.Bundle
import androidx.core.view.isGone
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_process_selection.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import javax.inject.Inject

class ProcessSelectionActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ProcessSelectionViewModel
    private val processAdapter by lazy { ProcessSelectionAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_selection)
        initViewModel()
        initView()
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_select_process_to_connect)
        process_list.adapter = processAdapter
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(intent?.getSerializableExtra(CONSTRAINT) as? ProcessEditViewModel.ConnectionConstraint)

        viewModel.constraint.observe(this) {
            supportActionBar?.subtitle = it.element.localizedName
        }

        viewModel.processList.observe(this) {
            processAdapter.submitList(it)
            empty_text.isGone = it?.isEmpty() != true
        }

        viewModel.isIconLoaded.observe(this) {
            processAdapter.setIconVisibility(it)
        }

        viewModel.connectionResult.observe(this) {
            if (it.isSuccessful()) {
                finish()
            } else {
                Snackbar.make(
                    root,
                    R.string.error_connect_recipe_failed,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        const val CONSTRAINT = "constraint"
    }
}