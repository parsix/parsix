package parsix.core.greedy

import parsix.core.Parse
import parsix.core.Parsed
import parsix.core.combineErrors
import parsix.fp.result.Failure
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
 * @see parseInto
 */
fun <I, A, B> Parse<I, (A) -> B>.greedyPluck(parse: Parse<I, A>): Parse<I, B> =
    { inp ->
        lift2(this(inp), parse(inp)) { f, a -> Ok(f(a)) }
    }

@JvmName("greedyFlatPluck")
fun <I, A, B> Parse<I, (A) -> Parsed<B>>.greedyPluck(parse: Parse<I, A>): Parse<I, B> =
    { inp ->
        lift2(this(inp), parse(inp)) { f, a -> f(a) }
    }

inline fun <A, B, O> lift2(
    pa: Parsed<A>,
    pb: Parsed<B>,
    crossinline f: (A, B) -> Parsed<O>
): Parsed<O> =
    when (pa) {
        is Ok ->
            when (pb) {
                is Ok ->
                    f(pa.value, pb.value)
                is Failure ->
                    pb
            }
        is Failure ->
            when (pb) {
                is Ok ->
                    pa
                is Failure ->
                    Failure(combineErrors(pa.error, pb.error))
            }
    }