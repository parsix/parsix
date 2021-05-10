package parsix.core

fun <O> parseKey(
    key: String,
    parse: Parse<Any?, O>
): Parse<Map<String, Any?>, O> =
    { inp ->
        parse(inp[key]).mapError {
            FieldError(key, it)
        }
    }
