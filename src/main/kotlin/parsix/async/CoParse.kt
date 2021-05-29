package parsix.async

import parsix.core.Parse
import parsix.core.ParseError
import parsix.core.Parsed
import parsix.fp.result.Failure
import parsix.fp.result.Ok
import parsix.fp.result.map
import parsix.fp.result.mapError

/**
 * Parse input in a coroutine, so that we can efficiently run side effects.
 */
typealias CoParse<I, O> =
    suspend (I) -> Parsed<O>

typealias CoParseMap<O> =
    CoParse<Map<String, Any?>, O>

/**
 * Combine two parsers, [parse] will use the parsed value of [this].
 *
 * For example, to convert from an [Any] into an [Enum] we could:
 * @sample samples.Parse.ThenExample
 */
inline infix fun <I, T, O> CoParse<I, T>.then(
    crossinline parse: CoParse<T, O>
): CoParse<I, O> =
    { inp ->
        when (val parsed = this(inp)) {
            is Ok ->
                parse(parsed.value)
            is Failure ->
                parsed
        }
    }

/**
 * Combine two parsers, [parse] will use the parsed value of [this].
 *
 * For example, to convert from an [Any] into an [Enum] we could:
 */
inline infix fun <I, T, O> Parse<I, T>.then(
    crossinline parse: CoParse<T, O>
): CoParse<I, O> =
    { inp ->
        when (val parsed = this(inp)) {
            is Ok ->
                parse(parsed.value)
            is Failure ->
                parsed
        }
    }

/**
 * Partially evaluate input, returns another parse and apply it to the same initial input.
 *
 * This combinator is useful whenever you need to parse a complex structure where we need
 * to evaluate part of it before being able to understand how to fully parse it.
 *
 * If you are familiar with functional programming, you may know this operator as `flatMap`
 *  or `bind`.
 */
inline fun <I, T, O> CoParse<I, T>.evalThen(
    crossinline next: (inp: T) -> CoParse<I, O>
): CoParse<I, O> =
    { inp ->
        when (val parsed = this(inp)) {
            is Failure ->
                parsed

            is Ok ->
                next(parsed.value)(inp)
        }
    }

/**
 * Transform a successful input into something else.
 *
 * This is quite useful when you want to provide a parse for a specific type.
 */
inline fun <I, T, O> CoParse<I, T>.map(
    crossinline f: (T) -> O
): CoParse<I, O> =
    { inp -> this(inp).map(f) }

/**
 * Transform a failure into something else.
 *
 * This is quite useful when you want to provide a specific error type when combining
 * multiple parsers together:
 */
inline fun <I, O> CoParse<I, O>.mapError(
    crossinline f: (ParseError) -> ParseError
): CoParse<I, O> =
    { inp -> this(inp).mapError(f) }
