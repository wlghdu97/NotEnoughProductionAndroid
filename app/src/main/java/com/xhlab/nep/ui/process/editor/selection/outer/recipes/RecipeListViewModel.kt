package com.xhlab.nep.ui.process.editor.selection.outer.recipes

import co.touchlab.kermit.Logger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.multiplatform.util.Resource
import com.xhlab.multiplatform.util.Resource.Companion.isSuccessful
import com.xhlab.nep.MR
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.recipes.OreChainRecipe
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.item.LoadElementDetailUseCase
import com.xhlab.nep.shared.domain.item.LoadElementDetailWithKeyUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.recipe.LoadRecipeMachineListUseCase
import com.xhlab.nep.shared.domain.recipe.LoadUsageMachineListUseCase
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.invokeUseCase
import com.xhlab.nep.shared.util.StringResolver
import com.xhlab.nep.ui.main.machines.MachineListener
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecipeListViewModel @Inject constructor(
    private val processRepo: ProcessRepo,
    private val loadElementDetailUseCase: LoadElementDetailUseCase,
    private val loadElementDetailWithKeyUseCase: LoadElementDetailWithKeyUseCase,
    private val loadRecipeMachineListUseCase: LoadRecipeMachineListUseCase,
    private val loadUsageMachineListUseCase: LoadUsageMachineListUseCase,
    private val stringResolver: StringResolver
) : ViewModel(), MachineListener {

    private val constraint = MutableStateFlow<ProcessEditViewModel.ConnectionConstraint?>(null)

    private val _elements = MutableStateFlow<Resource<List<RecipeElement>>?>(null)
    val elements = _elements.transform {
        if (it?.isSuccessful() == true && it.data!!.size > 1) {
            emit(it.data!!)
        }
    }

    private val _element = MutableStateFlow<Resource<RecipeElement>?>(null)
    val element = _element.transform {
        if (it?.isSuccessful() == true) {
            emit(it.data!!)
        }
    }

    val recipeList = loadRecipeMachineListUseCase.observeOnlySuccess()
    val usageList = loadUsageMachineListUseCase.observeOnlySuccess()

    private val _navigateToDetails = EventFlow<Triple<Long, Int, Boolean>>()
    val navigateToDetails: Flow<Triple<Long, Int, Boolean>>
        get() = _navigateToDetails.flow

    private val _modificationErrorMessage = EventFlow<String>()
    val modificationErrorMessage: Flow<String>
        get() = _modificationErrorMessage.flow

    private val _finish = EventFlow<Unit>()
    val finish: Flow<Unit>
        get() = _finish.flow

    private val modificationExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e("Failed to connect recipe to process", throwable)
        scope.launch {
            _modificationErrorMessage.emit(stringResolver.getString(MR.strings.error_failed_to_modify_process))
        }
    }

    init {
        scope.launch {
            _elements.collect {
                val data = it?.data
                if (data?.size == 1) {
                    _element.value = Resource.success(data[0])
                }
            }
        }

        scope.launch {
            element.collect {
                when (constraint.value?.connectToParent == true) {
                    true -> {
                        invokeMediatorUseCase(
                            useCase = loadUsageMachineListUseCase,
                            params = LoadUsageMachineListUseCase.Parameter(it.id)
                        )
                    }
                    false -> {
                        invokeMediatorUseCase(
                            useCase = loadRecipeMachineListUseCase,
                            params = LoadRecipeMachineListUseCase.Parameter(it.id)
                        )
                    }
                }
            }
        }
    }

    fun init(elementId: Long?, constraint: ProcessEditViewModel.ConnectionConstraint?) {
        requireNotNull(elementId)
        requireNotNull(constraint)
        this.constraint.value = constraint
        invokeUseCase(
            resultData = _element,
            useCase = loadElementDetailUseCase,
            params = elementId
        )
    }

    fun init(elementKey: String?, constraint: ProcessEditViewModel.ConnectionConstraint?) {
        requireNotNull(elementKey)
        requireNotNull(constraint)
        this.constraint.value = constraint
        invokeUseCase(
            resultData = _elements,
            useCase = loadElementDetailWithKeyUseCase,
            params = LoadElementDetailWithKeyUseCase.Parameter(elementKey)
        )
    }

    private fun requireElementId() =
        _element.value?.data?.id ?: throw NullPointerException("element id is null.")

    fun submitElement(element: RecipeElement) {
        _element.value = Resource.success(element)
    }

    fun attachOreDictAsSupplier() {
        scope.launch(modificationExceptionHandler) {
            val constraint = constraint.value
            val ingredient = _element.value?.data
            if (constraint != null && ingredient != null) {
                val recipe = constraint.recipe
                val target = constraint.element
                val chainRecipe = OreChainRecipe(target, ingredient)
                processRepo.connectRecipe(constraint.processId, chainRecipe, recipe, target, false)
                _finish.emit(Unit)
            }
        }
    }

    override fun onClick(machineId: Int) {
        scope.launch {
            val connectToParent = constraint.value?.connectToParent == true
            _navigateToDetails.emit(Triple(requireElementId(), machineId, connectToParent))
        }
    }
}
