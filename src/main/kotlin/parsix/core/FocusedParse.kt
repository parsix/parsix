package parsix.core

/**
 * Focus on an aspect of the input and parse it.
 * In case of failure, it will be mapped accordingly to [mapErr].
 *
 * @sample parsix.core.FocusedParseKtTest
 */
inline fun <I, T, O> focusedParse(
    crossinline focus: (I) -> T,
    crossinline parse: Parse<T, O>,
    crossinline mapErr: (I, ParseError) -> ParseError,
): Parse<I, O> = { inp ->
    parse(focus(inp)).mapError {
        mapErr(inp, it)
    }
}