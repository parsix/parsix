package readme.abstraction

import parsix.core.Ok
import parsix.core.ParseError
import parsix.core.Parsed

fun <T> handleParsed(parsed: Parsed<T>, happyCase: (T) -> Response): Response =
    when (parsed) {
        is Ok ->
            happyCase(parsed.value)
        is ParseError ->
            buildErrorResponse(parsed)
    }

fun buildErrorResponse(error: ParseError): Response =
    TODO("Convert ParseError into a Response")
