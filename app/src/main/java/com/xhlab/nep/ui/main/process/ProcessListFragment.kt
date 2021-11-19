package com.xhlab.nep.ui.main.process

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentProcessListBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.ui.main.process.ProcessListViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.adapters.ProcessAdapter
import com.xhlab.nep.ui.main.process.creator.ProcessCreationDialog
import com.xhlab.nep.ui.main.process.export.ProcessExportDialog
import com.xhlab.nep.ui.main.process.export.ProcessExportDialog.Companion.EXPORTED_STRING
import com.xhlab.nep.ui.main.process.importer.ProcessImportDialog
import com.xhlab.nep.ui.main.process.rename.ProcessRenameDialog
import com.xhlab.nep.ui.main.process.rename.ProcessRenameDialog.Companion.PROCESS_ID
import com.xhlab.nep.ui.main.process.rename.ProcessRenameDialog.Companion.PROCESS_NAME
import com.xhlab.nep.ui.process.editor.ProcessEditActivity.Companion.navigateToProcessEditActivity
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ProcessListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentProcessListBinding
    private lateinit var viewModel: ProcessListViewModel

    private val processAdapter by lazy { ProcessAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProcessListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        binding.processList.adapter = processAdapter

        binding.fabAdd.setOnClickListener {
            showProcessCreationSelectionAlert()
        }
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.isIconLoaded.asLiveData().observe(this) {
            processAdapter.setIconVisibility(it)
        }

        viewModel.processList.flatMapLatest {
            it.pagingData
        }.asLiveData().observe(this) {
            processAdapter.submitData(lifecycle, it)
        }

        viewModel.renameProcess.asLiveData().observe(this) { (processId, name) ->
            showProcessRenameDialog(processId, name)
        }

        viewModel.showExportStringDialog.asLiveData().observe(this) {
            showExportStringDialog(it)
        }

        viewModel.showExportFailedMessage.asLiveData().observe(this) {
            Snackbar.make(
                binding.root,
                R.string.error_failed_to_export_string,
                Snackbar.LENGTH_LONG
            ).show()
        }

        viewModel.deleteProcess.asLiveData().observe(this) { (processId, name) ->
            showProcessRemovalDialog(processId, name)
        }

        viewModel.navigateToProcessEdit.asLiveData().observe(this) { processId ->
            context?.navigateToProcessEditActivity(processId)
        }
    }

    private fun showProcessCreationSelectionAlert() {
        MaterialAlertDialogBuilder(context)
            .setItems(R.array.process_creation) { _, index ->
                when (index) {
                    0 -> showProcessCreationDialog()
                    1 -> showProcessImportDialog()
                }
            }
            .show()
    }

    private fun showProcessCreationDialog() {
        ProcessCreationDialog().show(childFragmentManager, PROCESS_CREATION_TAG)
    }

    private fun showProcessImportDialog() {
        ProcessImportDialog().show(childFragmentManager, PROCESS_IMPORT_TAG)
    }

    private fun showProcessRenameDialog(processId: String, name: String) {
        ProcessRenameDialog().apply {
            arguments = Bundle().apply {
                putString(PROCESS_ID, processId)
                putString(PROCESS_NAME, name)
            }
        }.show(childFragmentManager, PROCESS_RENAME_TAG)
    }

    private fun showExportStringDialog(string: String) {
        ProcessExportDialog().apply {
            arguments = Bundle().apply {
                putString(EXPORTED_STRING, string)
            }
        }.show(childFragmentManager, PROCESS_EXPORT_TAG)
    }

    private fun showProcessRemovalDialog(processId: String, name: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle(String.format(getString(R.string.title_delete_process), name))
            .setMessage(R.string.txt_action_cannot_be_undone)
            .setPositiveButton(R.string.btn_delete) { _, _ -> viewModel.deleteProcess(processId) }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    companion object {
        private const val PROCESS_CREATION_TAG = "process_creation_tag"
        private const val PROCESS_IMPORT_TAG = "process_import_tag"
        private const val PROCESS_RENAME_TAG = "process_rename_tag"
        private const val PROCESS_EXPORT_TAG = "process_export_tag"
    }
}
