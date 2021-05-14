package parsix.core

/**
 * Focus on an aspect of the input and parse it.
 * In case of failure, it will be mapped accordingly to [mapErr].
 *
 * @sample parsix.core.FocusedParseKtTest
 */
inline fun <I, T, O> focusedParse(
    crossinline focus: (I) -> T,
    crossinline parse: Parse<T, Any?>,
    crossinline mapOk: (I) -> O,
    crossinline mapErr: (I, ParseError) -> ParseError,
): Parse<I, O> = { inp ->
    when (val parsed = parse(focus(inp))) {
        is Ok ->
            Ok(mapOk(inp))
        is ParseError ->
            mapErr(inp, parsed)
    }
}