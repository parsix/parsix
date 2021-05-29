package parsix.async

import parsix.core.KeyError
import parsix.core.parseString
import parsix.fp.result.mapError

/**
 * Make a parser that will extract a key from a [Map] and [parse] it.
 * In case of failure, the error will be wrapped into a [KeyError]
 */
fun <O> coParseKey(
    key: String,
    parse: CoParse<Any?, O>
): CoParseMap<O> =
    { inp ->
        parse(inp[key]).mapError {
            KeyError(key, it)
        }
    }

@JvmName("parseKeyReq")
fun <O> coParseKey(
    key: String,
    parse: CoParse<Any, O>
): CoParseMap<O> =
    coParseKey(key, coNotNullable(parse))

@JvmName("parseKeyStr")
fun <O> coParseKey(
    key: String,
    parse: CoParse<String, O>
): CoParseMap<O> =
    coParseKey(key, coNotNullable(::parseString.then(parse)))