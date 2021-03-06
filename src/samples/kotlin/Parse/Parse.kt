package samples.Parse

import parsix.core.CompositeError
import parsix.core.ParsableEnum
import parsix.core.Parse
import parsix.core.ParseError
import parsix.core.parseEnum
import parsix.core.parseInt
import parsix.core.parseMin
import parsix.core.parseString
import parsix.core.then

internal fun mapExample() {
    data class Adult(val age: Int)

    val parseAdult: Parse<Any, Adult> =
        ::parseInt.then(parseMin(18)).map(::Adult)

}

internal fun mapErrorExample() {
    data class AdultError(override val error: ParseError) : CompositeError

    val parseAdult: Parse<Any, Int> =
        ::parseInt.then(parseMin(18)).mapError(::AdultError)
}

internal class ThenExample() {
    enum class MyEnum(override val key: String) : ParsableEnum {
        A("a"), B("b")
    }

    fun sample() =
        ::parseString then parseEnum<MyEnum>()
}