package com.xhlab.nep.shared.parser.process

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.xhlab.nep.model.process.Process
import java.lang.reflect.Type

class ProcessSerializer : JsonSerializer<Process> {

    override fun serialize(
        src: Process,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.add("id", context.serialize(src.id))
        jsonObject.add("name", context.serialize(src.name))
        jsonObject.add("rootRecipe", context.serialize(src.rootRecipe))
        jsonObject.add("targetOutput", context.serialize(src.targetOutput))
        val vertices = arrayListOf<JsonElement>()
        for (vertex in src.getVertices()) {
            vertices.add(context.serialize(vertex))
        }
        jsonObject.add("vertices", context.serialize(vertices))
        val edges = arrayListOf<JsonElement>()
        for ((fromIndex, edgeList) in src.getEdges().withIndex()) {
            for (edge in edgeList) {
                val edgeObject = JsonObject().apply {
                    add("from", context.serialize(fromIndex))
                    add("to", context.serialize(edge.index))
                    add("key", context.serialize(edge.key))
                    add("reversed", context.serialize(edge.reversed))
                }
                edges.add(context.serialize(edgeObject))
            }
        }
        jsonObject.add("edges", context.serialize(edges))
        return jsonObject
    }
}