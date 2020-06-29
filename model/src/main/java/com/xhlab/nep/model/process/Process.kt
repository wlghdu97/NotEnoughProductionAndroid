package com.xhlab.nep.model.process

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import java.util.*

open class Process<R : Recipe>(
    val id: String,
    val name: String,
    val rootRecipe: R,
    val targetOutput: Element
) {
    private val vertices = LinkedList<R>()
    private val edges = LinkedList<ArrayList<Edge>>()

    init {
        connectRecipe(rootRecipe, null, targetOutput)
    }

    fun connectRecipe(from: R, to: R?, element: Element, reversed: Boolean = false) {

        fun addRecipeNode(recipe: R) {
            if (vertices.indexOf(recipe) == -1) {
                vertices.add(recipe)
                edges.add(arrayListOf())
            }
        }

        addRecipeNode(from)
        if (to != null) {
            val key = element.unlocalizedName
            checkConnection(to, from, key)
            addRecipeNode(to)
            edges[vertices.indexOf(to)].add(Edge(vertices.indexOf(from), key, reversed))
        }
    }

    fun disconnectRecipe(from: R, to: R, element: Element, reversed: Boolean = false) {
        val edgeIndex = vertices.indexOf(from)
        val targetIndex = vertices.indexOf(to)
        if (edgeIndex != -1 && targetIndex != -1) {
            val edge = edges[targetIndex].find {
                it.index == edgeIndex &&
                it.key == element.unlocalizedName &&
                it.reversed == reversed
            }
            edges[targetIndex].remove(edge)
        }
    }

    fun markNotConsumed(recipe: R, element: Element, consumed: Boolean = false): Boolean {
        if (!vertices.contains(recipe)) {
            return false
        }
        val connection = getConnectionStatus(recipe, element)
        return if (connection == ConnectionStatus.UNCONNECTED) {
            val input = recipe.getInputs().find { it.unlocalizedName == element.unlocalizedName }
            if (input == null) {
                false
            } else {
                when (consumed) {
                    true -> disconnectRecipe(recipe, recipe, element)
                    false -> connectRecipe(recipe, recipe, element)
                }
                true
            }
        } else {
            false
        }
    }

    private fun checkConnection(input: R, output: R, key: String): Boolean {
        return (input.getInputs().find { it.unlocalizedName == key } != null &&
                output.getOutput().find { it.unlocalizedName == key } != null)
    }

    fun removeRecipeNode(recipe: R): Boolean {
        val vertexIndex = vertices.indexOf(recipe)
        if (vertexIndex == 0) {
            // can't remove root
            return false
        }
        for (index in 0 until edges.size) {
            val targetEdges = edges[index].filter { it.index == vertexIndex }
            edges[index].removeAll(targetEdges)
        }
        edges.removeAt(vertexIndex)
        vertices.removeAt(vertexIndex)
        // renumber post vertices
        if (vertexIndex < vertices.size) {
            for (edgeIndex in 0 until edges.size) {
                val targetEdges = edges[edgeIndex].filter { it.index > vertexIndex }
                edges[edgeIndex].removeAll(targetEdges)
                edges[edgeIndex].addAll(targetEdges.map { it.copy(index = it.index-1) })
            }
        }
        return true
    }

    fun getRecipeDFSTree(): RecipeNode<R> {
        val visited = BooleanArray(vertices.size) { false }
        return dfs(0, visited)
    }

    private fun dfs(vertex: Int, visited: BooleanArray): RecipeNode<R> {
        visited[vertex] = true
        val childNodes = arrayListOf<RecipeNode<R>>()
        for (adj in edges[vertex]) {
            if (!visited[adj.index] || edges[adj.index].isEmpty()) {
                childNodes.add(dfs(adj.index, visited))
            }
        }
        return RecipeNode(vertices[vertex], childNodes)
    }

    fun getConnectionStatus(recipe: R, key: Element): ConnectionStatus {
        if (recipe == rootRecipe && targetOutput == key) {
            return ConnectionStatus.FINAL_OUTPUT
        }
        val index = vertices.indexOf(recipe)
        val edge = edges[index].find { it.key == key.unlocalizedName }
        return when {
            edge == null -> {
                var parentEdge: Edge? = null
                for ((edgeIndex, otherEdge) in edges.withIndex()) {
                    if (edgeIndex == index) {
                        continue
                    }
                    parentEdge = otherEdge.find { it.index == index && it.key == key.unlocalizedName }
                    if (parentEdge != null) {
                        break
                    }
                }
                when {
                    parentEdge != null && parentEdge.reversed ->
                        ConnectionStatus.CONNECTED_TO_CHILD
                    parentEdge != null ->
                        ConnectionStatus.CONNECTED_TO_PARENT
                    else ->
                        ConnectionStatus.UNCONNECTED
                }
            }
            edge.index == index -> ConnectionStatus.NOT_CONSUMED
            edge.reversed -> ConnectionStatus.CONNECTED_TO_PARENT
            else -> ConnectionStatus.CONNECTED_TO_CHILD
        }
    }

    fun isElementNotConsumed(recipe: Recipe, key: Element): Boolean {
        val index = vertices.indexOf(recipe)
        val edge = edges[index].find { it.key == key.unlocalizedName }
        return edge?.index == index
    }

    fun getRecipeArray(): Array<Recipe> {
        return vertices.toTypedArray()
    }

    fun getElementMap(): Map<String, Element> {
        val keyMap = mutableMapOf<String, Element>()
        for (vertex in vertices) {
            for (input in vertex.getInputs()) {
                if (!keyMap.containsKey(input.unlocalizedName)) {
                    keyMap[input.unlocalizedName] = input
                }
            }
            for (output in vertex.getOutput()) {
                if (!keyMap.containsKey(output.unlocalizedName)) {
                    keyMap[output.unlocalizedName] = output
                }
            }
        }
        return keyMap
    }

    fun getElementKeyList(): List<String> {
        val keySet = mutableSetOf<String>()
        for (vertex in vertices) {
            for (input in vertex.getInputs()) {
                keySet.add(input.unlocalizedName)
            }
            for (output in vertex.getOutput()) {
                keySet.add(output.unlocalizedName)
            }
        }
        return keySet.toList()
    }

    fun getElementLeafKeyList(): List<String> {
        val keyMap = hashMapOf<String, Int>()
        for (vertex in vertices) {
            for (input in vertex.getInputs()) {
                val key = keyMap[input.unlocalizedName]
                if (key == null) {
                    keyMap[input.unlocalizedName] = 1
                } else {
                    keyMap[input.unlocalizedName] = key + 1
                }
            }
        }
        for (edge in edges) {
            for ((_, edgeKey, _) in edge) {
                val key = keyMap[edgeKey]
                if (key != null) {
                    keyMap[edgeKey] = key - 1
                }
            }
        }
        return keyMap.filter { it.value >= 1 }.keys.toList()
    }

    fun getRecipeNodeCount(): Int {
        return vertices.size
    }

    fun getEdgesCount(): Int {
        return edges.sumBy { it.size }
    }

    data class Edge(val index: Int, val key: String, val reversed: Boolean = false)

    enum class ConnectionStatus {
        CONNECTED_TO_PARENT, CONNECTED_TO_CHILD, UNCONNECTED, FINAL_OUTPUT, NOT_CONSUMED
    }
}