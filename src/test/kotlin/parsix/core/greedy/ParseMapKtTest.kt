package parsix.core.greedy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import parsix.core.KeyError
import parsix.core.ManyErrors
import parsix.core.RequiredError
import parsix.core.curry
import parsix.core.parseInt
import parsix.core.parseInto
import parsix.core.parseString
import parsix.core.succeed
import parsix.result.Failure
import parsix.result.Ok
import parsix.test.TestError
import parsix.test.neverCalled


internal class ParseMapKtTest {
    data class TestData(val a: String, val b: Int?)

    @Test
    fun `it successfully parses then input`() {
        assertEquals(
            Ok(TestData("Hello", null)),
            parseInto(::TestData.curry())
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
            Failure(KeyError("1st", RequiredError)),
            parseInto(::TestData.curry())
                .required("1st", neverCalled())
                .optional("snd", succeed(10))
                .invoke(mapOf("snd" to "present"))
        )
    }

    @Test
    fun `optional fields can be omitted`() {
        assertEquals(
            Ok(TestData("ok", null)),
            parseInto(::TestData.curry())
                .required("1st", succeed("ok"))
                .optional("snd", neverCalled())
                .invoke(mapOf("1st" to "present"))
        )
    }

    @Test
    fun `it greedily collect errors`() {
        assertEquals(
            Failure(
                ManyErrors(
                    setOf(
                        KeyError("1st", TestError("fail first")),
                        KeyError("snd", TestError("fail second")),
                    )
                )
            ),
            parseInto(::TestData.curry())
                .required("1st", TestError.lift("fail first"))
                .optional("snd", TestError.lift("fail second"))
                .invoke(
                    mapOf(
                        "1st" to "broken",
                        "snd" to "broken",
                    )
                )
        )
    }
}