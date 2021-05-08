package parsix.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail


internal class ParseMapTest {
    data class TestData(val a: String, val b: Int?)

    @Test
    fun `it successfully parse then input`() {
        assertEquals(
            Ok(TestData("Hello", null)),
            parseMap(::TestData.curry())
                .required("1st", ::parseString)
                .optional("snd", ::parseInt)
                .invoke(
                    mapOf(
                        "1st" to "Hello",
                        "snd" to null
                    )
                )
        )
    }

    @Test
    fun `required fields must be present in the input map`() {
        val neverCalled: Parse<Any, String> =
            { _ -> fail("Shouldn't be called") }

        val succeed: Parse<Any, Int> =
            { _ -> Ok(10) }

        assertEquals(
            FieldError("1st", OneError(CommonErrors.required)),
            parseMap(::TestData.curry())
                .required("1st", neverCalled)
                .optional("snd", succeed)
                .invoke(mapOf("snd" to "present"))
        )
    }

    @Test
    fun `optional fields can be omitted`() {
        val succeed: Parse<String, String> =
            { _ -> Ok("") }

        val neverCalled: Parse<String, Int> =
            { _ -> fail("Shouldn't be called") }

        assertEquals(
            Ok(TestData("", null)),
            parseMap(::TestData.curry())
                .required("1st", succeed)
                .optional("snd", neverCalled)
                .invoke(mapOf("1st" to "present"))
        )
    }

    @Test
    fun `it greedily collect errors`() {
        val fail1st: Parse<Any, String> =
            { _ -> OneError("fail first") }

        val fail2nd: Parse<Any, Int> =
            { _ -> OneError("fail second") }

        assertEquals(
            ManyErrors(
                setOf(
                    FieldError("1st", OneError("fail first")),
                    FieldError("snd", OneError("fail second")),
                )
            ),
            parseMap(::TestData.curry())
                .required("1st", fail1st)
                .optional("snd", fail2nd)
                .invoke(
                    mapOf(
                        "1st" to "broken",
                        "snd" to "broken",
                    )
                )
        )
    }
}