package com.xhlab.nep.shared.data.oredict

import com.xhlab.nep.model.form.ReplacementForm
import com.xhlab.nep.shared.data.element.SqlDelightParserElementMapper
import com.xhlab.nep.shared.data.getId
import com.xhlab.nep.shared.db.Nep
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ReplacementAdder constructor(
    private val db: Nep,
    private val io: CoroutineDispatcher
) {
    private val mapper = SqlDelightParserElementMapper()

    suspend fun insertReplacements(list: List<ReplacementForm>) = withContext(io) {
        insertItemsFromReplacements(list)
        insertReplacementsInternal(list)
    }

    private fun insertItemsFromReplacements(list: List<ReplacementForm>) {
        db.elementQueries.transaction {

            fun insertItemsFromReplacement(replacement: ReplacementForm) {
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

    private fun insertReplacementsInternal(list: List<ReplacementForm>) {
        db.replacementQueries.transaction {

            fun insertReplacement(replacement: ReplacementForm) {
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
