package parsix.core.greedy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import parsix.core.ManyErrors
import parsix.core.curry
import parsix.core.parseInto
import parsix.core.succeed
import parsix.result.Failure
import parsix.result.Ok
import parsix.test.TestError

internal class PluckKtTest {
    data class TestData(val a: Int, val b: String)

    @Test
    fun `it successfully parses the input`() {
        assertEquals(
            Ok(TestData(10, "test")),
            parseInto(::TestData.curry())
                .greedyPluck(succeed(10))
                .greedyPluck(succeed("test"))
                .invoke(mapOf())
        )
    }

    @Test
    fun `it greedily collects all errors`() {
        assertEquals(
            Failure(ManyErrors(
                setOf(
                    TestError("first"),
                    TestError("second"),
                )
            )),
            parseInto (::TestData.curry())
                .greedyPluck(TestError.lift("first"))
                .greedyPluck(TestError.lift("second"))
                .invoke(mapOf())
        )
    }
}