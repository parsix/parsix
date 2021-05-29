package parsix.async

import parsix.core.PropError
import parsix.fp.result.mapError
import kotlin.reflect.KProperty1

/**
 * Make a parser that will extract a prop from an object [I] and [parse] it.
 * In case of failure, the error will be wrapped into a [PropError]
 */
fun <I, P, O> coParseProp(
    prop: KProperty1<I, P>,
    parse: CoParse<P, O>
): CoParse<I, O> = { inp ->
    parse(prop.get(inp)).mapError {
        PropError(prop, it)
    }
}