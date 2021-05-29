package parsix.async.lazy

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import parsix.async.CoParse
import parsix.core.IndexError
import parsix.fp.result.Failure
import parsix.fp.result.Ok
import parsix.test.TestError

internal class ParseCollectionKtTest {

    @Test
    fun `it returns all parsed elements on success`() = runBlockingTest {
        val parse: CoParse<Long, String> = { n ->
            delay(n)
            Ok(n.toString())
        }
        assertEquals(
            Ok(listOf("3", "2", "1")),
            lazyAsyncManyOf(parse)
                .invoke(listOf(3, 2, 1))
        )
    }

    @Test
    fun `it immediately exit on failure`() = runBlockingTest {
        val parse: CoParse<Long, String> = { n ->
            delay(n)
            if (n == 1L)
                TestError.of("failed")
            else
                fail("this shouldn't happen")
        }
        assertEquals(
            Failure(IndexError(2, TestError("failed"))),
            lazyAsyncManyOf(parse)
                .invoke(listOf(3, 2, 1))
        )
    }
}