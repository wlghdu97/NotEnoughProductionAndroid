package com.xhlab.nep.ui.main.process.importer

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.databinding.DialogProcessImportBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.util.longToast
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class ProcessImportDialog : DaggerDialogFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: DialogProcessImportBinding
    private lateinit var viewModel: ProcessImportViewModel

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogProcessImportBinding.inflate(layoutInflater, null, false)

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_import_process)
            .setView(binding.root)
            .setPositiveButton(R.string.btn_import, null)
            .setNegativeButton(R.string.btn_close, null)
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        binding.importInputLayout.setEndIconOnClickListener {
            val clipboardManager = requireContext()
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboardManager.hasPrimaryClip() &&
                clipboardManager.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) == true) {
                val text = clipboardManager.primaryClip!!.getItemAt(0)
                binding.importEdit.setText(text.text)
                longToast(R.string.txt_copied_from_clipboard)
            }
        }

        with (binding.importEdit) {
            addTextChangedListener { viewModel.notifyTextChanged() }
            requestFocus()
        }

        dialog?.setOnShowListener {
            val importButton = (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            importButton.setOnClickListener {
                viewModel.importProcess(binding.importEdit.text.toString())
            }
        }
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.isStringValid.observeNotNull(this) {
            binding.importInputLayout.helperText = when (it) {
                true -> ""
                false -> getString(R.string.txt_invalid_process_string)
            }
        }

        viewModel.importResult.observe(this) {
            if (it.isSuccessful()) {
                longToast(R.string.txt_import_successful)
                dismiss()
            }
        }
    }
}