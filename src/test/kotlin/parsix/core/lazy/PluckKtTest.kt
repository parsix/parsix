package parsix.core.lazy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import parsix.core.Ok
import parsix.core.Parse
import parsix.core.curry
import parsix.core.parseInto
import parsix.core.succeed
import parsix.test.TestError

internal class PluckKtTest {
    data class TestData(val a: Int, val b: String)

    @Test
    fun `it correctly parses the input`() {
        assertEquals(
            Ok(TestData(10, "test")),
            parseInto(::TestData.curry())
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
            parseInto(::TestData.curry())
                .lazyPluck(crashParse)
                .lazyPluck(failParse)
                .invoke(mapOf())
        )
    }
}