package parsix.async.lazy

import parsix.async.CoParse
import parsix.core.Parsed
import parsix.core.lazy.lazyLift2
import parsix.fp.result.Ok

/**
 * This is the building block for complex data structures.
 *
 * It will [parse] the input and if it succeed will use that value as argument for the
 * wrapped function.
 * This is used together with [coParseKey][parsix.async.coParseKey] and [coParseProp][parsix.async.coParseProp].
 *
 * This combinator will short-circuit and return as soon as the first error is found,
 * for this reason is better to organise your code so that more performant `parse` are run
 * first.
 *
 * Please note that the *last* defined pluck will be executed *first*!
 * ```
 * coParseInto(::MyData.curry())
 *    .lazyAsyncPluck(heavyParse)
 *    .lazyAsyncPluck(quickParse)
 * ```
 * In the case above, `quickParse` will run before `heavyParse`.
 *
 * @see parsix.async.coParseInto
 */
fun <I, A, B> CoParse<I, (A) -> B>.lazyAsyncPluck(parse: CoParse<I, A>): CoParse<I, B> =
    { inp ->
        lazyAsyncScope {
            val pa = lazyAsync { parse(inp) }
            val pf = lazyAsync { this@lazyAsyncPluck(inp) }

            lazyLift2(pa.await(), { pf.await() }) { a, f -> Ok(f(a)) }
        }
    }

@JvmName("lazyAsyncFlatPluck")
fun <I, A, B> CoParse<I, (A) -> Parsed<B>>.lazyAsyncPluck(parse: CoParse<I, A>): CoParse<I, B> =
    { inp ->
        lazyAsyncScope {
            val pa = lazyAsync { parse(inp) }
            val pf = lazyAsync { this@lazyAsyncPluck(inp) }

            lazyLift2(pa.await(), { pf.await() }) { a, f -> f(a) }
        }
    }