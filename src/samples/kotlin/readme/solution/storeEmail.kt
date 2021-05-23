package readme.solution

import parsix.core.Ok
import parsix.core.Parse
import parsix.core.ParseError

@JvmInline
value class Email(val email: String)

val parseEmail: Parse<String, Email> =
    TODO("implement parser")

fun storeEmailEndpoint(inp: String) {
    when (val parsed = parseEmail(inp)) {
        is Ok ->
            storeEmail(parsed.value)

        is ParseError ->
            TODO("handle failure")
    }
}

fun storeEmail(email: Email) {
    TODO("store it somewhere")
}