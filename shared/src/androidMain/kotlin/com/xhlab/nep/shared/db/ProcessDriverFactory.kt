package com.xhlab.nep.shared.db

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class ProcessDriverFactory constructor(private val context: Context) {

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(NepProcess.Schema, context, PROCESS_DB_NAME)
    }
}
