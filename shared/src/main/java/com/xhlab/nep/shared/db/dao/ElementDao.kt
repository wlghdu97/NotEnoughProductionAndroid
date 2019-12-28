package com.xhlab.nep.shared.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.ElementEntity

@Dao
abstract class ElementDao : BaseDao<ElementEntity>() {

    @Query("""
        SELECT * FROM element
        WHERE element.id = :id
    """)
    abstract suspend fun getItem(id: Int): ElementEntity?

    @Query("""
        SELECT element.id FROM element
        WHERE element.unlocalized_name = :unlocalizedName
    """)
    abstract suspend fun getId(unlocalizedName: String): Int
}