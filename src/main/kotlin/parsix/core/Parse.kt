package parsix.core

import parsix.fp.result.Failure
import parsix.fp.result.Ok
import parsix.fp.result.flatMap
import parsix.fp.result.map
import parsix.fp.result.mapError

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
 * @sample samples.Parse.ThenExample
 */
inline infix fun <I, T, O> Parse<I, T>.then(
    crossinline parse: Parse<T, O>
): Parse<I, O> =
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
 *
 * @sample parsix.core.ParseEvalThenKtTest
 */
inline fun <I, T, O> Parse<I, T>.evalThen(
    crossinline next: (inp: T) -> Parse<I, O>
): Parse<I, O> =
    { inp ->
        this(inp).map(next).flatMap { it(inp) }
    }

/**
 * Transform a successful input into something else.
 *
 * This is quite useful when you want to provide a parse for a specific type.
 * @sample samples.Parse.mapExample
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
 *
 * @sample samples.Parse.mapErrorExample
 */
inline fun <I, O> Parse<I, O>.mapError(
    crossinline f: (ParseError) -> ParseError
): Parse<I, O> =
    { inp -> this(inp).mapError(f) }