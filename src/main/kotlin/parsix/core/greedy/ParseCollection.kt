package parsix.core.greedy

import parsix.core.IndexError
import parsix.core.Ok
import parsix.core.Parse
import parsix.core.Parsed
import parsix.core.mapError

fun <I, O> manyOf(
    parse: Parse<I, O>
): Parse<Iterable<I>, List<O>> = { inp ->
    inp.foldIndexed(
        Ok(ArrayList<O>()) as Parsed<ArrayList<O>>
    ) { i, z, item ->
        lift2(
            z,
            parse(item).mapError { IndexError(i, it) }
        ) { zv, iv ->
            zv.add(iv)
            Ok(zv)
        }
    }
}