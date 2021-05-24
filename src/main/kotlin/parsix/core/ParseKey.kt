package parsix.core

import parsix.result.mapError

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

@JvmName("parseKeyReq")
fun <O> parseKey(
    key: String,
    parse: Parse<Any, O>
): Parse<Map<String, Any?>, O> =
    parseKey(key, notNullable(parse))

@JvmName("parseKeyStr")
fun <O> parseKey(
    key: String,
    parse: Parse<String, O>
): Parse<Map<String, Any?>, O> =
    parseKey(key, notNullable(::parseString.then(parse)))