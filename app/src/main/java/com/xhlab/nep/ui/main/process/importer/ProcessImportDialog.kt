package com.xhlab.nep.ui.main.process.importer

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerDialogFragment
import org.jetbrains.anko.clipboardManager
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.support.v4.longToast
import javax.inject.Inject

class ProcessImportDialog : DaggerDialogFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ProcessImportViewModel
    private lateinit var dialogView: View

    private lateinit var importInputLayout: TextInputLayout
    private lateinit var importEdit: EditText

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireContext().layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_process_import, null)

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_import_process)
            .setView(dialogView)
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
        importInputLayout = dialogView.findViewById(R.id.import_input_layout)
        importEdit = dialogView.findViewById(R.id.import_edit)

        importInputLayout.setEndIconOnClickListener {
            val clipboardManager = requireContext().clipboardManager
            if (clipboardManager.hasPrimaryClip() &&
                clipboardManager.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) == true) {
                val text = clipboardManager.primaryClip!!.getItemAt(0)
                importEdit.setText(text.text)
                longToast(R.string.txt_copied_from_clipboard)
            }
        }

        with (importEdit) {
            addTextChangedListener { viewModel.notifyTextChanged() }
            requestFocus()
        }

        dialog?.setOnShowListener {
            val importButton = (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            importButton.setOnClickListener {
                viewModel.importProcess(importEdit.text.toString())
            }
        }
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.isStringValid.observeNotNull(this) {
            importInputLayout.helperText = when (it) {
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