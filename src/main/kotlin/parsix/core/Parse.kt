package parsix.core

typealias Parse<I, O> =
    (input: I) -> Parsed<O>

fun <I, O> Parse<I, O>.parse(inp: I): Parsed<O> =
    this(inp)

infix fun <I, T, O> Parse<I, T>.then(parse: Parse<T, O>): Parse<I, O> =
    { inp ->
        when (val parsed = this(inp)) {
            is Ok ->
                parse(parsed.value)
            is ParseError ->
                parsed
        }
    }

fun <I, A, B> Parse<I, (A) -> B>.pluck(parse: Parse<I, A>): Parse<I, B> =
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