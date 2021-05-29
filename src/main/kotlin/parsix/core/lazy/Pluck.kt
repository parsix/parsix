package parsix.core.lazy

import parsix.core.Parse
import parsix.core.Parsed
import parsix.fp.result.Failure
import parsix.fp.result.Ok

/**
 * This is the building block for complex data structures.
 *
 * It will [parse] the input and if it succeed will use that value as argument for the
 * wrapped function.
 * This is usually used together with [parseKey][parsix.core.parseKey] and [parseProp][parsix.core.parseProp].
 *
 * This combinator will short-circuit and return as soon as the first error is found,
 * for this reason is better to organise your code so that more performant `parse` are run
 * first.
 *
 * Please note that the *last* defined pluck will be executed *first*!
 * ```
 * parseInto(::MyData.curry())
 *    .lazyPluck(heavyParse)
 *    .lazyPluck(quickParse)
 * ```
 * In the case above, `quickParse` will run before `heavyParse`.
 *
 * @see parsix.core.parseInto
 */
fun <I, A, B> Parse<I, (A) -> B>.lazyPluck(parse: Parse<I, A>): Parse<I, B> =
    { inp ->
        lazyLift2(parse(inp), { this(inp) }) { a, f -> Ok(f(a)) }
    }

@JvmName("lazyFlatPluck")
fun <I, A, B> Parse<I, (A) -> Parsed<B>>.lazyPluck(parse: Parse<I, A>): Parse<I, B> =
    { inp ->
        lazyLift2(parse(inp), { this(inp) }) { a, f -> f(a) }
    }

inline fun <A, B, O> lazyLift2(
    pa: Parsed<A>,
    lazyB: () -> Parsed<B>,
    crossinline f: (A, B) -> Parsed<O>
): Parsed<O> =
    when (pa) {
        is Ok ->
            when (val pb = lazyB()) {
                is Ok ->
                    f(pa.value, pb.value)
                is Failure ->
                    pb
            }
        is Failure ->
            pa
    }