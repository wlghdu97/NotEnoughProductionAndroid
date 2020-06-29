package com.xhlab.nep.shared.domain.process

import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.recipes.view.RecipeView
import com.xhlab.nep.shared.domain.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.optim.linear.*
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import javax.inject.Inject

class ResourceRateCalculationUseCase @Inject constructor()
    : UseCase<ResourceRateCalculationUseCase.Parameter, ResourceRateCalculationUseCase.Result>() {

    override suspend fun execute(params: Parameter) = withContext(Dispatchers.IO) {
        val process = params.process
        val keyList = process.getElementKeyList()
        val leafKeyList = process.getElementLeafKeyList()
        val recipeCount = process.getRecipeNodeCount()

        // define objective function to minimize suppliers(raw resources) cost
        val coefficients = DoubleArray(recipeCount + leafKeyList.size) {
            if (it >= recipeCount) 1.0 else 0.0
        }
        val objective = LinearObjectiveFunction(coefficients, 0.0)
        val constraints = Array(process.getRecipeNodeCount() + leafKeyList.size) {
            DoubleArray(keyList.size)
        }
        // register recipes
        val recipes = process.getRecipeArray()
        for ((index, recipe) in recipes.withIndex()) {
            for (input in recipe.getInputs()) {
                val key = input.unlocalizedName
                if (!process.isElementNotConsumed(recipe, input)) {
                    constraints[index][keyList.indexOf(key)] = -input.amount.toDouble()
                }
            }
            for (output in recipe.getOutput()) {
                val key = output.unlocalizedName
                constraints[index][keyList.indexOf(key)] = output.amount.toDouble()
            }
        }

        // transpose
        val matrix = MatrixUtils.createRealMatrix(constraints).transpose()
        // register constraints
        val constraintList = arrayListOf<LinearConstraint>()
        for (index in keyList.indices) {
            val row = matrix.getRow(index)
            val leafIndex = leafKeyList.indexOf(keyList[index])
            if (leafIndex != -1) {
                row[recipeCount + leafIndex] = 1.0
            }
            val rhs = if (keyList[index] == process.targetOutput.unlocalizedName) {
                process.targetOutput.amount.toDouble()
            } else 0.0
            constraintList.add(LinearConstraint(row, Relationship.GEQ, rhs))
        }

        val constraintsSet = LinearConstraintSet(constraintList)
        val solution = SimplexSolver().optimize(objective, constraintsSet, GoalType.MINIMIZE)

        val elementMap = process.getElementMap()
        Result(
            recipes = List(recipes.size) {
                recipes[it] as RecipeView to solution.point[it]
            },
            suppliers = List(leafKeyList.size) {
                elementMap[leafKeyList[it]] as ElementView to solution.point[recipes.size + it]
            }
        )
    }

    data class Parameter(val process: Process)

    data class Result(
        val recipes: List<Pair<RecipeView, Double>>,
        val suppliers: List<Pair<ElementView, Double>>
    )
}