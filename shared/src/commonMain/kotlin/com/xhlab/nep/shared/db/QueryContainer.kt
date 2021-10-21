package com.xhlab.nep.shared.db

import com.squareup.sqldelight.Query

expect class QueryContainer<RowType : Any>() {
    var query: Query<RowType>?
}
