package parsix.core.lazy

import parsix.core.KeyError
import parsix.core.Ok
import parsix.core.RequiredError
import parsix.core.curry
import parsix.core.parseInt
import parsix.core.parseInto
import parsix.core.parseString
import parsix.core.succeed
import parsix.test.TestError
import parsix.test.neverCalled
import kotlin.test.Test
import kotlin.test.assertEquals

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
            KeyError("1st", RequiredError),
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
            KeyError("snd", TestError("fail fast")),
            parseInto(::TestData.curry())
                .lazyRequired("1st", neverCalled())
                .lazyOptional("snd", TestError.of("fail fast"))
                .invoke(
                    mapOf(
                        "1st" to "broken",
                        "snd" to "broken",
                    )
                )
        )
    }
}