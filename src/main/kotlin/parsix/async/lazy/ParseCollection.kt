package parsix.async.lazy

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitAll
import parsix.async.CoParse
import parsix.core.IndexError
import parsix.core.ParseError
import parsix.fp.result.Failure
import parsix.fp.result.Ok

data class ParseCancellationException(val failure: Failure<ParseError>) :
    CancellationException()

/**
 * Create a new [CoParse] capable of parsing and running effects over a homogenous
 * collection of items.
 * All items will be parsed asynchronously, one coroutine per item will be dispatched.
 *
 * In case of failure, all coroutines will be immediately cancelled and the first failure
 * will be returned.
 */
suspend fun <I, O> lazyAsyncManyOf(
    parse: CoParse<I, O>
): CoParse<Iterable<I>, List<O>> = { inp ->
    lazyAsyncScope {
        inp
            .mapIndexed { i, item ->
                lazyAsync(
                    { parse(item) },
                    { IndexError(i, it) },
                )
            }
            .awaitAll()
            .let(::Ok)
    }
}