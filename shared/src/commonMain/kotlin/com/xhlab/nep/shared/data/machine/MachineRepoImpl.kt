package com.xhlab.nep.shared.data.machine

import com.xhlab.nep.model.Machine
import com.xhlab.nep.shared.data.pagerScope
import com.xhlab.nep.shared.db.Nep
import com.xhlab.nep.shared.db.createOffsetLimitPager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kr.sparkweb.multiplatform.paging.Pager
import kr.sparkweb.multiplatform.paging.PagingConfig

class MachineRepoImpl constructor(
    private val db: Nep,
    private val io: CoroutineDispatcher
) : MachineRepo {

    override suspend fun getMachine(machineId: Int) = withContext(io) {
        db.machineQueries.getMachines(machineId, machineMapper).executeAsOneOrNull()
    }

    override suspend fun insertMachine(modName: String, machineName: String) = withContext(io) {
        with(db.machineQueries) {
            transactionWithResult<Int?> {
                insert(modName, machineName)
                getId(modName, machineName).executeAsOneOrNull()
            }
        }
    }

    override suspend fun deleteAll() = withContext(io) {
        db.machineQueries.deleteAll()
    }

    override fun getMachines(): Pager<Int, Machine> {
        return with(db.machineQueries) {
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = PagingConfig(PAGE_SIZE),
                queryProvider = { limit, offset ->
                    getAllMachines(limit, offset, machineMapper)
                },
                countQuery = getMachineCount(),
                transactor = this
            )
        }
    }

    private val machineMapper = { id: Int, mod_name: String, name: String ->
        Machine(id, mod_name, name)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
