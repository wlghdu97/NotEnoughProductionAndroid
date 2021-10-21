package com.xhlab.nep.shared.util

import co.touchlab.kermit.Logger

class LoggerInitializer {

    fun initialize() {
        Logger.setLogWriters(NSLogger())
    }
}
