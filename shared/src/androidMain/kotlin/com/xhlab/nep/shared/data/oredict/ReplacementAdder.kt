package com.xhlab.nep.shared.data.oredict

import com.xhlab.nep.model.oredict.Replacement
import com.xhlab.nep.shared.data.element.SqlDelightElementMapper
import com.xhlab.nep.shared.data.getId
import com.xhlab.nep.shared.db.Nep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class ReplacementAdder constructor(private val db: Nep) {

    private val mapper = SqlDelightElementMapper()
    private val io = Dispatchers.IO

    suspend fun insertReplacements(list: List<Replacement>) = withContext(io) {
        insertItemsFromReplacements(list)
        insertReplacementsInternal(list)
    }

    private fun insertItemsFromReplacements(list: List<Replacement>) {
        db.elementQueries.transaction {

            fun insertItemsFromReplacement(replacement: Replacement) {
                val itemList = replacement.elementList.distinct()
                val entityList = itemList.map { mapper.map(it)[0] }
                for (entity in entityList) {
                    db.elementQueries.insert(entity)
                }
            }

            for (replacement in list) {
                insertItemsFromReplacement(replacement)
            }
        }
    }

    private fun insertReplacementsInternal(list: List<Replacement>) {
        db.replacementQueries.transaction {

            fun insertReplacement(replacement: Replacement) {
                val entityList = replacement.elementList.map {
                    replacement.oreDictName to it.getId(db)
                }
                for ((oreDictName, elementId) in entityList) {
                    db.replacementQueries.insert(oreDictName, elementId)
                }
            }

            for (replacement in list) {
                insertReplacement(replacement)
            }
        }
    }
}
