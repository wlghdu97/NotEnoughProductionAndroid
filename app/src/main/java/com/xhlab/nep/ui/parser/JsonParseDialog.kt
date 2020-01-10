package com.xhlab.nep.ui.parser

import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.service.ParseRecipeService
import com.xhlab.nep.shared.util.Resource
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.dialog_json_parser.view.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.textResource

class JsonParseDialog : AppCompatDialogFragment() {

    private lateinit var parseRecipeService: ParseRecipeService
    private var isBounded = false
    private var isTaskDone = false

    private lateinit var dialogView: View
    private lateinit var negativeButton: Button

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder?) {
            val binder = service as ParseRecipeService.LocalBinder
            parseRecipeService = binder.getService()
            initService()
            isBounded = true
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            isBounded = false
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireContext().layoutInflater
        @Suppress("inflateParams")
        dialogView = inflater.inflate(R.layout.dialog_json_parser, null)

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_parsing)
            .setView(dialogView)
            .setNegativeButton(R.string.btn_abort, null)
            .setCancelable(false)
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService()
    }

    override fun onStart() {
        super.onStart()
        bindService()
    }

    override fun onStop() {
        super.onStop()
        unbindService()
    }

    private fun initService() {
        parseRecipeService.parseLog.observe(this) {
            dialogView.log.text = it.data
        }

        parseRecipeService.parseLog.observe(this) {
            isTaskDone = when (it.status) {
                Resource.Status.LOADING -> false
                else -> true
            }
            with (dialogView.progress_bar) {
                isIndeterminate = !isTaskDone
                negativeButton.textResource = when (isTaskDone) {
                    true -> {
                        progress = 100
                        R.string.btn_close
                    }
                    false -> {
                        R.string.btn_abort
                    }
                }
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
                negativeButton.setOnClickListener {
                    when (isTaskDone) {
                        true -> dismiss()
                        false -> stopService()
                    }
                }
            }
        }
    }

    private fun startService() {
        val activity = requireActivity()
        val intent = getIntent(activity).apply {
            arguments?.let { putExtras(it) }
        }
        if (Build.VERSION.SDK_INT >= 26) {
            activity.startForegroundService(intent)
        } else {
            activity.startService(intent)
        }
    }

    private fun stopService() {
        val activity = requireActivity()
        activity.stopService(getIntent(activity))
    }

    private fun bindService() {
        val activity = requireActivity()
        activity.bindService(getIntent(activity), connection, 0)
    }

    private fun unbindService() {
        val activity = requireActivity()
        activity.unbindService(connection)
    }

    private fun getIntent(activity: Activity): Intent {
        return Intent(activity, ParseRecipeService::class.java)
    }

    companion object {
        const val SHOW_JSON_PARSER_DIALOG = "show_json_parser_dialog"
    }
}