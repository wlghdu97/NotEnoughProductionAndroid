package com.xhlab.nep.ui.main.process

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.process.creator.ProcessCreationDialog
import com.xhlab.nep.ui.main.process.rename.ProcessRenameDialog
import com.xhlab.nep.ui.main.process.rename.ProcessRenameDialog.Companion.PROCESS_ID
import com.xhlab.nep.ui.main.process.rename.ProcessRenameDialog.Companion.PROCESS_NAME
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_process_list.*
import javax.inject.Inject

class ProcessListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ProcessListViewModel

    private val processAdapter by lazy { ProcessAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_process_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        process_list.adapter = processAdapter

        fab_add.setOnClickListener {
            showProcessCreationDialog()
        }
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.isIconLoaded.observe(this) {
            processAdapter.setIconVisibility(it)
        }

        viewModel.processList.observe(this) {
            processAdapter.submitList(it)
        }

        viewModel.renameProcess.observe(this) { (processId, name) ->
            showProcessRenameDialog(processId, name)
        }

        viewModel.exportProcess.observe(this) {

        }

        viewModel.deleteProcess.observe(this) { (processId, name) ->
            showProcessRemovalDialog(processId, name)
        }
    }

    private fun showProcessCreationDialog() {
        ProcessCreationDialog().show(childFragmentManager, PROCESS_CREATION_TAG)
    }

    private fun showProcessRenameDialog(processId: String, name: String) {
        ProcessRenameDialog().apply {
            arguments = Bundle().apply {
                putString(PROCESS_ID, processId)
                putString(PROCESS_NAME, name)
            }
        }.show(childFragmentManager, PROCESS_RENAME_TAG)
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
        private const val PROCESS_RENAME_TAG = "process_rename_tag"
    }
}