package com.xhlab.nep

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Process
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.xhlab.nep.di.DaggerAppComponent
import com.xhlab.nep.logging.CrashlyticsTree
import com.xhlab.nep.ui.ErrorActivity
import com.xhlab.nep.ui.util.SimpleApplicationCallback
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.fabric.sdk.android.Fabric
import io.fabric.sdk.android.InitializationCallback
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

@Suppress("unused")
class NEPApp : DaggerApplication() {

    private val uncaughtHandler = UncaughtHandler()
    private var defaultUEH: Thread.UncaughtExceptionHandler? = null

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .application(this)
            .build().apply { inject(this@NEPApp) }
    }

    override fun onCreate() {
        initFabric()
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }

    private fun initFabric() {
        val core = CrashlyticsCore.Builder().build()
        val callback = object : InitializationCallback<Fabric> {
            override fun failure(e: Exception) = Unit
            override fun success(fabric: Fabric) {
                defaultUEH = Thread.getDefaultUncaughtExceptionHandler()
                Thread.setDefaultUncaughtExceptionHandler(uncaughtHandler)
            }
        }

        Fabric.with(Fabric.Builder(this)
            .kits(Crashlytics.Builder()
                .core(core)
                .build())
            .initializationCallback(callback)
            .build())
    }

    inner class UncaughtHandler :
        Thread.UncaughtExceptionHandler,
        ActivityLifecycleCallbacks by SimpleApplicationCallback()
    {
        private var latestActivity: WeakReference<Activity>? = null
        private var activityCount = 0

        init {
            this@NEPApp.registerActivityLifecycleCallbacks(this)
        }

        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            if (isErrorActivity(activity)) return
            latestActivity = WeakReference(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            if (isErrorActivity(activity)) return
            latestActivity = WeakReference(activity)
            activityCount++
        }

        override fun onActivityStopped(activity: Activity) {
            if (isErrorActivity(activity)) return
            activityCount--
            if (activityCount < 0) {
                latestActivity = null
            }
        }

        private fun isErrorActivity(activity: Activity) = activity is ErrorActivity

        override fun uncaughtException(thread: Thread, throwable: Throwable) {
            latestActivity?.get()?.run {
                val intent = Intent(this@NEPApp, ErrorActivity::class.java).apply {
                    val writer = StringWriter()
                    throwable.printStackTrace(PrintWriter(writer))
                    putExtra(ErrorActivity.ERROR_EXTRA_TEXT, writer.toString())
                    putExtra(ErrorActivity.ERROR_EXTRA_INTENT, intent)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                finish()
            }
            defaultUEH?.uncaughtException(thread, throwable)

            Process.killProcess(Process.myPid())
            exitProcess(10)
        }
    }
}