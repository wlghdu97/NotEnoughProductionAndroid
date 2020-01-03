package com.xhlab.nep.shared.db.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.xhlab.nep.shared.domain.item.model.ElementView

@DatabaseView(
    viewName = "element_view",
    value = """
        SELECT id, localized_name, unlocalized_name, type, meta_data FROM element
    """
)
data class RoomElementView(
    override val id: Long,
    @ColumnInfo(name = "localized_name")
    override val localizedName: String,
    @ColumnInfo(name = "unlocalized_name")
    override val unlocalizedName: String,
    override val type: Int,
    @ColumnInfo(name = "meta_data")
    override val metaData: String
) : ElementView()