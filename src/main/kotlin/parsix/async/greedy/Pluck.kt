package parsix.async.greedy

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import parsix.async.CoParse
import parsix.core.Parsed
import parsix.core.greedy.lift2
import parsix.fp.result.Ok

/**
 * This is the building block for complex data structures.
 *
 * It will [parse] the input and if it succeed will use that value as argument for the
 * wrapped function.
 * This is usually used together with [parseKey][parsix.core.parseKey] and [parseProp][parsix.core.parseProp].
 *
 * This combinator will greedily parse the input and collect all errors into a [ManyErrors][parsix.core.ManyErrors].
 *
 * @see parsix.async.coParseInto
 */
fun <I, A, B> CoParse<I, (A) -> B>.greedyAsyncPluck(
    parse: CoParse<I, A>
): CoParse<I, B> =
    { inp ->
        coroutineScope {
            val df = async { this@greedyAsyncPluck(inp) }
            val da = async { parse(inp) }

            lift2(df.await(), da.await()) { f, a -> Ok(f(a)) }
        }
    }

@JvmName("greedyFlatPluck")
fun <I, A, B> CoParse<I, (A) -> Parsed<B>>.greedyAsyncPluck(
    parse: CoParse<I, A>
): CoParse<I, B> =
    { inp ->
        coroutineScope {
            val df = async { this@greedyAsyncPluck(inp) }
            val da = async { parse(inp) }

            lift2(df.await(), da.await()) { f, a -> f(a) }
        }
    }