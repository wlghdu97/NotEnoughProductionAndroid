package com.xhlab.nep.shared.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.ElementEntity
import com.xhlab.nep.shared.db.view.RoomElementView

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
    abstract suspend fun getId(unlocalizedName: String): Long

    @Query("""
        SELECT element.id FROM element
        WHERE element.unlocalized_name = :unlocalizedName AND element.meta_data = :metaData
    """)
    abstract suspend fun getId(unlocalizedName: String, metaData: String): Long

    @Query("""
        SELECT chain_element_id FROM ore_dict_chain
        INNER JOIN element ON element.unlocalized_name IN (:unlocalizedNameList)
        WHERE element_id = element.id
        GROUP BY chain_element_id HAVING COUNT(element.id)
    """)
    abstract suspend fun getOreDictChainId(unlocalizedNameList: List<String>): Long

    @Transaction
    @Query("""
        SELECT * FROM element_view
        INNER JOIN element_fts ON element_view.id = element_fts.docid
        WHERE element_fts MATCH :term AND element_view.type NOT IN (2, 3)
        ORDER BY element_view.localized_name ASC
    """)
    abstract fun searchByName(term: String): DataSource.Factory<Int, RoomElementView>

    @Query("""
        DELETE FROM element
    """)
    abstract suspend fun deleteAll()
}