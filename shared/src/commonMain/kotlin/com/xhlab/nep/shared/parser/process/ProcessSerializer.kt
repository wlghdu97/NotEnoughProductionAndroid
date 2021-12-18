package com.xhlab.nep.shared.parser.process

import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.Process
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ProcessSerializer : KSerializer<Process> {

    override val descriptor = ProcessSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Process) {
        val surrogate = with(value) {
            ProcessSurrogate(
                id = id,
                name = name,
                rootRecipe = rootRecipe,
                targetOutput = targetOutput,
                vertices = getVertices(),
                edges = getEdges().toEdges()
            )
        }
        encoder.encodeSerializableValue(ProcessSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Process {
        val process = decoder.decodeSerializableValue(ProcessSurrogate.serializer())
        return with(process) {
            Process(
                id = id,
                name = name,
                rootRecipe = rootRecipe,
                targetOutput = targetOutput
            ).apply {
                for (edge in edges) {
                    if (edge.from != edge.to) {
                        val from = vertices[edge.from]
                        val to = vertices[edge.to]
                        val element = from.getElement(edge.key)
                        if (element != null) {
                            connectRecipe(from, to, element, edge.reversed)
                        } else {
                            throw NullPointerException()
                        }
                    } else {
                        val recipe = vertices[edge.from]
                        val element = recipe.getElement(edge.key)
                        if (element != null) {
                            markNotConsumed(recipe, element, false)
                        } else {
                            throw NullPointerException()
                        }
                    }
                }
            }
        }
    }

    private fun Recipe.getElement(key: String): RecipeElement? {
        return (getInputs() + getOutput()).find { it.unlocalizedName == key }
    }

    private fun List<List<Process.Edge>>.toEdges(): List<EdgeSurrogate> {
        val edges = arrayListOf<EdgeSurrogate>()
        for ((fromIndex, edgeList) in withIndex()) {
            for (edge in edgeList) {
                val surrogate = EdgeSurrogate(
                    from = edge.index,
                    to = fromIndex,
                    key = edge.key,
                    reversed = edge.reversed
                )
                edges.add(surrogate)
            }
        }
        return edges
    }

    @Serializable
    data class ProcessSurrogate(
        val id: String,
        val name: String,
        val rootRecipe: Recipe,
        val targetOutput: RecipeElement,
        val vertices: List<Recipe>,
        val edges: List<EdgeSurrogate>
    )

    @Serializable
    data class EdgeSurrogate(
        val from: Int,
        val to: Int,
        val key: String,
        val reversed: Boolean
    )
}
