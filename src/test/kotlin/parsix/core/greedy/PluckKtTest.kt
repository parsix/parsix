package parsix.core.greedy

import parsix.core.ManyErrors
import parsix.core.Ok
import parsix.core.curry
import parsix.core.succeed
import parsix.test.TestError
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PluckKtTest {
    data class TestData(val a: Int, val b: String)

    @Test
    fun `it successfully parses the input`() {
        assertEquals(
            Ok(TestData(10, "test")),
            parseMap(::TestData.curry())
                .greedyPluck(succeed(10))
                .greedyPluck(succeed("test"))
                .invoke(mapOf())
        )
    }

    @Test
    fun `it greedily collects all errors`() {
        assertEquals(
            ManyErrors(
                setOf(
                    TestError("first"),
                    TestError("second"),
                )
            ),
            parseMap (::TestData.curry())
                .greedyPluck(TestError.of("first"))
                .greedyPluck(TestError.of("second"))
                .invoke(mapOf())
        )
    }
}