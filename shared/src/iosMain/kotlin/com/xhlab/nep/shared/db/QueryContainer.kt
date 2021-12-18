package com.xhlab.nep.shared.db

import com.squareup.sqldelight.Query
import kotlinx.atomicfu.atomic

actual class QueryContainer<RowType : Any> {
    private val ref = atomic<Query<RowType>?>(null)
    actual var query: Query<RowType>? by ref
}
