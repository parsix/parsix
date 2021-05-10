package parsix.core

/**
 * The building block of this library: a simple function that gets a single input
 * and returns a [Parsed]
 */
typealias Parse<I, O> =
        (input: I) -> Parsed<O>

/**
 * Combine two parsers, [parse] will use the parsed value of [this].
 *
 * For example, to convert from an [Any] into an [Enum] we could:
 * ```
 * ::parseString then parseEnum<MyEnum>()
 * ```
 */
infix fun <I, T, O> Parse<I, T>.then(parse: Parse<T, O>): Parse<I, O> =
    { inp ->
        when (val parsed = this(inp)) {
            is Ok ->
                parse(parsed.value)
            is ParseError ->
                parsed
        }
    }

/**
 * Transform a successful input into something else.
 *
 * This is quite useful when you want to provide a parse for a specific type, for example:
 * ```
 * data class Adult(val age: Int)
 * val parseAdult: Parse<Any, Adult> =
 *     ::parseInt.then(parseMin(18)).map(::Adult)
 * ```
 */
inline fun <I, T, O> Parse<I, T>.map(
    crossinline f: (T) -> O
): Parse<I, O> =
    { inp -> this(inp).map(f) }

/**
 * Transform a failure into something else.
 *
 * This is quite useful when you want to provide a specific error type when combining
 * multiple parsers together:
 * ```
 * data class AdultError(val error: OneError) : OneError()
 * val parseAdult: Parse<Any, Int> =
 *     ::parseInt.then(parseMin(18)).mapError(::AdultError)
 * ```
 *
 * This will allow us to provide a more meaningful error message to our clients.
 */
inline fun <I, O> Parse<I, O>.mapError(
    crossinline f: (ParseError) -> ParseError
): Parse<I, O> =
    { inp -> this(inp).mapError(f) }
