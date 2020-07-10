package com.xhlab.nep.ui.main.process.export

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.xhlab.nep.R
import org.jetbrains.anko.clipboardManager
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.support.v4.longToast

class ProcessExportDialog : DialogFragment() {

    private lateinit var dialogView: View

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireContext().layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_process_export, null)

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_export_result)
            .setView(dialogView)
            .setNegativeButton(R.string.btn_close, null)
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val exportInputLayout = dialogView.findViewById<TextInputLayout>(R.id.export_input_layout)
        val exportEdit = dialogView.findViewById<EditText>(R.id.export_edit)

        exportInputLayout.setEndIconOnClickListener {
            val clipboardManager = requireContext().clipboardManager
            val clip = ClipData.newPlainText("process", exportEdit.text)
            clipboardManager.setPrimaryClip(clip)
            longToast(R.string.txt_copied_to_clipboard)
        }

        exportEdit.setText(arguments?.getString(EXPORTED_STRING))
    }

    companion object {
        const val EXPORTED_STRING = "exported_string"
    }
}