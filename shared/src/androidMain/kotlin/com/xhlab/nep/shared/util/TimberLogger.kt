package com.xhlab.nep.shared.util

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import timber.log.Timber

class TimberLogger : LogWriter() {

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        Timber.tag(tag).log(severity.ordinal + 2, throwable, message)
    }
}
