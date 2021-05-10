package parsix.core

/**
 * @see parseKey
 */
data class FieldError(val key: String, val error: ParseError) : OneError()

/**
 * Make a parser that will extract a key from a [Map] and [parse] it.
 * In case of failure, the error will be wrapped into a [FieldError]
 */
fun <O> parseKey(
    key: String,
    parse: Parse<Any?, O>
): Parse<Map<String, Any?>, O> =
    { inp ->
        parse(inp[key]).mapError {
            FieldError(key, it)
        }
    }
