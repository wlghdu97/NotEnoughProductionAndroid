package com.xhlab.nep.model.process

import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.Process.ConnectionStatus.*
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import kotlinx.serialization.Serializable

open class Process(
    val id: String,
    var name: String,
    val rootRecipe: Recipe,
    val targetOutput: RecipeElement
) {
    private val vertices = ArrayDeque<Recipe>()
    private val edges = ArrayDeque<ArrayList<Edge>>()

    init {
        connectRecipe(rootRecipe, null, targetOutput)
    }

    fun connectProcess(process: Process, target: Recipe?, element: RecipeElement): Boolean {
        if (vertices.size != edges.size) {
            throw RuntimeException("corrupted process")
        }
        for (edgeIndex in process.edges.indices) {
            for (edge in process.edges[edgeIndex]) {
                val from = process.vertices[edge.index]
                val to = process.vertices[edgeIndex]
                val elementKey = (to.getInputs() + to.getOutput()).find {
                    it.unlocalizedName == edge.key
                }
                if (elementKey != null) {
                    connectRecipe(from, to, elementKey, edge.reversed)
                } else {
                    throw NullPointerException()
                }
            }
        }
        return connectRecipe(process.rootRecipe, target, element)
    }

    fun connectRecipe(
        from: Recipe,
        to: Recipe?,
        element: RecipeElement,
        reversed: Boolean = false
    ): Boolean {

        fun addRecipeNode(recipe: Recipe) {
            if (vertices.indexOf(recipe) == -1) {
                vertices.add(recipe)
                edges.add(arrayListOf())
            }
        }

        fun checkConnection(input: Recipe, output: Recipe, key: String): Boolean {
            return (input.getInputs().find { it.unlocalizedName == key } != null &&
                    output.getOutput().find { it.unlocalizedName == key } != null)
        }

        addRecipeNode(from)
        return if (to != null) {
            val key = element.unlocalizedName
            val isConnectionPossible = when (from == to) {
                true -> true
                false -> checkConnection(to, from, key)
            }
            if (!isConnectionPossible) {
                throw IllegalArgumentException("connection cannot be established.")
            }
            addRecipeNode(to)
            val edges = edges[vertices.indexOf(to)]
            val edge = Edge(vertices.indexOf(from), key, reversed)
            when (!edges.contains(edge)) {
                true -> edges.add(edge)
                false -> false
            }
        } else {
            false
        }
    }

    fun disconnectRecipe(
        from: Recipe,
        to: Recipe,
        element: RecipeElement,
        reversed: Boolean = false
    ): Boolean {
        val edgeIndex = vertices.indexOf(if (reversed) to else from)
        val targetIndex = vertices.indexOf(if (reversed) from else to)
        return if (edgeIndex != -1 && targetIndex != -1) {
            val edge = edges[targetIndex].find {
                it.index == edgeIndex &&
                        it.key == element.unlocalizedName &&
                        it.reversed == reversed
            }
            when {
                edge == null ->
                    disconnectRecipe(to, from, element, reversed)
                edges[targetIndex].remove(edge) ->
                    removeIslands()
                else -> false
            }
        } else {
            false
        }
    }

    fun markNotConsumed(
        recipe: Recipe,
        element: RecipeElement,
        consumed: Boolean = false
    ): Boolean {
        if (!vertices.contains(recipe)) {
            return false
        }
        val connections = getConnectionStatus(recipe, element)
        val status = when (consumed) {
            true -> NOT_CONSUMED
            false -> UNCONNECTED
        }
        return if (connections.size == 1 && connections[0].status == status) {
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

    fun removeRecipeNode(recipe: Recipe): Boolean {
        val vertexIndex = vertices.indexOf(recipe)
        if (vertexIndex == 0) {
            // can't remove root
            return false
        }
        for (index in edges.indices) {
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
                edges[edgeIndex].addAll(targetEdges.map { it.copy(index = it.index - 1) })
            }
        }
        return true
    }

    private fun removeIslands(): Boolean {
        val visited = BooleanArray(vertices.size) { false }
        dfs(0, visited)
        val visitedList = visited.mapIndexed { index, it -> vertices[index] to it }
        var result = true
        for ((vertex, connected) in visitedList) {
            if (!connected && !removeRecipeNode(vertex)) {
                result = false
            }
        }
        return result
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

    fun isRecipeConnected(from: Recipe, to: Recipe, key: String, reversed: Boolean): Boolean {
        return edges[vertices.indexOf(to)].contains(Edge(vertices.indexOf(from), key, reversed))
    }

    fun getConnectionStatus(recipe: Recipe, key: RecipeElement): List<Connection> {
        if (vertices.indexOf(recipe) == -1) {
            return emptyList()
        }
        if (recipe == rootRecipe && targetOutput == key) {
            return listOf(Connection(FINAL_OUTPUT, rootRecipe))
        }
        val index = vertices.indexOf(recipe)
        val edges = edges[index].filter { it.key == key.unlocalizedName }
        val connections = arrayListOf<Connection>()
        val parentEdges = findParentEdges(recipe, key)
        if (parentEdges.isNotEmpty()) {
            for ((edgeIndex, parentEdge) in parentEdges) {
                val parentRecipe = vertices[edgeIndex]
                if (parentEdge != null && parentEdge.reversed) {
                    connections.add(Connection(CONNECTED_TO_CHILD, parentRecipe, true))
                } else if (parentEdge != null) {
                    connections.add(Connection(CONNECTED_TO_PARENT, parentRecipe))
                }
            }
        }
        for (edge in edges) {
            connections.add(
                when {
                    edge.index == index ->
                        Connection(NOT_CONSUMED, recipe)
                    edge.reversed ->
                        Connection(CONNECTED_TO_PARENT, vertices[edge.index], true)
                    else ->
                        Connection(CONNECTED_TO_CHILD, vertices[edge.index])
                }
            )
        }
        if (connections.isEmpty()) {
            connections.add(Connection(UNCONNECTED))
        }
        return connections
    }

    private fun findParentEdges(recipe: Recipe, key: RecipeElement): List<Pair<Int, Edge?>> {
        val index = vertices.indexOf(recipe)
        val edgeList = arrayListOf<Pair<Int, Edge?>>()
        for ((edgeIndex, otherEdge) in edges.withIndex()) {
            if (edgeIndex == index) {
                continue
            }
            val edges = otherEdge.filter { it.index == index && it.key == key.unlocalizedName }
            if (edges.isNotEmpty()) {
                edgeList.addAll(edges.map { edgeIndex to it })
            }
        }
        return edgeList
    }

    fun isElementNotConsumed(recipe: Recipe, key: RecipeElement): Boolean {
        val index = vertices.indexOf(recipe)
        val edge = edges[index].find { it.key == key.unlocalizedName }
        return edge?.index == index
    }

    fun getRecipeArray(): Array<Recipe> {
        return vertices.filterNot { it is SupplierRecipe }.toTypedArray()
    }

    fun getElementMap(): Map<String, RecipeElement> {
        val keyMap = mutableMapOf<String, RecipeElement>()
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
        val whiteList = mutableSetOf<String>()
        for (vertex in vertices) {
            when (vertex) {
                is SupplierRecipe -> {
                    whiteList.addAll(vertex.getOutput().map { it.unlocalizedName })
                }
                else -> {
                    for (input in vertex.getInputs()) {
                        val key = keyMap[input.unlocalizedName]
                        if (key == null) {
                            keyMap[input.unlocalizedName] = 1
                        } else {
                            keyMap[input.unlocalizedName] = key + 1
                        }
                    }
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
        for (key in whiteList) {
            keyMap[key] = 1
        }
        return keyMap.filter { it.value >= 1 }.keys.toList()
    }

    fun getRecipeNodeCount(): Int {
        return vertices.filterNot { it is SupplierRecipe }.size
    }

    fun getEdgesCount(): Int {
        return edges.sumOf { it.size }
    }

    fun getVertices(): List<Recipe> {
        return vertices.toList()
    }

    fun getEdges(): List<List<Edge>> {
        return edges.map { it.toList() }
    }

    @Serializable
    data class Edge(
        val index: Int,
        val key: String,
        val reversed: Boolean = false
    )

    @Serializable
    data class Connection(
        val status: ConnectionStatus,
        val connectedRecipe: Recipe? = null,
        val reversed: Boolean = false
    )

    enum class ConnectionStatus {
        CONNECTED_TO_PARENT, CONNECTED_TO_CHILD, UNCONNECTED, FINAL_OUTPUT, NOT_CONSUMED
    }
}
