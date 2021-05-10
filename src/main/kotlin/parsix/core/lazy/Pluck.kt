package parsix.core.lazy

import parsix.core.Ok
import parsix.core.Parse
import parsix.core.ParseError

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
