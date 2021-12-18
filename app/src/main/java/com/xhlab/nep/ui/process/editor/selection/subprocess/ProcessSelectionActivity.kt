package com.xhlab.nep.ui.process.editor.selection.subprocess

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isGone
import androidx.lifecycle.asLiveData
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityProcessSelectionBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.model.defaultJson
import com.xhlab.nep.shared.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.shared.ui.process.editor.selection.subprocess.ProcessSelectionViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
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

        processAdapter.addLoadStateListener {
            if (it.refresh is LoadState.NotLoading) {
                binding.emptyText.isGone = processAdapter.itemCount > 0
            }
        }
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        val constraint = intent?.getStringExtra(RecipeSelectionActivity.CONSTRAINT)?.let {
            defaultJson.decodeFromString<ProcessEditViewModel.ConnectionConstraint>(it)
        }
        viewModel.init(constraint)

        viewModel.constraint.asLiveData().observe(this) {
            supportActionBar?.subtitle = it.element.localizedName
        }

        viewModel.processList.flatMapLatest {
            it.pagingData
        }.asLiveData().observe(this) {
            processAdapter.submitData(lifecycle, it)
        }

        viewModel.isIconLoaded.asLiveData().observe(this) {
            processAdapter.setIconVisibility(it)
        }

        viewModel.connectionErrorMessage.asLiveData().observe(this) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }

        viewModel.finish.asLiveData().observe(this) {
            finish()
        }
    }

    companion object {
        private const val CONSTRAINT = "constraint"

        fun Context.navigateToProcessSelectionActivity(constraint: ProcessEditViewModel.ConnectionConstraint) {
            startActivity(Intent(this, ProcessSelectionActivity::class.java).apply {
                putExtra(CONSTRAINT, defaultJson.encodeToString(constraint))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
