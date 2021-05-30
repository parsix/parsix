package parsix.async.lazy

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import parsix.async.CoParse
import parsix.async.coParseInto
import parsix.async.coSucceed
import parsix.core.curry
import parsix.fp.result.Ok
import parsix.test.TestError

internal class PluckKtTest {
    @Test
    fun `lazyAsyncPluck fails immediately`() = runBlockingTest {
        val parse =
            coParseInto({ a: Int, b: Int, c: Int -> a + b + c }.curry())
                .lazyAsyncPluck(broken())
                .lazyAsyncPluck(failure())
                .lazyAsyncPluck(broken())

        assertEquals(
            TestError.of<Int>(),
            parse(mapOf())
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
                .lazyCoPluck(broken())
                .lazyCoPluck(failure())
                .lazyCoPluck(coSucceed(3))

        assertEquals(
            TestError.of<Int>(),
            parse(mapOf())
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

    private fun <T> broken(): CoParse<Any, T> =
        { _ ->
            delay(1000)
            fail("this should not run")
        }

    private fun <T> failure(): CoParse<Any, T> =
        { _ ->
            delay(10)
            TestError.of()
        }
}