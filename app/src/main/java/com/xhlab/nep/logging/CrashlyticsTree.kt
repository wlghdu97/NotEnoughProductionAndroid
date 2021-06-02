package com.xhlab.nep.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        when (priority) {
            Log.VERBOSE, Log.DEBUG, Log.INFO -> return
        }

        val instance = FirebaseCrashlytics.getInstance()
        instance.setCustomKey("priority", priority)
        instance.setCustomKey("tag", tag ?: "")
        instance.setCustomKey("message", message)

        if (t == null) {
            instance.recordException(Exception(message))
        } else {
            instance.recordException(t)
        }
    }
}
