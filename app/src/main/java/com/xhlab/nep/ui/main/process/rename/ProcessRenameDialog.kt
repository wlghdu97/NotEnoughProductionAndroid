package com.xhlab.nep.ui.main.process.rename

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.databinding.DialogProcessRenameBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.util.longToast
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class ProcessRenameDialog : DaggerDialogFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: DialogProcessRenameBinding
    private lateinit var viewModel: ProcessRenameViewModel

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogProcessRenameBinding.inflate(layoutInflater, null, false)

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_rename_process)
            .setView(binding.root)
            .setPositiveButton(R.string.btn_rename, null)
            .setNegativeButton(R.string.btn_cancel, null)
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        with (binding.nameEdit) {
            addTextChangedListener { viewModel.changeName(it.toString()) }
            requestFocus()
        }

        dialog?.setOnShowListener {
            val createButton = (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            createButton.setOnClickListener {
                viewModel.renameProcess()
            }
        }
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        val processId = arguments?.getString(PROCESS_ID)
        val processName = arguments?.getString(PROCESS_NAME)
        viewModel.init(processId, processName)

        viewModel.name.observeNotNull(this) {
            with(binding.nameEdit) {
                if (text.toString() != it) {
                    setText(it)
                    setSelection(it.length)
                }
            }
        }

        viewModel.isNameValid.observeNotNull(this) {
            binding.nameInputLayout.helperText = when (it) {
                true -> ""
                false -> getString(R.string.txt_name_empty)
            }
        }

        viewModel.renameResult.observe(this) {
            when {
                it.isSuccessful() -> dismiss()
                else -> longToast(R.string.error_failed_to_rename_process)
            }
        }
    }

    companion object {
        const val PROCESS_ID = "process_id"
        const val PROCESS_NAME = "process_name"
    }
}