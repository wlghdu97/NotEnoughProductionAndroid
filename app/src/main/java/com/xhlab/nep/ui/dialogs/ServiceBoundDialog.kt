package com.xhlab.nep.ui.dialogs

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatDialogFragment
import com.xhlab.nep.service.IServiceBinder

abstract class ServiceBoundDialog<T : Service> : AppCompatDialogFragment() {

    private lateinit var service: T
    private var isServiceInvoked = false
    private var isBounded = false
    protected var isTaskDone = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, ibinder: IBinder?) {
            service = getServiceBinder(ibinder).getService()
            initService(service)
            isBounded = true
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            isBounded = false
        }
    }

    abstract fun initService(service: T)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // start service only once
        isServiceInvoked = savedInstanceState?.getBoolean(SERVICE_INVOKED) ?: false
        if (!isServiceInvoked) {
            startService()
            isServiceInvoked = true
        }
    }

    override fun onStart() {
        super.onStart()
        bindService()
    }

    override fun onStop() {
        super.onStop()
        unbindService()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(SERVICE_INVOKED, isServiceInvoked)
        super.onSaveInstanceState(outState)
    }

    private fun startService() {
        val activity = requireActivity()
        val intent = getServiceIntent(activity).apply {
            arguments?.let { putExtras(it) }
        }
        if (Build.VERSION.SDK_INT >= 26) {
            activity.startForegroundService(intent)
        } else {
            activity.startService(intent)
        }
    }

    protected fun stopService() {
        val activity = requireActivity()
        activity.stopService(getServiceIntent(activity))
    }

    private fun bindService() {
        val activity = requireActivity()
        activity.bindService(getServiceIntent(activity), connection, 0)
    }

    private fun unbindService() {
        val activity = requireActivity()
        activity.unbindService(connection)
    }

    abstract fun getServiceIntent(activity: Activity): Intent

    abstract fun getServiceBinder(binder: IBinder?): IServiceBinder<T>

    companion object {
        private const val SERVICE_INVOKED = "service_invoked"
    }
}