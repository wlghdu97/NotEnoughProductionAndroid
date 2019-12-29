package com.xhlab.nep.shared.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.ElementEntity
import com.xhlab.nep.shared.db.view.SearchResultView

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

    @Transaction
    @Query("""
        SELECT * FROM search_result
        INNER JOIN element_fts ON search_result.id = element_fts.docid
        WHERE element_fts MATCH :term
        ORDER BY search_result.localized_name ASC
    """)
    abstract fun searchByName(term: String): DataSource.Factory<Int, SearchResultView>
}