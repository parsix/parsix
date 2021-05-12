package parsix.core.greedy

import parsix.core.Ok
import parsix.core.Parse
import parsix.core.ParseError
import parsix.core.combineErrors

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
        val pf = this(inp)
        when (val parsed = parse(inp)) {
            is Ok ->
                when (pf) {
                    is Ok ->
                        Ok(pf.value(parsed.value))
                    is ParseError ->
                        pf
                }
            is ParseError ->
                when (pf) {
                    is Ok ->
                        parsed
                    is ParseError ->
                        combineErrors(pf, parsed)
                }
        }
    }
