package com.xhlab.nep.shared.domain.process

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.ProcessRecipe
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.optim.linear.*
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import java.util.*
import javax.inject.Inject
import kotlin.collections.LinkedHashSet

class ResourceRateCalculationUseCase @Inject constructor(
    private val processRepo: ProcessRepo
) : UseCase<ResourceRateCalculationUseCase.Parameter, ResourceRateCalculationUseCase.Result>() {

    override suspend fun execute(params: Parameter) = withContext(Dispatchers.IO) {
        val rootProcess = params.process
        val subProcesses = getSubProcesses(rootProcess)

        // define objective function to minimize suppliers(raw resources) cost
        val recipeCount = rootProcess.getRecipeNodeCount() + subProcesses.sumBy { it.getRecipeNodeCount() }
        val leafKeySet = getTotalLeafKeySet(rootProcess, subProcesses)
        val coefficients = DoubleArray(recipeCount + leafKeySet.size) {
            if (it >= recipeCount) 1.0 else 0.0
        }

        val keySet = getTotalElementKeySet(rootProcess, subProcesses)
        val objective = LinearObjectiveFunction(coefficients, 0.0)
        val constraints = Array(coefficients.size) {
            DoubleArray(keySet.size)
        }

        // register recipes
        val recipes = LinkedList(rootProcess.getRecipeArray().map { it to rootProcess })
        val subRecipes: Queue<Triple<Int, Process, Array<Recipe>>> = LinkedList()
        for ((index, pair) in recipes.withIndex()) {
            val (recipe, _) = pair
            if (recipe is ProcessRecipe) {
                val processId = recipe.getInputs()[0].unlocalizedName
                val subProcess = subProcesses.find { it.id == processId }
                    ?: throw NullPointerException()
                subRecipes.add(Triple(index + 1, subProcess, subProcess.getRecipeArray()))
            }
        }
        for (subRecipe in subRecipes) {
            val (index, process, recipeArray) = subRecipe
            recipes.addAll(index, recipeArray.map { it to process })
        }

        for ((index, pair) in recipes.withIndex()) {
            val (recipe, process) = pair
            for (input in recipe.getInputs()) {
                val key = input.unlocalizedName
                if (!process.isElementNotConsumed(recipe, input)) {
                    constraints[index][keySet.indexOf(key)] = -input.amount.toDouble()
                }
            }
            for (output in recipe.getOutput()) {
                val key = output.unlocalizedName
                constraints[index][keySet.indexOf(key)] = output.amount.toDouble()
            }
        }

        // transpose
        val matrix = MatrixUtils.createRealMatrix(constraints).transpose()
        // register constraints
        val ketList = keySet.toList()
        val leafKeyList = leafKeySet.toList()
        val constraintList = arrayListOf<LinearConstraint>()
        for (index in keySet.indices) {
            val row = matrix.getRow(index)
            val leafIndex = leafKeyList.indexOf(ketList[index])
            if (leafIndex != -1) {
                row[recipeCount + leafIndex] = 1.0
            }
            val rhs = if (ketList[index] == rootProcess.targetOutput.unlocalizedName) {
                rootProcess.targetOutput.amount.toDouble()
            } else 0.0
            constraintList.add(LinearConstraint(row, Relationship.GEQ, rhs))
        }

        for (index in ketList.indices) {
            val row = DoubleArray(ketList.size) { idx -> if (idx == index) 1.0 else 0.0 }
            constraintList.add(LinearConstraint(row, Relationship.GEQ, 0.0))
        }

        val constraintsSet = LinearConstraintSet(constraintList)
        val solution = SimplexSolver().optimize(objective, constraintsSet, GoalType.MINIMIZE)

        val elementMap = getTotalElementMap(rootProcess, subProcesses)
        Result(
            recipes = List(recipes.size) {
                recipes[it].first to solution.point[it]
            },
            suppliers = List(leafKeyList.size) {
                elementMap[leafKeyList[it]] as ElementView to solution.point[recipes.size + it]
            }
        )
    }

    private suspend fun getSubProcesses(rootProcess: Process): List<Process> {
        val list = arrayListOf<Process>()
        for (processId in rootProcess.getSubProcessIds()) {
            val subProcess = processRepo.getProcess(processId)
                ?: throw NullPointerException("could not find referenced process.")
            list.add(subProcess)
        }
        return list
    }

    private fun getTotalLeafKeySet(rootProcess: Process, subProcesses: List<Process>): Set<String> {
        val leafKeySet = LinkedHashSet<String>(rootProcess.getElementLeafKeyList())
        for (subProcess in subProcesses) {
            leafKeySet.addAll(subProcess.getElementLeafKeyList())
        }
        return leafKeySet
    }

    private fun getTotalElementKeySet(rootProcess: Process, subProcesses: List<Process>): Set<String> {
        val elementKeySet = LinkedHashSet<String>(rootProcess.getElementKeyList())
        for (subProcess in subProcesses) {
            elementKeySet.addAll(subProcess.getElementKeyList())
        }
        return elementKeySet
    }

    private fun getTotalElementMap(rootProcess: Process, subProcesses: List<Process>): Map<String, Element> {
        val elementMap = rootProcess.getElementMap().toMutableMap()
        for (subProcess in subProcesses) {
            elementMap.putAll(subProcess.getElementMap())
        }
        return elementMap
    }

    data class Parameter(val process: Process)

    data class Result(
        val recipes: List<Pair<Recipe, Double>>,
        val suppliers: List<Pair<ElementView, Double>>
    )
}