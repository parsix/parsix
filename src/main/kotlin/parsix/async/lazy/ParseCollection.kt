package parsix.async.lazy

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import parsix.async.CoParse
import parsix.core.IndexError
import parsix.core.Parsed
import parsix.core.lazy.lazyLift2
import parsix.result.Failure
import parsix.result.Ok
import parsix.result.mapError

suspend fun <I, O> lazyAsyncManyOf(
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
                lazyLift2(z, { item }) { zv, iv ->
                    zv.add(iv)
                    Ok(zv)
                }.also {
                    if (it is Failure<*>)
                        return@coroutineScope it
                }
            }
    }
}