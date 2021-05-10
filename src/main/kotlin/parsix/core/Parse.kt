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