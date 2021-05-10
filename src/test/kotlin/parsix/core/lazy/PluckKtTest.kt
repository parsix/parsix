package parsix.core.lazy

import parsix.core.Ok
import parsix.core.Parse
import parsix.core.curry
import parsix.core.succeed
import parsix.test.TestError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

internal class PluckKtTest {
    data class TestData(val a: Int, val b: String)

    @Test
    fun `it correctly parses the input`() {
        assertEquals(
            Ok(TestData(10, "test")),
            lazyParseMap(::TestData.curry())
                .lazyPluck(succeed(10))
                .lazyPluck(succeed("test"))
                .invoke(mapOf())
        )
    }

    @Test
    fun `it short-circuits on first error, unwrapping from last one`() {
        val failParse: Parse<Any, String> =
            TestError.of("failure")
        val crashParse: Parse<Any, Int> =
            { _ -> fail("crashed") }

        assertEquals(
            TestError("failure"),
            lazyParseMap(::TestData.curry())
                .lazyPluck(crashParse)
                .lazyPluck(failParse)
                .invoke(mapOf())
        )
    }
}