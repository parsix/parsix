package parsix.async.greedy

import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import parsix.async.coParseInto
import parsix.async.failureIn
import parsix.async.succeedIn
import parsix.core.ManyErrors
import parsix.core.curry
import parsix.fp.result.Failure
import parsix.fp.result.Ok
import parsix.test.TestError

internal class PluckKtTest {
    @Test
    fun `it runs all async and returns value when all succeeds`() =
        runBlockingTest {
            val parse =
                coParseInto({ a: Int, b: Int -> a + b }.curry())
                    .greedyAsyncPluck(succeedIn(ms = 100, value = 1))
                    .greedyAsyncPluck(succeedIn(ms = 50, value = 2))

            assertEquals(Ok(3), parse(mapOf()))
            assertEquals(100, currentTime)
        }

    @Test
    fun `it runs all async and collect failures`() = runBlockingTest {
        val parse =
            coParseInto({ a: Int, b: Int, c: Int -> a + b + c }.curry())
                .greedyAsyncPluck(failureIn(ms = 10, msg = "failed in 10ms"))
                .greedyAsyncPluck(succeedIn(ms = 1, value = 10))
                .greedyAsyncPluck(failureIn(ms = 50, msg = "failed in 50ms"))

        assertEquals(
            Failure(
                ManyErrors(
                    setOf(
                        TestError("failed in 10ms"),
                        TestError("failed in 50ms"),
                    )
                )
            ),
            parse(mapOf())
        )
        assertEquals(50, currentTime)
    }
}