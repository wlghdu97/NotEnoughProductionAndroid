package com.xhlab.nep.shared.db

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class NepDriverFactory constructor(private val context: Context) {

    actual fun createDriver(): SqlDriver {
        val schema = Nep.Schema
        return AndroidSqliteDriver(
            schema = schema,
            context = context,
            name = NEP_DB_NAME,
            callback = object : AndroidSqliteDriver.Callback(schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
                    db.execSQL("PRAGMA foreign_keys=ON;") // db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
    }
}
