package parsix.arrow

import arrow.core.ValidatedNel
import arrow.core.zip
import parsix.core.ParseError

typealias AParse<I, O> = (I) -> ValidatedNel<ParseError, O>

data class User(val name: String, val age: UInt)

val parseUInt: AParse<Any, UInt> =
    TODO("later")

val parseString: AParse<Any, String> =
    TODO("later")

fun parseUser(rawAge: Any, rawName: Any) =
    parseUInt(rawAge).zip(parseString(rawName)) { age, name ->
        User(name, age)
    }

