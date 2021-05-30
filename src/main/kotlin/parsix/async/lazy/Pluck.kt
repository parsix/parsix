package parsix.async.lazy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitAll
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
 *    .lazyAsyncPluck(parseAfter)
 *    .lazyAsyncPluck(parseFirst)
 * ```
 * In the case above, `parseFirst` will run before `parseAfter`.
 *
 * @see parsix.async.coParseInto
 */
fun <I, A, B> CoParse<I, (A) -> B>.lazyCoPluck(parse: CoParse<I, A>): CoParse<I, B> =
    { inp ->
        lazyLift2(parse(inp), { this(inp) }) { a, f -> Ok(f(a)) }
    }

@JvmName("lazyCoFlatPluck")
fun <I, A, B> CoParse<I, (A) -> Parsed<B>>.lazyCoPluck(parse: CoParse<I, A>): CoParse<I, B> =
    { inp ->
        lazyLift2(parse(inp), { this(inp) }) { a, f -> f(a) }
    }

/**
 * Similar to [lazyCoPluck], however all calls to [lazyAsyncPluck] will run asynchronously
 * using [kotlinx.coroutines.async]. In case of failure, all deferred will be cancelled.
 *
 * @see lazyCoPluck
 */
fun <I, A, B> CoParse<I, (A) -> B>.lazyAsyncPluck(parse: CoParse<I, A>): CoParse<I, B> =
    { inp ->
        runBothAsync(
            { parse(inp) },
            { this@lazyAsyncPluck(inp) },
            { a, f -> Ok(f(a)) }
        )
    }

@JvmName("lazyAsyncFlatPluck")
fun <I, A, B> CoParse<I, (A) -> Parsed<B>>.lazyAsyncPluck(parse: CoParse<I, A>): CoParse<I, B> =
    { inp ->
        runBothAsync(
            { parse(inp) },
            { this@lazyAsyncPluck(inp) },
            { a, f -> f(a) }
        )
    }

internal suspend inline fun <A, B, R> runBothAsync(
    noinline a: suspend CoroutineScope.() -> Parsed<A>,
    noinline b: suspend CoroutineScope.() -> Parsed<B>,
    crossinline f: (A, B) -> Parsed<R>
): Parsed<R> =
    lazyAsyncScope {
        val da = lazyAsync(a)
        val db = lazyAsync(b)

        // allows the whole chain to fail fast
        awaitAll(da, db)

        f(da.await(), db.await())
    }