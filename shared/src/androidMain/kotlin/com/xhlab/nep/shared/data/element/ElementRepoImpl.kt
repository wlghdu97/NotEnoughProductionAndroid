package com.xhlab.nep.shared.data.element

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.paging.PagingConfig
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.recipes.view.RecipeMachineView
import com.xhlab.nep.shared.data.pagerScope
import com.xhlab.nep.shared.db.Nep
import com.xhlab.nep.shared.db.createOffsetLimitPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class ElementRepoImpl constructor(private val db: Nep) : ElementRepo {

    private val io = Dispatchers.IO

    override suspend fun getIdsByKey(unlocalizedName: String) = withContext(io) {
        db.elementQueries.getIds(unlocalizedName).executeAsList()
    }

    override suspend fun getElementDetail(id: Long) = withContext(io) {
        db.elementViewQueries.getElementDetail(id, elementViewMapper).executeAsOne()
    }

    override suspend fun getReplacementCountByElement(oreDictName: String) = withContext(io) {
        db.elementViewQueries.getReplacementCount(oreDictName).executeAsOne().toInt()
    }

    override suspend fun deleteAll() = withContext(io) {
        db.elementQueries.deleteAll()
    }

    override fun searchByName(term: String): Pager<Int, ElementView> {
        return with(db.elementViewQueries) {
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    searchByName(term, limit, offset, elementViewMapper)
                },
                countQuery = searchCountByName(term),
                transactor = this
            )
        }
    }

    override fun searchMachineResults(machineId: Int, term: String): Pager<Int, ElementView> {
        return with(db.elementViewQueries) {
            val id = machineId.toLong()
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    searchMachineResults(id, term, limit, offset, elementViewMapper)
                },
                countQuery = searchMachineResultCount(id, term),
                transactor = this
            )
        }
    }

    override fun searchMachineResultsFts(machineId: Int, term: String): Pager<Int, ElementView> {
        return with(db.elementViewQueries) {
            val id = machineId.toLong()
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    searchMachineResultsFts(id, term, limit, offset, elementViewMapper)
                },
                countQuery = searchMachineResultCountFts(id, term),
                transactor = this
            )
        }
    }

    override fun getElements(): Pager<Int, ElementView> {
        return with(db.elementViewQueries) {
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    getElements(limit, offset, elementViewMapper)
                },
                countQuery = getElementCount(),
                transactor = this
            )
        }
    }

    override fun getResultsByMachine(machineId: Int): Pager<Int, ElementView> {
        return with(db.elementViewQueries) {
            val id = machineId.toLong()
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    getMachineResults(id, limit, offset, elementViewMapper)
                },
                countQuery = getMachineResultCount(id),
                transactor = this
            )
        }
    }

    override fun getRecipeMachinesByElement(elementId: Long): Pager<Int, RecipeMachineView> {
        return with(db.elementQueries) {
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    getRecipeMachinesByElement(elementId, limit, offset, recipeMachineViewMapper)
                },
                countQuery = getRecipeMachineCountByElement(elementId),
                transactor = this
            )
        }
    }

    override fun getUsageMachinesByElement(elementId: Long): Pager<Int, RecipeMachineView> {
        return with(db.elementQueries) {
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    getUsageMachinesByElement(elementId, limit, offset, recipeMachineViewMapper)
                },
                countQuery = getUsageMachineCountByElement(elementId),
                transactor = this
            )
        }
    }

    override fun getUsagesByElement(elementId: Long): Pager<Int, ElementView> {
        return with(db.elementViewQueries) {
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    getUsagesByElement(elementId, limit, offset, elementViewMapper)
                },
                countQuery = getUsageCountByElement(elementId),
                transactor = this
            )
        }
    }

    override fun getOreDictsByElement(elementId: Long): Pager<Int, String> {
        return with(db.elementQueries) {
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    getOreDictsByElement(elementId, limit, offset)
                },
                countQuery = getOreDictCountByElement(elementId),
                transactor = this
            )
        }
    }

    override fun getReplacementsByElement(oreDictName: String): Pager<Int, ElementView> {
        return with(db.elementViewQueries) {
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    getReplacements(oreDictName, limit, offset, elementViewMapper)
                },
                countQuery = getReplacementCount(oreDictName),
                transactor = this
            )
        }
    }

    private val pagingConfig: PagingConfig
        get() = PagingConfig(PAGE_SIZE)

    private val elementViewMapper = { id: Long,
                                      localized_name: String,
                                      unlocalized_name: String,
                                      type: Int ->
        ElementViewImpl(
            id = id,
            localizedName = localized_name,
            unlocalizedName = unlocalized_name,
            type = type,
            metaData = null
        )
    }

    private val recipeMachineViewMapper = { machineId: Int,
                                            machineName: String,
                                            modName: String,
                                            recipeCount: Long ->
        RecipeMachineView(
            machineId = machineId,
            machineName = machineName,
            modName = modName,
            recipeCount = recipeCount.toInt()
        )
    }

    class ElementViewImpl(
        override val id: Long,
        override val localizedName: String,
        override val unlocalizedName: String,
        override val type: Int,
        override val metaData: String?
    ) : ElementView() {
        override val amount = 1
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
