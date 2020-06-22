package com.xhlab.nep.model.process

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import java.util.*

data class Process(
    val id: String,
    val name: String,
    val rootRecipe: Recipe,
    val targetOutput: Element
) {
    private val vertices = LinkedList<Recipe>()
    private val edges = LinkedList<ArrayList<Edge>>()

    init {
        connectRecipe(rootRecipe, null, targetOutput)
    }

    fun connectRecipe(from: Recipe, to: Recipe?, element: Element, reversed: Boolean = false) {

        fun addRecipeNode(recipe: Recipe) {
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

    fun disconnectRecipe(from: Recipe, to: Recipe, element: Element, reversed: Boolean = false) {
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

    private fun checkConnection(input: Recipe, output: Recipe, key: String): Boolean {
        return (input.getInputs().find { it.unlocalizedName == key } != null &&
                output.getOutput().find { it.unlocalizedName == key } != null)
    }

    fun removeRecipeNode(recipe: Recipe): Boolean {
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

    fun getRecipeDFSTree(): RecipeNode {
        val visited = BooleanArray(vertices.size) { false }
        return dfs(0, visited)
    }

    private fun dfs(vertex: Int, visited: BooleanArray): RecipeNode {
        visited[vertex] = true
        val childNodes = arrayListOf<RecipeNode>()
        for (adj in edges[vertex]) {
            if (!visited[adj.index] || edges[adj.index].isEmpty()) {
                childNodes.add(dfs(adj.index, visited))
            }
        }
        return RecipeNode(vertices[vertex], childNodes)
    }

    fun getRecipeNodeCount(): Int {
        return vertices.size
    }

    fun getEdgesCount(): Int {
        return edges.sumBy { it.size }
    }

    data class Edge(val index: Int, val key: String, val reversed: Boolean = false)
}