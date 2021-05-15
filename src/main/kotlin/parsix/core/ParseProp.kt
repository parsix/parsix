package parsix.core

import kotlin.reflect.KProperty1

/**
 * @see parseProp
 */
data class PropError<K, P>(
    val prop: KProperty1<K, P>,
    override val error: ParseError,
) : CompositeError

/**
 * Make a parser that will extract a prop from an object [I] and [parse] it.
 * In case of failure, the error will be wrapped into a [PropError]
 */
fun <I, P, O> parseProp(
    prop: KProperty1<I, P>,
    parse: Parse<P, O>
): Parse<I, O> = { inp ->
    parse(prop.get(inp)).mapError {
        PropError(prop, it)
    }
}
