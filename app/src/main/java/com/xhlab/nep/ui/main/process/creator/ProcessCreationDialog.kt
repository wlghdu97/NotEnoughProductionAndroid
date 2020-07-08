package com.xhlab.nep.ui.main.process.creator

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerDialogFragment
import org.jetbrains.anko.layoutInflater
import javax.inject.Inject

class ProcessCreationDialog : DaggerDialogFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ProcessCreationViewModel
    private lateinit var dialogView: View

    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var nameEdit: EditText

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireContext().layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_process_creation, null)

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_create_process)
            .setView(dialogView)
            .setPositiveButton(R.string.btn_create) { _, _  -> viewModel.createProcess()}
            .setNegativeButton(R.string.btn_cancel, null)
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        nameInputLayout = dialogView.findViewById(R.id.name_input_layout)
        nameEdit = dialogView.findViewById(R.id.name_edit)

        with (nameEdit) {
            addTextChangedListener { viewModel.changeProcessName(it.toString()) }
            requestFocus()
        }
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.processName.observeNotNull(this) {
            if (nameEdit.text.toString() != it) {
                nameEdit.setText(it)
            }
        }

        viewModel.isNameValid.observeNotNull(this) {
            nameInputLayout.helperText = when (it) {
                true -> ""
                false -> getString(R.string.txt_name_empty)
            }
        }
    }
}