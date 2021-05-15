package parsix.core

/**
 * @see parseKey
 */
data class KeyError(val key: String, override val error: ParseError) : CompositeError

/**
 * Make a parser that will extract a key from a [Map] and [parse] it.
 * In case of failure, the error will be wrapped into a [KeyError]
 */
fun <O> parseKey(
    key: String,
    parse: Parse<Any?, O>
): Parse<Map<String, Any?>, O> =
    { inp ->
        parse(inp[key]).mapError {
            KeyError(key, it)
        }
    }
