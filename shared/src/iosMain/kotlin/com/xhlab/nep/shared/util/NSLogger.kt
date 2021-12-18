package com.xhlab.nep.shared.util

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import platform.Foundation.NSLog

class NSLogger : LogWriter() {

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        NSLog("%s: (%s) %s", severity.name, tag, message)
    }
}
