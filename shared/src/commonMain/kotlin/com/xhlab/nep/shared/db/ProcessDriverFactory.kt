package com.xhlab.nep.shared.db

import com.squareup.sqldelight.db.SqlDriver

internal const val PROCESS_DB_NAME = "process.db"

expect class ProcessDriverFactory {
    fun createDriver(): SqlDriver
}

internal fun ProcessDriverFactory.createDatabase(): NepProcess {
    return NepProcess(createDriver())
}
