package com.xhlab.nep.ui.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.service.IServiceBinder
import com.xhlab.nep.service.IconUnzipService
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.ViewInit
import kotlinx.android.synthetic.main.dialog_with_progress.view.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.textResource

class IconUnzipDialog : ServiceBoundDialog<IconUnzipService>(), ViewInit {

    private lateinit var dialogView: View
    private var negativeButton: Button? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireContext().layoutInflater
        @Suppress("inflateParams")
        dialogView = inflater.inflate(R.layout.dialog_with_progress, null)

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_unzipping)
            .setView(dialogView)
            .setNegativeButton(R.string.btn_abort, null)
            .setCancelable(false)
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    override fun initService(service: IconUnzipService) {
        service.unzipLog.observe(this) {
            isTaskDone = it.status != Resource.Status.LOADING
            dialogView.log.text = it.data?.message
            dialogView.progress_bar.progress = it.data?.progress ?: 0
            negativeButton?.textResource = when (isTaskDone) {
                true -> R.string.btn_close
                false -> R.string.btn_abort
            }
        }
    }

    override fun initView() {
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

        with (dialogView.progress_bar) {
            isIndeterminate = false
            max = 100
        }

        with (dialogView.log) {
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