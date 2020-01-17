package com.xhlab.nep.shared.data.oredict

import androidx.room.withTransaction
import com.xhlab.nep.model.oredict.Replacement
import com.xhlab.nep.shared.data.element.RoomElementMapper
import com.xhlab.nep.shared.data.generateLongUUID
import com.xhlab.nep.shared.data.getId
import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.shared.db.entity.ReplacementEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReplacementAdder @Inject constructor(
    private val db: AppDatabase,
    private val mapper: RoomElementMapper
) {
    private val io = Dispatchers.IO

    suspend fun insertReplacements(list: List<Replacement>) = withContext(io) {
        insertItemsFromReplacements(list)
        insertReplacementsInternal(list)
    }

    private suspend fun insertItemsFromReplacements(list: List<Replacement>) = db.withTransaction {

        suspend fun insertItemsFromReplacement(replacement: Replacement) {
            val itemList = replacement.elementList.distinct()
            val entityList = itemList.map { mapper.map(it)[0] }
            db.getElementDao().insert(entityList)
        }

        for (replacement in list) {
            insertItemsFromReplacement(replacement)
        }
    }

    private suspend fun insertReplacementsInternal(list: List<Replacement>) = db.withTransaction {
        for (replacement in list) {
            insertReplacement(replacement)
        }
    }

    private suspend fun insertReplacement(replacement: Replacement) {
        val replacementId = generateLongUUID()
        val entityList = replacement.elementList.map {
            ReplacementEntity(
                replacementId = replacementId,
                elementId = it.getId(db)
            )
        }
        db.getReplacementDao().insert(entityList)
    }
}