package com.xhlab.nep.shared.db

import com.squareup.sqldelight.Query

actual class QueryContainer<RowType : Any> {
    actual var query: Query<RowType>? = null
}
