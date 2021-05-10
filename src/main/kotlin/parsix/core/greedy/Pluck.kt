package parsix.core.greedy

import parsix.core.Ok
import parsix.core.Parse
import parsix.core.ParseError
import parsix.core.combineErrors

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
