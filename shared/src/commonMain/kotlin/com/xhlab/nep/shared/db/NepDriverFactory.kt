package com.xhlab.nep.shared.db

import com.squareup.sqldelight.db.SqlDriver

internal const val NEP_DB_NAME = "nep.db"

expect class NepDriverFactory {
    fun createDriver(): SqlDriver
}

internal fun NepDriverFactory.createDatabase(): Nep {
    return Nep(driver = createDriver())
}
