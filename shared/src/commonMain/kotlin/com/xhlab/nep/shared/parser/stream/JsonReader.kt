package com.xhlab.nep.shared.parser.stream

expect class JsonReader {
    fun beginArray()
    fun endArray()
    fun beginObject()
    fun endObject()
    fun hasNext(): Boolean
    fun peek(): JsonToken
    fun nextName(): String
    fun nextString(): String
    fun nextBoolean(): Boolean
    fun nextNull()
    fun nextDouble(): Double
    fun nextLong(): Long
    fun nextInt(): Int
    fun skipValue()
    fun close()
    fun getPath(): String
}
