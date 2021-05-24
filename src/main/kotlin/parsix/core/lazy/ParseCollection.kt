package parsix.core.lazy

import parsix.core.IndexError
import parsix.core.Parse
import parsix.core.ParseError
import parsix.core.Parsed
import parsix.result.Ok
import parsix.result.mapError

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
        }.also {
            if (it is ParseError)
                return@parse it
        }
    }
}
