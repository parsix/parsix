package parsix.core.lazy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import parsix.core.KeyError
import parsix.core.RequiredError
import parsix.core.curry
import parsix.core.parseInt
import parsix.core.parseInto
import parsix.core.parseString
import parsix.core.succeed
import parsix.fp.result.Failure
import parsix.fp.result.Ok
import parsix.test.TestError
import parsix.test.neverCalled

internal class ParseMapKtTest {
    data class TestData(val a: String, val b: Int?)

    @Test
    fun `it successfully parses the input`() {
        assertEquals(
            Ok(TestData("Hello", null)),
            parseInto(::TestData.curry())
                .lazyRequired("1st", ::parseString)
                .lazyOptional("snd", ::parseInt)
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
                .lazyRequired("1st", neverCalled())
                .lazyOptional("snd", succeed(10))
                .invoke(mapOf("snd" to "present"))
        )
    }

    @Test
    fun `optional fields can be omitted`() {
        assertEquals(
            Ok(TestData("ok", null)),
            parseInto(::TestData.curry())
                .lazyRequired("1st", succeed("ok"))
                .lazyOptional("snd", neverCalled())
                .invoke(mapOf("1st" to "present"))
        )
    }

    @Test
    fun `it short-circuits on first error`() {
        assertEquals(
            Failure(KeyError("snd", TestError("fail fast"))),
            parseInto(::TestData.curry())
                .lazyRequired("1st", neverCalled())
                .lazyOptional("snd", TestError.lift("fail fast"))
                .invoke(
                    mapOf(
                        "1st" to "broken",
                        "snd" to "broken",
                    )
                )
        )
    }
}