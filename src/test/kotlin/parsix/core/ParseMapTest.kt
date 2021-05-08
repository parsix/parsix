package parsix.core

import kotlin.test.Test
import kotlin.test.assertEquals


internal class ParseMapTest {
    data class TestData(val a: String, val b: Int?)

    @Test
    fun `test parseMap successfully parse it`() {
        assertEquals(
            Ok(TestData("Hello", null)),
            parseMap(::TestData.curry())
                .required("1st", ::parseString)
                .optional("snd", ::parseInt)
                .invoke(mapOf(
                    "1st" to "Hello",
                    "snd" to null
                ))
        )
    }

    @Test
    fun `test parseMap greedily collect errors`() {
        val fail1st: Parse<Any, String> =
            { _ -> OneError("fail first") }

        val fail2nd: Parse<Any, Int> =
            { _ -> OneError("fail second") }

        assertEquals(
            ManyErrors(setOf(
                FieldError("1st", OneError("fail first")),
                FieldError("snd", OneError("fail second")),
            )),
            parseMap(::TestData.curry())
                .required("1st", fail1st)
                .optional("snd", fail2nd)
                .invoke(mapOf(
                    "1st" to "broken",
                    "snd" to "broken",
                ))
        )
    }
}