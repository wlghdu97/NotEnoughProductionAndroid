package com.xhlab.nep.ui.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.databinding.DialogWithProgressBinding
import com.xhlab.nep.service.IServiceBinder
import com.xhlab.nep.service.ParseRecipeService
import com.xhlab.nep.shared.util.Resource

class JsonParseDialog : ServiceBoundDialog<ParseRecipeService>() {

    private lateinit var binding: DialogWithProgressBinding
    private var negativeButton: Button? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @Suppress("inflateParams")
        binding = DialogWithProgressBinding.inflate(layoutInflater, null, false)

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_parsing)
            .setView(binding.root)
            .setNegativeButton(R.string.btn_abort, null)
            .setCancelable(false)
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    override fun initService(service: ParseRecipeService) {
        service.parseLog.observe(this) {
            binding.log.text = it.data
        }

        service.parseLog.observe(this) {
            isTaskDone = when (it.status) {
                Resource.Status.LOADING -> false
                else -> true
            }
            with (binding.progressBar) {
                isIndeterminate = !isTaskDone
                negativeButton?.setText(when (isTaskDone) {
                    true -> {
                        progress = 100
                        R.string.btn_close
                    }
                    false -> {
                        R.string.btn_abort
                    }
                })
            }
        }
    }

    private fun initView() {
        val dialog = requireDialog()

        with (dialog) {
            setCanceledOnTouchOutside(false)
            setOnShowListener {
                val alertDialog = dialog as AlertDialog
                negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeButton?.setOnClickListener {
                    when (isTaskDone) {
                        true -> dismiss()
                        false -> stopService()
                    }
                }
            }
            setOnKeyListener { _, keyCode, _ ->
                (keyCode == KeyEvent.KEYCODE_BACK && !isTaskDone)
            }
        }
    }

    override fun getServiceBinder(binder: IBinder?): IServiceBinder<ParseRecipeService> {
        return binder as ParseRecipeService.LocalBinder
    }

    override fun getServiceIntent(activity: Activity): Intent {
        return Intent(activity, ParseRecipeService::class.java)
    }

    companion object {
        const val SHOW_JSON_PARSER_DIALOG = "show_json_parser_dialog"
    }
}
