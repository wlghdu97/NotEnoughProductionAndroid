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

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.Foundation.NSInputStream
import kotlin.math.max

/**
 * Reads a JSON (<a href="http://www.ietf.org/rfc/rfc7159.txt">RFC 7159</a>)
 * encoded value as a stream of tokens. This stream includes both literal
 * values (strings, numbers, booleans, and nulls) as well as the begin and
 * end delimiters of objects and arrays. The tokens are traversed in
 * depth-first order, the same order that they appear in the JSON document.
 * Within JSON objects, name/value pairs are represented by a single token.
 *
 * <h3>Parsing JSON</h3>
 * To create a recursive descent parser for your own JSON streams, first create
 * an entry point method that creates a {@code JsonReader}.
 *
 * <p>Next, create handler methods for each structure in your JSON text. You'll
 * need a method for each object type and for each array type.
 * <ul>
 *   <li>Within <strong>array handling</strong> methods, first call {@link
 *       #beginArray} to consume the array's opening bracket. Then create a
 *       while loop that accumulates values, terminating when {@link #hasNext}
 *       is false. Finally, read the array's closing bracket by calling {@link
 *       #endArray}.
 *   <li>Within <strong>object handling</strong> methods, first call {@link
 *       #beginObject} to consume the object's opening brace. Then create a
 *       while loop that assigns values to local variables based on their name.
 *       This loop should terminate when {@link #hasNext} is false. Finally,
 *       read the object's closing brace by calling {@link #endObject}.
 * </ul>
 * <p>When a nested object or array is encountered, delegate to the
 * corresponding handler method.
 *
 * <p>When an unknown name is encountered, strict parsers should fail with an
 * exception. Lenient parsers should call {@link #skipValue()} to recursively
 * skip the value's nested tokens, which may otherwise conflict.
 *
 * <p>If a value may be null, you should first check using {@link #peek()}.
 * Null literals can be consumed using either {@link #nextNull()} or {@link
 * #skipValue()}.
 *
 * <h3>Example</h3>
 * Suppose we'd like to parse a stream of messages such as the following: <pre> {@code
 * [
 *   {
 *     "id": 912345678901,
 *     "text": "How do I read a JSON stream in Java?",
 *     "geo": null,
 *     "user": {
 *       "name": "json_newb",
 *       "followers_count": 41
 *      }
 *   },
 *   {
 *     "id": 912345678902,
 *     "text": "@json_newb just use JsonReader!",
 *     "geo": [50.454722, -104.606667],
 *     "user": {
 *       "name": "jesse",
 *       "followers_count": 2
 *     }
 *   }
 * ]}</pre>
 * This code implements the parser for the above structure: <pre>   {@code
 *
 *   public List<Message> readJsonStream(InputStream in) throws IOException {
 *     JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
 *     try {
 *       return readMessagesArray(reader);
 *     } finally {
 *       reader.close();
 *     }
 *   }
 *
 *   public List<Message> readMessagesArray(JsonReader reader) throws IOException {
 *     List<Message> messages = new ArrayList<Message>();
 *
 *     reader.beginArray();
 *     while (reader.hasNext()) {
 *       messages.add(readMessage(reader));
 *     }
 *     reader.endArray();
 *     return messages;
 *   }
 *
 *   public Message readMessage(JsonReader reader) throws IOException {
 *     long id = -1;
 *     String text = null;
 *     User user = null;
 *     List<Double> geo = null;
 *
 *     reader.beginObject();
 *     while (reader.hasNext()) {
 *       String name = reader.nextName();
 *       if (name.equals("id")) {
 *         id = reader.nextLong();
 *       } else if (name.equals("text")) {
 *         text = reader.nextString();
 *       } else if (name.equals("geo") && reader.peek() != JsonToken.NULL) {
 *         geo = readDoublesArray(reader);
 *       } else if (name.equals("user")) {
 *         user = readUser(reader);
 *       } else {
 *         reader.skipValue();
 *       }
 *     }
 *     reader.endObject();
 *     return new Message(id, text, user, geo);
 *   }
 *
 *   public List<Double> readDoublesArray(JsonReader reader) throws IOException {
 *     List<Double> doubles = new ArrayList<Double>();
 *
 *     reader.beginArray();
 *     while (reader.hasNext()) {
 *       doubles.add(reader.nextDouble());
 *     }
 *     reader.endArray();
 *     return doubles;
 *   }
 *
 *   public User readUser(JsonReader reader) throws IOException {
 *     String username = null;
 *     int followersCount = -1;
 *
 *     reader.beginObject();
 *     while (reader.hasNext()) {
 *       String name = reader.nextName();
 *       if (name.equals("name")) {
 *         username = reader.nextString();
 *       } else if (name.equals("followers_count")) {
 *         followersCount = reader.nextInt();
 *       } else {
 *         reader.skipValue();
 *       }
 *     }
 *     reader.endObject();
 *     return new User(username, followersCount);
 *   }}</pre>
 *
 * <h3>Number Handling</h3>
 * This reader permits numeric values to be read as strings and string values to
 * be read as numbers. For example, both elements of the JSON array {@code
 * [1, "1"]} may be read using either {@link #nextInt} or {@link #nextString}.
 * This behavior is intended to prevent lossy numeric conversions: double is
 * JavaScript's only numeric type and very large values like {@code
 * 9007199254740993} cannot be represented exactly on that platform. To minimize
 * precision loss, extremely large values should be written and read as strings
 * in JSON.
 *
 * <a id="nonexecuteprefix"/><h3>Non-Execute Prefix</h3>
 * Web servers that serve private data using JSON may be vulnerable to <a
 * href="http://en.wikipedia.org/wiki/JSON#Cross-site_request_forgery">Cross-site
 * request forgery</a> attacks. In such an attack, a malicious site gains access
 * to a private JSON file by executing it with an HTML {@code <script>} tag.
 *
 * <p>Prefixing JSON files with <code>")]}'\n"</code> makes them non-executable
 * by {@code <script>} tags, disarming the attack. Since the prefix is malformed
 * JSON, strict parsing fails when it is encountered. This class permits the
 * non-execute prefix when {@link #setLenient(boolean) lenient parsing} is
 * enabled.
 *
 * <p>Each {@code JsonReader} may be used to read a single JSON stream. Instances
 * of this class are not thread safe.
 *
 * @author Jesse Wilson
 * @since 1.6
 */
actual class JsonReader(private val input: NSInputStream) {

    /**
     * Configure this parser to be liberal in what it accepts. By default,
     * this parser is strict and only accepts JSON as specified by [RFC 4627](http://www.ietf.org/rfc/rfc4627.txt). Setting the
     * parser to lenient causes it to ignore the following syntax errors:
     *
     *
     *  * Streams that start with the [non-execute prefix](#nonexecuteprefix).
     *  * Streams that include multiple top-level values. With strict parsing,
     * each stream must contain exactly one top-level value.
     *  * Top-level values of any type. With strict parsing, the top-level
     * value must be an object or an array.
     *  * Numbers may be [NaNs][Double.isNaN] or [infinities][Double.isInfinite].
     *  * End of line comments starting with `//` or `#` and
     * ending with a newline character.
     *  * C-style comments starting with `/*` and ending with
     * `*/`. Such comments may not be nested.
     *  * Names that are unquoted or `'single quoted'`.
     *  * Strings that are unquoted or `'single quoted'`.
     *  * Array elements separated by `;` instead of `,`.
     *  * Unnecessary array separators. These are interpreted as if null
     * was the omitted value.
     *  * Names and values separated by `=` or `=>` instead of
     * `:`.
     *  * Name/value pairs separated by `;` instead of `,`.
     *
     */
    var isLenient = false

    /**
     * Use a manual buffer to easily read and unread upcoming characters, and
     * also so we can create strings without an intermediate StringBuilder.
     * We decode literals directly out of this buffer, so it must be at least as
     * long as the longest token that can be reported as a number.
     */
    private val buffer = CharArray(BUFFER_SIZE)
    private var pos = 0
    private var limit = 0

    private var lineNumber = 0
    private var lineStart = 0

    var peeked = PEEKED_NONE

    /**
     * A peeked value that was composed entirely of digits with an optional
     * leading dash. Positive values may not have a leading 0.
     */
    private var peekedLong: Long = 0

    /**
     * The number of characters in a peeked number literal. Increment 'pos' by
     * this after reading a number.
     */
    private var peekedNumberLength = 0

    /**
     * A peeked string that should be parsed on the next double, long or string.
     * This is populated before a numeric value is parsed and used if that parsing
     * fails.
     */
    private var peekedString: String? = null

    /*
     * The nesting stack. Using a manual array rather than an ArrayList saves 20%.
     */
    private var stack = IntArray(32)
    private var stackSize = 0

    init {
        stack[stackSize++] = JsonScope.EMPTY_DOCUMENT
    }

    /*
     * The path members. It corresponds directly to stack: At indices where the
     * stack contains an object (EMPTY_OBJECT, DANGLING_NAME or NONEMPTY_OBJECT),
     * pathNames contains the name at this scope. Where it contains an array
     * (EMPTY_ARRAY, NONEMPTY_ARRAY) pathIndices contains the current index in
     * that array. Otherwise the value is undefined, and we take advantage of that
     * by incrementing pathIndices when doing so isn't useful.
     */
    private var pathNames = arrayOfNulls<String>(32)
    private var pathIndices = IntArray(32)

    init {
        input.open()
    }

    /**
     * Consumes the next token from the JSON stream and asserts that it is the
     * beginning of a new array.
     */
    actual fun beginArray() {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        if (p == PEEKED_BEGIN_ARRAY) {
            push(JsonScope.EMPTY_ARRAY)
            pathIndices[stackSize - 1] = 0
            peeked = PEEKED_NONE
        } else {
            throw IllegalStateException("Expected BEGIN_ARRAY but was " + peek() + locationString())
        }
    }

    /**
     * Consumes the next token from the JSON stream and asserts that it is the
     * end of the current array.
     */
    actual fun endArray() {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        if (p == PEEKED_END_ARRAY) {
            stackSize--
            pathIndices[stackSize - 1]++
            peeked = PEEKED_NONE
        } else {
            throw IllegalStateException("Expected END_ARRAY but was " + peek() + locationString())
        }
    }

    /**
     * Consumes the next token from the JSON stream and asserts that it is the
     * beginning of a new object.
     */
    actual fun beginObject() {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        if (p == PEEKED_BEGIN_OBJECT) {
            push(JsonScope.EMPTY_OBJECT)
            peeked = PEEKED_NONE
        } else {
            throw IllegalStateException("Expected BEGIN_OBJECT but was " + peek() + locationString())
        }
    }

    /**
     * Consumes the next token from the JSON stream and asserts that it is the
     * end of the current object.
     */
    actual fun endObject() {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        if (p == PEEKED_END_OBJECT) {
            stackSize--
            // Free the last path name so that it can be garbage collected!
            pathNames[stackSize] = null
            pathIndices[stackSize - 1]++
            peeked = PEEKED_NONE
        } else {
            throw IllegalStateException("Expected END_OBJECT but was " + peek() + locationString())
        }
    }

    /**
     * Returns true if the current array or object has another element.
     */
    actual operator fun hasNext(): Boolean {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        return p != PEEKED_END_OBJECT && p != PEEKED_END_ARRAY
    }

    /**
     * Returns the type of the next token without consuming it.
     */
    actual fun peek(): JsonToken {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }

        return when (p) {
            PEEKED_BEGIN_OBJECT -> JsonToken.BEGIN_OBJECT
            PEEKED_END_OBJECT -> JsonToken.END_OBJECT
            PEEKED_BEGIN_ARRAY -> JsonToken.BEGIN_ARRAY
            PEEKED_END_ARRAY -> JsonToken.END_ARRAY
            PEEKED_SINGLE_QUOTED_NAME,
            PEEKED_DOUBLE_QUOTED_NAME,
            PEEKED_UNQUOTED_NAME -> JsonToken.NAME
            PEEKED_TRUE,
            PEEKED_FALSE -> JsonToken.BOOLEAN
            PEEKED_NULL -> JsonToken.NULL
            PEEKED_SINGLE_QUOTED,
            PEEKED_DOUBLE_QUOTED,
            PEEKED_UNQUOTED,
            PEEKED_BUFFERED -> JsonToken.STRING
            PEEKED_LONG,
            PEEKED_NUMBER -> JsonToken.NUMBER
            PEEKED_EOF -> JsonToken.END_DOCUMENT
            else -> throw AssertionError()
        }
    }

    private fun doPeek(): Int {
        val peekStack = stack[stackSize - 1]
        if (peekStack == JsonScope.EMPTY_ARRAY) {
            stack[stackSize - 1] = JsonScope.NONEMPTY_ARRAY
        } else if (peekStack == JsonScope.NONEMPTY_ARRAY) {
            // Look for a comma before the next element.
            when (nextNonWhitespace(true).toChar()) {
                ']' -> return PEEKED_END_ARRAY.also { peeked = it }
                ';' -> checkLenient() // fall-through
                ',' -> Unit
                else -> throw syntaxError("Unterminated array")
            }
        } else if (peekStack == JsonScope.EMPTY_OBJECT || peekStack == JsonScope.NONEMPTY_OBJECT) {
            stack[stackSize - 1] = JsonScope.DANGLING_NAME
            // Look for a comma before the next element.
            if (peekStack == JsonScope.NONEMPTY_OBJECT) {
                when (nextNonWhitespace(true).toChar()) {
                    '}' -> return PEEKED_END_OBJECT.also { peeked = it }
                    ';' -> checkLenient() // fall-through
                    ',' -> Unit
                    else -> throw syntaxError("Unterminated object")
                }
            }
            return when (val c = nextNonWhitespace(true).toChar()) {
                '"' -> PEEKED_DOUBLE_QUOTED_NAME.also { peeked = it }
                '\'' -> {
                    checkLenient()
                    PEEKED_SINGLE_QUOTED_NAME.also { peeked = it }
                }
                '}' -> if (peekStack != JsonScope.NONEMPTY_OBJECT) {
                    PEEKED_END_OBJECT.also { peeked = it }
                } else {
                    throw syntaxError("Expected name")
                }
                else -> {
                    checkLenient()
                    pos-- // Don't consume the first character in an unquoted string.
                    if (isLiteral(c)) {
                        PEEKED_UNQUOTED_NAME.also { peeked = it }
                    } else {
                        throw syntaxError("Expected name")
                    }
                }
            }
        } else if (peekStack == JsonScope.DANGLING_NAME) {
            stack[stackSize - 1] = JsonScope.NONEMPTY_OBJECT
            // Look for a colon before the value.
            when (nextNonWhitespace(true).toChar()) {
                ':' -> Unit
                '=' -> {
                    checkLenient()
                    if ((pos < limit || fillBuffer(1)) && buffer[pos] == '>') {
                        pos++
                    }
                }
                else -> throw syntaxError("Expected ':'")
            }
        } else if (peekStack == JsonScope.EMPTY_DOCUMENT) {
            if (isLenient) {
                consumeNonExecutePrefix()
            }
            stack[stackSize - 1] = JsonScope.NONEMPTY_DOCUMENT
        } else if (peekStack == JsonScope.NONEMPTY_DOCUMENT) {
            val c = nextNonWhitespace(false)
            if (c == -1) {
                return PEEKED_EOF.also { peeked = it }
            } else {
                checkLenient()
                pos--
            }
        } else if (peekStack == JsonScope.CLOSED) {
            throw IllegalArgumentException("JsonReader is closed")
        }

        when (nextNonWhitespace(true).toChar()) {
            ']' -> {
                if (peekStack == JsonScope.EMPTY_ARRAY) {
                    return PEEKED_END_ARRAY.also { peeked = it }
                }
                // In lenient mode, a 0-length literal in an array means 'null'.
                if (peekStack == JsonScope.EMPTY_ARRAY || peekStack == JsonScope.NONEMPTY_ARRAY) {
                    checkLenient()
                    pos--
                    return PEEKED_NULL.also { peeked = it }
                } else {
                    throw syntaxError("Unexpected value")
                }
            }
            ';', ',' -> if (peekStack == JsonScope.EMPTY_ARRAY || peekStack == JsonScope.NONEMPTY_ARRAY) {
                checkLenient()
                pos--
                return PEEKED_NULL.also { peeked = it }
            } else {
                throw syntaxError("Unexpected value")
            }
            '\'' -> {
                checkLenient()
                return PEEKED_SINGLE_QUOTED.also { peeked = it }
            }
            '"' -> return PEEKED_DOUBLE_QUOTED.also { peeked = it }
            '[' -> return PEEKED_BEGIN_ARRAY.also { peeked = it }
            '{' -> return PEEKED_BEGIN_OBJECT.also { peeked = it }
            else -> pos-- // Don't consume the first character in a literal value.
        }

        var result = peekKeyword()
        if (result != PEEKED_NONE) {
            return result
        }

        result = peekNumber()
        if (result != PEEKED_NONE) {
            return result
        }

        if (!isLiteral(buffer[pos])) {
            throw syntaxError("Expected value")
        }

        checkLenient()
        return PEEKED_UNQUOTED.also { peeked = it }
    }

    private fun peekKeyword(): Int {
        // Figure out which keyword we're matching against by its first character.
        var c = buffer[pos]
        val keyword: String
        val keywordUpper: String
        val peeking: Int
        if (c == 't' || c == 'T') {
            keyword = "true"
            keywordUpper = "TRUE"
            peeking = PEEKED_TRUE
        } else if (c == 'f' || c == 'F') {
            keyword = "false"
            keywordUpper = "FALSE"
            peeking = PEEKED_FALSE
        } else if (c == 'n' || c == 'N') {
            keyword = "null"
            keywordUpper = "NULL"
            peeking = PEEKED_NULL
        } else {
            return PEEKED_NONE
        }

        // Confirm that chars [1..length) match the keyword.
        val length = keyword.length
        for (i in 1 until length) {
            if (pos + i >= limit && !fillBuffer(i + 1)) {
                return PEEKED_NONE
            }
            c = buffer[pos + i]
            if (c != keyword[i] && c != keywordUpper[i]) {
                return PEEKED_NONE
            }
        }

        if ((pos + length < limit || fillBuffer(length + 1)) && isLiteral(buffer[pos + length])) {
            return PEEKED_NONE // Don't match trues, falsey or nullsoft!
        }

        // We've found the keyword followed either by EOF or by a non-literal character.
        pos += length
        return peeking.also { peeked = it }
    }

    private fun peekNumber(): Int {
        // Like nextNonWhitespace, this uses locals 'p' and 'l' to save inner-loop field access.
        val buffer = buffer
        var p = pos
        var l = limit

        var value: Long = 0 // Negative to accommodate Long.MIN_VALUE more easily.
        var negative = false
        var fitsInLong = true
        var last = NUMBER_CHAR_NONE

        var i = 0

        charactersOfNumber@ while (true) {
            if (p + i == l) {
                if (i == buffer.size) {
                    // Though this looks like a well-formed number, it's too long to continue reading. Give up
                    // and let the application handle this as an unquoted literal.
                    return PEEKED_NONE
                }
                if (!fillBuffer(i + 1)) {
                    break
                }
                p = pos
                l = limit
            }

            when (val c = buffer[p + i]) {
                '-' -> {
                    if (last == NUMBER_CHAR_NONE) {
                        negative = true
                        last = NUMBER_CHAR_SIGN
                        i++
                        continue
                    } else if (last == NUMBER_CHAR_EXP_E) {
                        last = NUMBER_CHAR_EXP_SIGN
                        i++
                        continue
                    }
                    return PEEKED_NONE
                }
                '+' -> {
                    if (last == NUMBER_CHAR_EXP_E) {
                        last = NUMBER_CHAR_EXP_SIGN
                        i++
                        continue
                    }
                    return PEEKED_NONE
                }
                'e', 'E' -> {
                    if (last == NUMBER_CHAR_DIGIT || last == NUMBER_CHAR_FRACTION_DIGIT) {
                        last = NUMBER_CHAR_EXP_E
                        i++
                        continue
                    }
                    return PEEKED_NONE
                }
                '.' -> {
                    if (last == NUMBER_CHAR_DIGIT) {
                        last = NUMBER_CHAR_DECIMAL
                        i++
                        continue
                    }
                    return PEEKED_NONE
                }
                else -> {
                    if (c < '0' || c > '9') {
                        if (!isLiteral(c)) {
                            break@charactersOfNumber
                        }
                        return PEEKED_NONE
                    }
                    if (last == NUMBER_CHAR_SIGN || last == NUMBER_CHAR_NONE) {
                        value = -(c - '0').toLong()
                        last = NUMBER_CHAR_DIGIT
                    } else if (last == NUMBER_CHAR_DIGIT) {
                        if (value == 0L) {
                            return PEEKED_NONE // Leading '0' prefix is not allowed (since it could be octal).
                        }
                        val newValue = value * 10 - (c - '0')
                        fitsInLong = fitsInLong and (value > MIN_INCOMPLETE_INTEGER
                                || value == MIN_INCOMPLETE_INTEGER && newValue < value)
                        value = newValue
                    } else if (last == NUMBER_CHAR_DECIMAL) {
                        last = NUMBER_CHAR_FRACTION_DIGIT
                    } else if (last == NUMBER_CHAR_EXP_E || last == NUMBER_CHAR_EXP_SIGN) {
                        last = NUMBER_CHAR_EXP_DIGIT
                    }
                }
            }

            i++
        }

        // We've read a complete number. Decide if it's a PEEKED_LONG or a PEEKED_NUMBER.
        return if (last == NUMBER_CHAR_DIGIT && fitsInLong && (value != Long.MIN_VALUE || negative) && (value != 0L || !negative)) {
            peekedLong = if (negative) value else -value
            pos += i
            PEEKED_LONG.also { peeked = it }
        } else if (last == NUMBER_CHAR_DIGIT || last == NUMBER_CHAR_FRACTION_DIGIT || last == NUMBER_CHAR_EXP_DIGIT) {
            peekedNumberLength = i
            PEEKED_NUMBER.also { peeked = it }
        } else {
            PEEKED_NONE
        }
    }

    private fun isLiteral(c: Char): Boolean {
        return when (c) {
            '/', '\\', ';', '#', '=' -> {
                checkLenient() // fall-through
                false
            }
            '{', '}', '[', ']', ':', ',', ' ', '\t', '\u000C', '\r', '\n' -> false
            else -> true
        }
    }

    /**
     * Returns the next token, a [property name][com.google.gson.stream.JsonToken.NAME], and
     * consumes it.
     *
     * @throws java.io.IOException if the next token in the stream is not a property
     * name.
     */
    actual fun nextName(): String {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        val result = when {
            (p == PEEKED_UNQUOTED_NAME) -> {
                nextUnquotedValue()
            }
            (p == PEEKED_SINGLE_QUOTED_NAME) -> {
                nextQuotedValue('\'')
            }
            (p == PEEKED_DOUBLE_QUOTED_NAME) -> {
                nextQuotedValue('"')
            }
            else -> {
                throw IllegalStateException("Expected a name but was " + peek() + locationString())
            }
        }
        peeked = PEEKED_NONE
        pathNames[stackSize - 1] = result
        return result
    }

    /**
     * Returns the [string][com.google.gson.stream.JsonToken.STRING] value of the next token,
     * consuming it. If the next token is a number, this method will return its
     * string form.
     *
     * @throws IllegalStateException if the next token is not a string or if
     * this reader is closed.
     */
    actual fun nextString(): String {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        val result: String
        when {
            (p == PEEKED_UNQUOTED) -> {
                result = nextUnquotedValue()
            }
            (p == PEEKED_SINGLE_QUOTED) -> {
                result = nextQuotedValue('\'')
            }
            (p == PEEKED_DOUBLE_QUOTED) -> {
                result = nextQuotedValue('"')
            }
            (p == PEEKED_BUFFERED) -> {
                result = peekedString ?: throw NullPointerException("peeked string is null.")
                peekedString = null
            }
            (p == PEEKED_LONG) -> {
                result = peekedLong.toString()
            }
            (p == PEEKED_NUMBER) -> {
                result = buffer.concatToString(pos, pos + peekedNumberLength)
                pos += peekedNumberLength
            }
            else -> {
                throw IllegalStateException("Expected a string but was " + peek() + locationString())
            }
        }
        peeked = PEEKED_NONE
        pathIndices[stackSize - 1]++
        return result
    }

    /**
     * Returns the [boolean][com.google.gson.stream.JsonToken.BOOLEAN] value of the next token,
     * consuming it.
     *
     * @throws IllegalStateException if the next token is not a boolean or if
     * this reader is closed.
     */
    actual fun nextBoolean(): Boolean {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        if (p == PEEKED_TRUE) {
            peeked = PEEKED_NONE
            pathIndices[stackSize - 1]++
            return true
        } else if (p == PEEKED_FALSE) {
            peeked = PEEKED_NONE
            pathIndices[stackSize - 1]++
            return false
        }
        throw IllegalStateException("Expected a boolean but was " + peek() + locationString())
    }

    /**
     * Consumes the next token from the JSON stream and asserts that it is a
     * literal null.
     *
     * @throws IllegalStateException if the next token is not null or if this
     * reader is closed.
     */
    actual fun nextNull() {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        if (p == PEEKED_NULL) {
            peeked = PEEKED_NONE
            pathIndices[stackSize - 1]++
        } else {
            throw IllegalStateException("Expected null but was " + peek() + locationString())
        }
    }

    /**
     * Returns the [double][com.google.gson.stream.JsonToken.NUMBER] value of the next token,
     * consuming it. If the next token is a string, this method will attempt to
     * parse it as a double using [Double.parseDouble].
     *
     * @throws IllegalStateException if the next token is not a literal value.
     * @throws NumberFormatException if the next literal value cannot be parsed
     * as a double, or is non-finite.
     */
    actual fun nextDouble(): Double {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }

        if (p == PEEKED_LONG) {
            peeked = PEEKED_NONE
            pathIndices[stackSize - 1]++
            return peekedLong.toDouble()
        }

        if (p == PEEKED_NUMBER) {
            peekedString = buffer.concatToString(pos, pos + peekedNumberLength)
            pos += peekedNumberLength
        } else if (p == PEEKED_SINGLE_QUOTED || p == PEEKED_DOUBLE_QUOTED) {
            peekedString = nextQuotedValue(if (p == PEEKED_SINGLE_QUOTED) '\'' else '"')
        } else if (p == PEEKED_UNQUOTED) {
            peekedString = nextUnquotedValue()
        } else if (p != PEEKED_BUFFERED) {
            throw IllegalStateException("Expected a double but was " + peek() + locationString())
        }

        peeked = PEEKED_BUFFERED
        val result = peekedString!!.toDouble() // don't catch this NumberFormatException.
        if (!isLenient && (result.isNaN() || result.isInfinite())) {
            throw MalformedJsonException(
                "JSON forbids NaN and infinities: " + result + locationString()
            )
        }
        peekedString = null
        peeked = PEEKED_NONE
        pathIndices[stackSize - 1]++
        return result
    }

    /**
     * Returns the [long][com.google.gson.stream.JsonToken.NUMBER] value of the next token,
     * consuming it. If the next token is a string, this method will attempt to
     * parse it as a long. If the next token's numeric value cannot be exactly
     * represented by a Java `long`, this method throws.
     *
     * @throws IllegalStateException if the next token is not a literal value.
     * @throws NumberFormatException if the next literal value cannot be parsed
     * as a number, or exactly represented as a long.
     */
    actual fun nextLong(): Long {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }

        if (p == PEEKED_LONG) {
            peeked = PEEKED_NONE
            pathIndices[stackSize - 1]++
            return peekedLong
        }

        if (p == PEEKED_NUMBER) {
            peekedString = buffer.concatToString(pos, pos + peekedNumberLength)
            pos += peekedNumberLength
        } else if (p == PEEKED_SINGLE_QUOTED || p == PEEKED_DOUBLE_QUOTED || p == PEEKED_UNQUOTED) {
            peekedString = if (p == PEEKED_UNQUOTED) {
                nextUnquotedValue()
            } else {
                nextQuotedValue(if (p == PEEKED_SINGLE_QUOTED) '\'' else '"')
            }
            try {
                val result = peekedString!!.toLong()
                peeked = PEEKED_NONE
                pathIndices[stackSize - 1]++
                return result
            } catch (ignored: NumberFormatException) {
                // Fall back to parse as a double below.
            }
        } else {
            throw IllegalStateException("Expected a long but was " + peek() + locationString())
        }

        peeked = PEEKED_BUFFERED
        val asDouble = peekedString!!.toDouble() // don't catch this NumberFormatException.
        val result = asDouble.toLong()
        if (result.toDouble() != asDouble) { // Make sure no precision was lost casting to 'long'.
            throw NumberFormatException("Expected a long but was " + peekedString + locationString())
        }
        peekedString = null
        peeked = PEEKED_NONE
        pathIndices[stackSize - 1]++
        return result
    }

    /**
     * Returns the string up to but not including `quote`, unescaping any
     * character escape sequences encountered along the way. The opening quote
     * should have already been read. This consumes the closing quote, but does
     * not include it in the returned string.
     *
     * @param quote either ' or ".
     * @throws NumberFormatException if any unicode escape sequences are
     * malformed.
     */
    private fun nextQuotedValue(quote: Char): String {
        // Like nextNonWhitespace, this uses locals 'p' and 'l' to save inner-loop field access.
        val buffer = buffer
        var builder: StringBuilder? = null
        while (true) {
            var p = pos
            var l = limit
            /* the index of the first character not yet appended to the builder. */
            var start = p
            while (p < l) {
                val c = buffer[p++]

                when {
                    (c == quote) -> {
                        pos = p
                        val len = p - start - 1
                        return if (builder == null) {
                            buffer.concatToString(start, start + len)
                        } else {
                            builder.append(buffer, start, len)
                            builder.toString()
                        }
                    }
                    (c == '\\') -> {
                        pos = p
                        val len = p - start - 1
                        if (builder == null) {
                            val estimatedLength = (len + 1) * 2
                            builder = StringBuilder(max(estimatedLength, 16))
                        }
                        builder.append(buffer, start, len)
                        builder.append(readEscapeCharacter())
                        p = pos
                        l = limit
                        start = p
                    }
                    (c == '\n') -> {
                        lineNumber++
                        lineStart = p
                    }
                }
            }

            if (builder == null) {
                val estimatedLength = (p - start) * 2
                builder = StringBuilder(max(estimatedLength, 16))
            }
            builder.append(buffer, start, p - start)
            pos = p
            if (!fillBuffer(1)) {
                throw syntaxError("Unterminated string")
            }
        }
    }

    /**
     * Returns an unquoted value as a string.
     */
    private fun nextUnquotedValue(): String {
        var builder: StringBuilder? = null
        var i = 0
        findNonLiteralCharacter@ while (true) {
            while (pos + i < limit) {
                when (buffer[pos + i]) {
                    '/', '\\', ';', '#', '=' -> {
                        checkLenient() // fall-through
                        break@findNonLiteralCharacter
                    }
                    '{', '}', '[', ']', ':', ',', ' ', '\t', '\u000C', '\r', '\n' -> {
                        break@findNonLiteralCharacter
                    }
                }
                i++
            }

            // Attempt to load the entire literal into the buffer at once.
            if (i < buffer.size) {
                if (fillBuffer(i + 1)) {
                    continue
                } else {
                    break
                }
            }

            // use a StringBuilder when the value is too long. This is too long to be a number!
            if (builder == null) {
                builder = StringBuilder(max(i, 16))
            }
            builder.append(buffer, pos, i)
            pos += i
            i = 0
            if (!fillBuffer(1)) {
                break
            }
        }

        val result =
            builder?.append(buffer, pos, i)?.toString() ?: buffer.concatToString(pos, pos + i)
        pos += i
        return result
    }

    private fun skipQuotedValue(quote: Char) {
        // Like nextNonWhitespace, this uses locals 'p' and 'l' to save inner-loop field access.
        val buffer = buffer
        do {
            var p = pos
            var l = limit
            /* the index of the first character not yet appended to the builder. */
            while (p < l) {
                val c = buffer[p++]
                when {
                    (c == quote) -> {
                        pos = p
                        return
                    }
                    (c == '\\') -> {
                        pos = p
                        readEscapeCharacter()
                        p = pos
                        l = limit
                    }
                    (c == '\n') -> {
                        lineNumber++
                        lineStart = p
                    }
                }
            }
            pos = p
        } while (fillBuffer(1))

        throw syntaxError("Unterminated string")
    }

    private fun skipUnquotedValue() {
        do {
            var i = 0
            while (pos + i < limit) {
                when (buffer[pos + i]) {
                    '/', '\\', ';', '#', '=' -> {
                        checkLenient() // fall-through
                        pos += i
                        return
                    }
                    '{', '}', '[', ']', ':', ',', ' ', '\t', '\u000C', '\r', '\n' -> {
                        pos += i
                        return
                    }
                }
                i++
            }
            pos += i
        } while (fillBuffer(1))
    }

    /**
     * Returns the [int][com.google.gson.stream.JsonToken.NUMBER] value of the next token,
     * consuming it. If the next token is a string, this method will attempt to
     * parse it as an int. If the next token's numeric value cannot be exactly
     * represented by a Java `int`, this method throws.
     *
     * @throws IllegalStateException if the next token is not a literal value.
     * @throws NumberFormatException if the next literal value cannot be parsed
     * as a number, or exactly represented as an int.
     */
    actual fun nextInt(): Int {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }

        var result: Int
        if (p == PEEKED_LONG) {
            result = peekedLong.toInt()
            if (peekedLong != result.toLong()) { // Make sure no precision was lost casting to 'int'.
                throw NumberFormatException("Expected an int but was " + peekedLong + locationString())
            }
            peeked = PEEKED_NONE
            pathIndices[stackSize - 1]++
            return result
        }

        if (p == PEEKED_NUMBER) {
            peekedString = buffer.concatToString(pos, pos + peekedNumberLength)
            pos += peekedNumberLength
        } else if (p == PEEKED_SINGLE_QUOTED || p == PEEKED_DOUBLE_QUOTED || p == PEEKED_UNQUOTED) {
            peekedString = if (p == PEEKED_UNQUOTED) {
                nextUnquotedValue()
            } else {
                nextQuotedValue(if (p == PEEKED_SINGLE_QUOTED) '\'' else '"')
            }
            try {
                result = peekedString!!.toInt()
                peeked = PEEKED_NONE
                pathIndices[stackSize - 1]++
                return result
            } catch (ignored: NumberFormatException) {
                // Fall back to parse as a double below.
            }
        } else {
            throw IllegalStateException("Expected an int but was " + peek() + locationString())
        }

        peeked = PEEKED_BUFFERED
        val asDouble = peekedString!!.toDouble() // don't catch this NumberFormatException.
        result = asDouble.toInt()
        if (result.toDouble() != asDouble) { // Make sure no precision was lost casting to 'int'.
            throw NumberFormatException("Expected an int but was " + peekedString + locationString())
        }
        peekedString = null
        peeked = PEEKED_NONE
        pathIndices[stackSize - 1]++
        return result
    }

    /**
     * Closes this JSON reader and the underlying [java.io.Reader].
     */
    actual fun close() {
        peeked = PEEKED_NONE
        stack[0] = JsonScope.CLOSED
        stackSize = 1
        input.close()
    }

    /**
     * Skips the next value recursively. If it is an object or array, all nested
     * elements are skipped. This method is intended for use when the JSON token
     * stream contains unrecognized or unhandled values.
     */
    actual fun skipValue() {
        var count = 0
        do {
            var p = peeked
            if (p == PEEKED_NONE) {
                p = doPeek()
            }

            if (p == PEEKED_BEGIN_ARRAY) {
                push(JsonScope.EMPTY_ARRAY)
                count++
            } else if (p == PEEKED_BEGIN_OBJECT) {
                push(JsonScope.EMPTY_OBJECT)
                count++
            } else if (p == PEEKED_END_ARRAY) {
                stackSize--
                count--
            } else if (p == PEEKED_END_OBJECT) {
                stackSize--
                count--
            } else if (p == PEEKED_UNQUOTED_NAME || p == PEEKED_UNQUOTED) {
                skipUnquotedValue()
            } else if (p == PEEKED_SINGLE_QUOTED || p == PEEKED_SINGLE_QUOTED_NAME) {
                skipQuotedValue('\'')
            } else if (p == PEEKED_DOUBLE_QUOTED || p == PEEKED_DOUBLE_QUOTED_NAME) {
                skipQuotedValue('"')
            } else if (p == PEEKED_NUMBER) {
                pos += peekedNumberLength
            }
            peeked = PEEKED_NONE
        } while (count != 0)

        pathIndices[stackSize - 1]++
        pathNames[stackSize - 1] = "null"
    }

    private fun push(newTop: Int) {
        if (stackSize == stack.size) {
            val newLength = stackSize * 2
            stack = stack.copyOf(newLength)
            pathIndices = pathIndices.copyOf(newLength)
            pathNames = pathNames.copyOf(newLength)
        }
        stack[stackSize++] = newTop
    }

    /**
     * Returns true once `limit - pos >= minimum`. If the data is
     * exhausted before that many characters are available, this returns
     * false.
     */
    private fun fillBuffer(minimum: Int): Boolean {
        var min = minimum
        val buffer = buffer
        lineStart -= pos
        if (limit != pos) {
            limit -= pos
            buffer.copyInto(buffer, 0, pos, pos + limit)
        } else {
            limit = 0
        }

        pos = 0
        var total: Long
        buffer.map { it.code.toUByte() }.toUByteArray().usePinned {

            fun read() = input.read(it.addressOf(limit), (buffer.size - limit).convert())

            fun UByteArray.copyInto(charArray: CharArray) {
                map { uByte -> uByte.toInt().toChar() }
                    .toCharArray()
                    .copyInto(charArray)
            }

            total = read()
            while (total != -1L) {
                limit += total.toInt()

                // if this is the first read, consume an optional byte order mark (BOM) if it exists
                if (lineNumber == 0 &&
                    lineStart == 0 &&
                    limit > 0 &&
                    it.get()[0].toInt().toChar() == '\ufeff'
                ) {
                    pos++
                    lineStart++
                    min++
                }
                if (limit >= min) {
                    it.get().copyInto(buffer)
                    return true
                }

                total = read()
            }

            it.get().copyInto(buffer)
        }

        return false
    }

    /**
     * Returns the next character in the stream that is neither whitespace nor a
     * part of a comment. When this returns, the returned character is always at
     * `buffer[pos-1]`; this means the caller can always push back the
     * returned character by decrementing `pos`.
     */
    private fun nextNonWhitespace(throwOnEof: Boolean): Int {
        /*
         * This code uses ugly local variables 'p' and 'l' representing the 'pos'
         * and 'limit' fields respectively. Using locals rather than fields saves
         * a few field reads for each whitespace character in a pretty-printed
         * document, resulting in a 5% speedup. We need to flush 'p' to its field
         * before any (potentially indirect) call to fillBuffer() and reread both
         * 'p' and 'l' after any (potentially indirect) call to the same method.
         */
        val buffer = buffer
        var p = pos
        var l = limit
        while (true) {
            if (p == l) {
                pos = p
                if (!fillBuffer(1)) {
                    break
                }
                p = pos
                l = limit
            }

            val c = buffer[p++]
            if (c == '\n') {
                lineNumber++
                lineStart = p
                continue
            } else if (c == ' ' || c == '\r' || c == '\t') {
                continue
            }

            if (c == '/') {
                pos = p
                if (p == l) {
                    pos-- // push back '/' so it's still in the buffer when this method returns
                    val charsLoaded = fillBuffer(2)
                    pos++ // consume the '/' again
                    if (!charsLoaded) {
                        return c.code
                    }
                }

                checkLenient()
                return when (buffer[pos]) {
                    '*' -> {
                        // skip a /* c-style comment */
                        pos++
                        if (!skipTo("*/")) {
                            throw syntaxError("Unterminated comment")
                        }
                        p = pos + 2
                        l = limit
                        continue
                    }
                    '/' -> {
                        // skip a // end-of-line comment
                        pos++
                        skipToEndOfLine()
                        p = pos
                        l = limit
                        continue
                    }
                    else -> c.code
                }
            } else if (c == '#') {
                pos = p
                /*
                 * Skip a # hash end-of-line comment. The JSON RFC doesn't
                 * specify this behaviour, but it's required to parse
                 * existing documents. See http://b/2571423.
                 */checkLenient()
                skipToEndOfLine()
                p = pos
                l = limit
            } else {
                pos = p
                return c.code
            }
        }

        return if (throwOnEof) {
            throw Exception("End of input" + locationString())
        } else {
            -1
        }
    }

    private fun checkLenient() {
        if (!isLenient) {
            throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON")
        }
    }

    /**
     * Advances the position until after the next newline character. If the line
     * is terminated by "\r\n", the '\n' must be consumed as whitespace by the
     * caller.
     */
    private fun skipToEndOfLine() {
        while (pos < limit || fillBuffer(1)) {
            val c = buffer[pos++]
            if (c == '\n') {
                lineNumber++
                lineStart = pos
                break
            } else if (c == '\r') {
                break
            }
        }
    }

    /**
     * @param toFind a string to search for. Must not contain a newline.
     */
    private fun skipTo(toFind: String): Boolean {
        val length = toFind.length
        outer@ while (pos + length <= limit || fillBuffer(length)) {
            if (buffer[pos] == '\n') {
                lineNumber++
                lineStart = pos + 1
                pos++
                continue
            }
            for (c in 0 until length) {
                if (buffer[pos + c] != toFind[c]) {
                    pos++
                    continue@outer
                }
            }

            return true
        }

        return false
    }

    override fun toString(): String {
        return JsonReader::class.simpleName + locationString()
    }

    private fun locationString(): String {
        val line = lineNumber + 1
        val column = pos - lineStart + 1
        return " at line $line column $column path ${getPath()}"
    }

    private fun getPath(usePreviousPath: Boolean): String {
        val result = StringBuilder().append('$')
        for (i in 0 until stackSize) {
            when (stack[i]) {
                JsonScope.EMPTY_ARRAY,
                JsonScope.NONEMPTY_ARRAY -> {
                    var pathIndex = pathIndices[i]
                    // If index is last path element it points to next array element; have to decrement
                    if (usePreviousPath && pathIndex > 0 && i == stackSize - 1) {
                        pathIndex--
                    }
                    result.append('[').append(pathIndex).append(']')
                }
                JsonScope.EMPTY_OBJECT,
                JsonScope.DANGLING_NAME,
                JsonScope.NONEMPTY_OBJECT -> {
                    result.append('.')
                    if (pathNames[i] != null) {
                        result.append(pathNames[i])
                    }
                }
                JsonScope.NONEMPTY_DOCUMENT,
                JsonScope.EMPTY_DOCUMENT,
                JsonScope.CLOSED -> Unit
            }
        }

        return result.toString()
    }

    /**
     * Returns a [JsonPath](https://goessner.net/articles/JsonPath/)
     * in *dot-notation* to the next (or current) location in the JSON document:
     *
     *  * For JSON arrays the path points to the index of the next element (even
     * if there are no further elements).
     *  * For JSON objects the path points to the last property, or to the current
     * property if its value has not been consumed yet.
     *
     *
     *
     * This method can be useful to add additional context to exception messages
     * *before* a value is consumed, for example when the [peeked][.peek]
     * token is unexpected.
     */
    actual fun getPath(): String {
        return getPath(false)
    }

    /**
     * Unescapes the character identified by the character or characters that
     * immediately follow a backslash. The backslash '\' should have already
     * been read. This supports both unicode escapes "u000A" and two-character
     * escapes "\n".
     *
     * @throws NumberFormatException if any unicode escape sequences are
     * malformed.
     */
    private fun readEscapeCharacter(): Char {
        if (pos == limit && !fillBuffer(1)) {
            throw syntaxError("Unterminated escape sequence")
        }
        return when (val escaped = buffer[pos++]) {
            'u' -> {
                if (pos + 4 > limit && !fillBuffer(4)) {
                    throw syntaxError("Unterminated escape sequence")
                }
                // Equivalent to Integer.parseInt(stringPool.get(buffer, pos, 4), 16);
                var result = 0.toChar()
                var i = pos
                val end = i + 4
                while (i < end) {
                    val c = buffer[i]
                    result = (result.code shl 4).toChar()
                    result += when {
                        (c in '0'..'9') -> {
                            (c - '0')
                        }
                        (c in 'a'..'f') -> {
                            (c - 'a' + 10)
                        }
                        (c in 'A'..'F') -> {
                            (c - 'A' + 10)
                        }
                        else -> {
                            throw NumberFormatException("\\u" + buffer.concatToString(pos, pos + 4))
                        }
                    }
                    i++
                }
                pos += 4
                result
            }
            't' -> '\t'
            'b' -> '\b'
            'n' -> '\n'
            'r' -> '\r'
            'f' -> '\u000C' // this is '\f' but kotlin not support it
            '\n' -> {
                lineNumber++
                lineStart = pos
                escaped
            }
            '\'', '"', '\\', '/' -> escaped
            else -> throw syntaxError("Invalid escape sequence")
        }
    }

    /**
     * Throws a new IO exception with the given message and a context snippet
     * with this reader's content.
     */
    private fun syntaxError(message: String): Exception {
        throw MalformedJsonException(message + locationString())
    }

    /**
     * Consumes the non-execute prefix if it exists.
     */
    private fun consumeNonExecutePrefix() {
        // fast forward through the leading whitespace
        nextNonWhitespace(true)
        pos--
        if (pos + 5 > limit && !fillBuffer(5)) {
            return
        }
        val p = pos
        val buf = buffer
        if (buf[p] != ')' || buf[p + 1] != ']' || buf[p + 2] != '}' || buf[p + 3] != '\'' || buf[p + 4] != '\n') {
            return  // not a security token!
        }

        // we consumed a security token!
        pos += 5
    }

    private fun StringBuilder.append(charArray: CharArray, offset: Int, len: Int): StringBuilder {
        val arrString = charArray.concatToString(offset, offset + len)
        return append(arrString)
    }

    companion object {
        private const val MIN_INCOMPLETE_INTEGER = Long.MIN_VALUE / 10
        private const val PEEKED_NONE = 0
        private const val PEEKED_BEGIN_OBJECT = 1
        private const val PEEKED_END_OBJECT = 2
        private const val PEEKED_BEGIN_ARRAY = 3
        private const val PEEKED_END_ARRAY = 4
        private const val PEEKED_TRUE = 5
        private const val PEEKED_FALSE = 6
        private const val PEEKED_NULL = 7
        private const val PEEKED_SINGLE_QUOTED = 8
        private const val PEEKED_DOUBLE_QUOTED = 9
        private const val PEEKED_UNQUOTED = 10

        /**
         * When this is returned, the string value is stored in peekedString.
         */
        private const val PEEKED_BUFFERED = 11
        private const val PEEKED_SINGLE_QUOTED_NAME = 12
        private const val PEEKED_DOUBLE_QUOTED_NAME = 13
        private const val PEEKED_UNQUOTED_NAME = 14

        /**
         * When this is returned, the integer value is stored in peekedLong.
         */
        private const val PEEKED_LONG = 15
        private const val PEEKED_NUMBER = 16
        private const val PEEKED_EOF = 17

        /* State machine when parsing numbers */
        private const val NUMBER_CHAR_NONE = 0
        private const val NUMBER_CHAR_SIGN = 1
        private const val NUMBER_CHAR_DIGIT = 2
        private const val NUMBER_CHAR_DECIMAL = 3
        private const val NUMBER_CHAR_FRACTION_DIGIT = 4
        private const val NUMBER_CHAR_EXP_E = 5
        private const val NUMBER_CHAR_EXP_SIGN = 6
        private const val NUMBER_CHAR_EXP_DIGIT = 7
        const val BUFFER_SIZE = 1024
    }
}
