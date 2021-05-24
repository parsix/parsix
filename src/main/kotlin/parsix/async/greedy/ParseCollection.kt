package parsix.async.greedy

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import parsix.async.CoParse
import parsix.core.IndexError
import parsix.core.Parsed
import parsix.core.greedy.lift2
import parsix.result.Ok
import parsix.result.mapError

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