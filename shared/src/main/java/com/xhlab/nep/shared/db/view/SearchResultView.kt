package com.xhlab.nep.shared.db.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

@DatabaseView(
    viewName = "search_result",
    value = """
        SELECT element.id, element.localized_name, element.type FROM element
    """
)
data class SearchResultView(
    val id: Long,
    @ColumnInfo(name = "localized_name")
    val localizedName: String,
    val type: Int
)