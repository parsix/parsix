package parsix.async.lazy

import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import parsix.async.brokenIn
import parsix.async.coParseInto
import parsix.async.coSucceed
import parsix.async.failureIn
import parsix.async.succeedIn
import parsix.core.curry
import parsix.fp.result.Ok
import parsix.test.TestError

internal class PluckKtTest {
    @Test
    fun `lazyAsyncPluck fails immediately`() = runBlockingTest {
        val parse =
            coParseInto({ a: Int, b: Int, c: Int -> a + b + c }.curry())
                .lazyAsyncPluck(brokenIn(1000))
                .lazyAsyncPluck(failureIn(10))
                .lazyAsyncPluck(brokenIn(2000))

        assertEquals(
            TestError.of(),
            parse(mapOf())
        )
        assertEquals(
            10,
            currentTime
        )
    }

    @Test
    fun `lazyAsyncPluck returns result when all succeeds`() = runBlockingTest {
        val parse =
            coParseInto({ a: Int, b: Int -> a - b }.curry())
                .lazyAsyncPluck(coSucceed(3))
                .lazyAsyncPluck(coSucceed(2))

        assertEquals(
            Ok(1),
            parse(mapOf())
        )
    }

    @Test
    fun `lazyCoPluck fails on first error, starting from last call`() = runBlockingTest {
        val parse =
            coParseInto({ a: Int, b: Int, c: Int -> a + b + c }.curry())
                .lazyCoPluck(brokenIn(1000))
                .lazyCoPluck(failureIn(10))
                .lazyCoPluck(succeedIn(ms = 100, value = 3))

        assertEquals(
            TestError.of(),
            parse(mapOf())
        )
        assertEquals(
            110,
            currentTime
        )
    }

    @Test
    fun `lazyCoPluck returns result when all succeeds`() = runBlockingTest {
        val parse =
            coParseInto({ a: Int, b: Int -> a + b }.curry())
                .lazyCoPluck(coSucceed(1))
                .lazyCoPluck(coSucceed(2))

        assertEquals(
            Ok(3),
            parse(mapOf())
        )
    }
}