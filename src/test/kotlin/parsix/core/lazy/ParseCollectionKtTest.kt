package parsix.core.lazy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import parsix.core.IndexError
import parsix.core.Parse
import parsix.result.Failure
import parsix.result.Ok
import parsix.test.TestError

internal class ParseCollectionKtTest {
    @Test
    fun `it parses all items when successful`() {
        assertEquals(
            Ok(listOf("1", "2", "3")),
            lazyManyOf { inp: Int -> Ok(inp.toString()) }
                .invoke(listOf(1, 2, 3))
        )
    }

    @Test
    fun `it immediately returns error on failure`() {
        val parse: Parse<Int, String> = { inp: Int ->
            if (inp == 2)
                TestError.of("failed")
            else
                Ok(inp.toString())
        }

        assertEquals(
            Failure(IndexError(1, TestError("failed"))),
            lazyManyOf(parse)
                .invoke(listOf(1, 2, 3))
        )
    }
}