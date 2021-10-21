package com.xhlab.nep.shared.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class NepDriverFactory {

    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(Nep.Schema, NEP_DB_NAME)
    }
}
