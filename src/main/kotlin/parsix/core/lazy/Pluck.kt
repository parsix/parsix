package parsix.core.lazy

import parsix.core.Ok
import parsix.core.Parse
import parsix.core.ParseError

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
 * parseMap(::MyData.curry())
 *    .lazyPluck(heavyParse)
 *    .lazyPluck(quickParse)
 * ```
 * In the case above, `quickParse` will run before `heavyParse`.
 *
 * @see parsix.core.parseInto
 */
fun <I, A, B> Parse<I, (A) -> B>.lazyPluck(parse: Parse<I, A>): Parse<I, B> =
    { inp ->
        when (val parsed = parse(inp)) {
            is Ok ->
                when (val pf = this(inp)) {
                    is Ok ->
                        Ok(pf.value(parsed.value))
                    is ParseError ->
                        pf
                }
            is ParseError ->
                parsed
        }
    }
