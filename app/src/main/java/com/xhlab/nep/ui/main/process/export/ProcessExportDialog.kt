package com.xhlab.nep.ui.main.process.export

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.databinding.DialogProcessExportBinding
import com.xhlab.nep.util.longToast

class ProcessExportDialog : DialogFragment() {

    private lateinit var binding: DialogProcessExportBinding

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogProcessExportBinding.inflate(layoutInflater, null, false)

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_export_result)
            .setView(binding.root)
            .setNegativeButton(R.string.btn_close, null)
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.exportInputLayout.setEndIconOnClickListener {
            val clipboardManager = requireContext()
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("process", binding.exportEdit.text)
            clipboardManager.setPrimaryClip(clip)
            longToast(R.string.txt_copied_to_clipboard)
        }

        binding.exportEdit.setText(arguments?.getString(EXPORTED_STRING))
    }

    companion object {
        const val EXPORTED_STRING = "exported_string"
    }
}