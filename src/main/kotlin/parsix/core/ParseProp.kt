package parsix.core

import kotlin.reflect.KProperty1

fun <I, P, O> parseProp(
    prop: KProperty1<I, P>,
    parse: Parse<P, O>
): Parse<I, O> = { inp ->
    parse(prop.get(inp)).mapError {
        FieldError(prop.name, it)
    }
}
