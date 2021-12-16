/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xhlab.nep.shared.parser.stream

/**
 * A structure, name or value type in a JSON-encoded string.
 *
 * @author Jesse Wilson
 * @since 1.6
 */
actual enum class JsonToken {
    /**
     * The opening of a JSON array. Written using [JsonWriter.beginArray]
     * and read using [JsonReader.beginArray].
     */
    BEGIN_ARRAY,

    /**
     * The closing of a JSON array. Written using [JsonWriter.endArray]
     * and read using [JsonReader.endArray].
     */
    END_ARRAY,

    /**
     * The opening of a JSON object. Written using [JsonWriter.beginObject]
     * and read using [JsonReader.beginObject].
     */
    BEGIN_OBJECT,

    /**
     * The closing of a JSON object. Written using [JsonWriter.endObject]
     * and read using [JsonReader.endObject].
     */
    END_OBJECT,

    /**
     * A JSON property name. Within objects, tokens alternate between names and
     * their values. Written using [JsonWriter.name] and read using [ ][JsonReader.nextName]
     */
    NAME,

    /**
     * A JSON string.
     */
    STRING,

    /**
     * A JSON number represented in this API by a Java `double`, `long`, or `int`.
     */
    NUMBER,

    /**
     * A JSON `true` or `false`.
     */
    BOOLEAN,

    /**
     * A JSON `null`.
     */
    NULL,

    /**
     * The end of the JSON stream. This sentinel value is returned by [ ][JsonReader.peek] to signal that the JSON-encoded value has no more
     * tokens.
     */
    END_DOCUMENT
}
