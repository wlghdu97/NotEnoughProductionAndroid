package com.xhlab.nep.ui.process.editor.selection.subprocess

import android.os.Bundle
import androidx.core.view.isGone
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityProcessSelectionBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class ProcessSelectionActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityProcessSelectionBinding
    private lateinit var viewModel: ProcessSelectionViewModel
    private val processAdapter by lazy { ProcessSelectionAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        binding = ActivityProcessSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setTitle(R.string.title_select_process_to_connect)
        binding.processList.adapter = processAdapter
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(intent?.getSerializableExtra(CONSTRAINT) as? ProcessEditViewModel.ConnectionConstraint)

        viewModel.constraint.observe(this) {
            supportActionBar?.subtitle = it.element.localizedName
        }

        viewModel.processList.observe(this) {
            processAdapter.submitList(it)
            binding.emptyText.isGone = it?.isEmpty() != true
        }

        viewModel.isIconLoaded.observe(this) {
            processAdapter.setIconVisibility(it)
        }

        viewModel.connectionResult.observe(this) {
            if (it.isSuccessful()) {
                finish()
            } else {
                Snackbar.make(
                    binding.root,
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
