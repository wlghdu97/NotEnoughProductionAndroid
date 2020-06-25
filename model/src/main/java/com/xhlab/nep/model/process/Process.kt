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
            edge.reversed -> ConnectionStatus.CONNECTED_TO_PARENT
            else -> ConnectionStatus.CONNECTED_TO_CHILD
        }
    }

    fun getRecipeNodeCount(): Int {
        return vertices.size
    }

    fun getEdgesCount(): Int {
        return edges.sumBy { it.size }
    }

    data class Edge(val index: Int, val key: String, val reversed: Boolean = false)

    enum class ConnectionStatus {
        CONNECTED_TO_PARENT, CONNECTED_TO_CHILD, UNCONNECTED, FINAL_OUTPUT
    }
}