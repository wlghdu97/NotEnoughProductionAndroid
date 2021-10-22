package com.xhlab.nep.ui.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.R
import com.xhlab.nep.databinding.DialogWithProgressBinding
import com.xhlab.nep.service.IServiceBinder
import com.xhlab.nep.service.IconUnzipService
import com.xhlab.nep.ui.ViewInit
import kotlinx.coroutines.flow.collectLatest

class IconUnzipDialog : ServiceBoundDialog<IconUnzipService>(), ViewInit {

    private lateinit var binding: DialogWithProgressBinding
    private var negativeButton: Button? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogWithProgressBinding.inflate(LayoutInflater.from(context))

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_unzipping)
            .setView(binding.root)
            .setNegativeButton(R.string.btn_abort, null)
            .setCancelable(false)
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    override fun initService(service: IconUnzipService) {
        lifecycleScope.launchWhenResumed {
            service.unzipLog.collectLatest {
                isTaskDone = it.status != Resource.Status.LOADING
                binding.log.text = it.data?.message
                binding.progressBar.progress = it.data?.progress ?: 0
                negativeButton?.setText(
                    when (isTaskDone) {
                        true -> R.string.btn_close
                        false -> R.string.btn_abort
                    }
                )
            }
        }
    }

    override fun initView() {
        val dialog = requireDialog()

        with(dialog) {
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

        with(binding.progressBar) {
            isIndeterminate = false
            max = 100
        }

        with(binding.log) {
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
        }
    }

    override fun initViewModel() = Unit

    override fun getServiceBinder(binder: IBinder?): IServiceBinder<IconUnzipService> {
        return binder as IconUnzipService.LocalBinder
    }

    override fun getServiceIntent(activity: Activity): Intent {
        return Intent(activity, IconUnzipService::class.java)
    }

    companion object {
        const val SHOW_ICON_UNZIP_DIALOG = "show_icon_unzip_dialog"
    }
}
