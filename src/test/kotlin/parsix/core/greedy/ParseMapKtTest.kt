package parsix.core.greedy

import parsix.core.FieldError
import parsix.core.ManyErrors
import parsix.core.Ok
import parsix.core.RequiredError
import parsix.core.curry
import parsix.core.parseInt
import parsix.core.parseString
import parsix.core.succeed
import parsix.test.TestError
import parsix.test.neverCalled
import kotlin.test.Test
import kotlin.test.assertEquals


internal class ParseMapKtTest {
    data class TestData(val a: String, val b: Int?)

    @Test
    fun `it successfully parses then input`() {
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
        assertEquals(
            FieldError("1st", RequiredError),
            parseMap(::TestData.curry())
                .required("1st", neverCalled())
                .optional("snd", succeed(10))
                .invoke(mapOf("snd" to "present"))
        )
    }

    @Test
    fun `optional fields can be omitted`() {
        assertEquals(
            Ok(TestData("ok", null)),
            parseMap(::TestData.curry())
                .required("1st", succeed("ok"))
                .optional("snd", neverCalled())
                .invoke(mapOf("1st" to "present"))
        )
    }

    @Test
    fun `it greedily collect errors`() {
        assertEquals(
            ManyErrors(
                setOf(
                    FieldError("1st", TestError("fail first")),
                    FieldError("snd", TestError("fail second")),
                )
            ),
            parseMap(::TestData.curry())
                .required("1st", TestError.of("fail first"))
                .optional("snd", TestError.of("fail second"))
                .invoke(
                    mapOf(
                        "1st" to "broken",
                        "snd" to "broken",
                    )
                )
        )
    }
}