package parsix.async.greedy

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import parsix.async.CoParse
import parsix.core.IndexError
import parsix.core.ManyErrors
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
            asyncManyOf(parse)
                .invoke(listOf(3, 2, 1))
        )
        assertEquals(3, currentTime)
    }

    @Test
    fun `it collects all failures`() = runBlockingTest {
        val parse: CoParse<Long, String> = { n ->
            delay(n)
            if (n == 2L)
                Ok("2")
            else
                TestError.of("failed $n")
        }
        assertEquals(
            Failure(
                ManyErrors(
                    setOf(
                        IndexError(0, TestError("failed 3")),
                        IndexError(2, TestError("failed 1")),
                    )
                )
            ),
            asyncManyOf(parse)
                .invoke(listOf(3, 2, 1))
        )
        assertEquals(3, currentTime)
    }
}