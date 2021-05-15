package parsix.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ParseEvalThenKtTest {
    sealed class Attribute
    data class IntAttribute(val value: Int) : Attribute()
    data class StrAttribute(val value: String) : Attribute()

    enum class AttrType(override val key: String) : ParsableEnum {
        IntType("int"),
        StrType("str"),
    }

    @Test
    fun `it parses the int type`() {
        assertEquals(
            Ok(IntAttribute(10)),
            this.mkParse().invoke(
                this.mkMap("int", 10)
            )
        )
    }

    @Test
    fun `it parses the str type`() {
        assertEquals(
            Ok(StrAttribute("hello")),
            this.mkParse().invoke(
                this.mkMap("str", "hello")
            )
        )
    }

    @Test
    fun `it fails when next parse fails`() {
        assertEquals(
            KeyError("val", TypedError(10, "string")),
            this.mkParse().invoke(
                this.mkMap("str", 10)
            )
        )
    }

    private fun <T> mkMap(type: String, value: T) =
        mapOf("type" to type, "val" to value)

    private fun mkParse(): ParseMap<Attribute> {
        val parseType = notNullable(
            ::parseString.then(
                parseEnum<AttrType>()
            )
        )
        val parseIntAttr = notNullable(
            ::parseInt.map(::IntAttribute)
        )
        val parseStrAttr = notNullable(
            ::parseString.map(::StrAttribute)
        )

        return parseKey("type", parseType)
            .evalThen {
                when (it) {
                    AttrType.IntType ->
                        parseKey("val", parseIntAttr)
                    AttrType.StrType ->
                        parseKey("val", parseStrAttr)
                }
            }
    }
}