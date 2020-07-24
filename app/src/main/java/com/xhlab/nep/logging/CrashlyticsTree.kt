package com.xhlab.nep.logging

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        when (priority) {
            Log.VERBOSE, Log.DEBUG, Log.INFO -> return
        }

        Crashlytics.setInt("priority", priority)
        Crashlytics.setString("tag", tag)
        Crashlytics.setString("message", message)

        if (t == null) {
            Crashlytics.logException(Exception(message))
        } else {
            Crashlytics.logException(t)
        }
    }
}