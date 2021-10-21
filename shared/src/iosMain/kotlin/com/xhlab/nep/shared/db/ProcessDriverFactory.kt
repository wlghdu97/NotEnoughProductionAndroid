package com.xhlab.nep.shared.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class ProcessDriverFactory {

    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(NepProcess.Schema, PROCESS_DB_NAME)
    }
}
