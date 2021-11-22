package com.xhlab.nep.shared.db

import co.touchlab.sqliter.DatabaseConfiguration
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.squareup.sqldelight.drivers.native.wrapConnection

actual class ProcessDriverFactory {

    actual fun createDriver(): SqlDriver {
        val schema = NepProcess.Schema
        val config = DatabaseConfiguration(
            name = PROCESS_DB_NAME,
            version = schema.version,
            create = { connection ->
                wrapConnection(connection) { schema.create(it) }
            },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) { schema.migrate(it, oldVersion, newVersion) }
            },
            extendedConfig = DatabaseConfiguration.Extended(
                foreignKeyConstraints = true
            )
        )
        return NativeSqliteDriver(config)
    }
}
