package parsix.async.greedy

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import parsix.async.CoParse
import parsix.core.IndexError
import parsix.core.Parsed
import parsix.core.greedy.lift2
import parsix.fp.result.Ok
import parsix.fp.result.mapError

/**
 * Create a new [CoParse] capable of parsing and running effects over a homogenous
 * collection of items.
 * All items will be parsed asynchronously, one coroutine per element will be dispatched.
 *
 * This greedy version will always run over all elements, hence it is only recommended
 * to use it for quick [parse].
 *
 * In case you would like to quickly bail out from extensive computations, please have a
 * look at [lazyAsyncManyOf][parsix.async.lazy.lazyAsyncManyOf]
 */
suspend fun <I, O> asyncManyOf(
    parse: CoParse<I, O>
): CoParse<Iterable<I>, List<O>> = { inp ->
    coroutineScope {
        inp
            .mapIndexed { i, item ->
                async {
                    parse(item).mapError { IndexError(i, it) }
                }
            }
            .awaitAll()
            .fold(
                Ok(ArrayList<O>()) as Parsed<ArrayList<O>>
            ) { z, item ->
                lift2(z, item) { zv, iv ->
                    zv.add(iv)
                    Ok(zv)
                }
            }
    }
}