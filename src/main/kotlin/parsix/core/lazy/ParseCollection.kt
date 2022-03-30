package parsix.core.lazy

import parsix.core.IndexError
import parsix.core.Parse
import parsix.core.Parsed
import parsix.fp.result.Ok
import parsix.fp.result.mapError
import parsix.fp.result.onError

fun <I, O> lazyManyOf(
    parse: Parse<I, O>
): Parse<Iterable<I>, List<O>> = parse@{ inp ->
    inp.foldIndexed(
        Ok(ArrayList<O>()) as Parsed<ArrayList<O>>
    ) { i, z, item ->
        lazyLift2(
            z,
            { parse(item).mapError { IndexError(i, it) } }
        ) { zv, iv ->
            zv.add(iv)
            Ok(zv)
        }.onError {
            return@parse it
        }
    }
}
