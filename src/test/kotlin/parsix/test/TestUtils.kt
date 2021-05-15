package parsix.test

import org.junit.jupiter.api.Assertions.fail
import parsix.core.Parse
import parsix.core.TerminalError

fun <I, O> neverCalled(): Parse<I, O> =
    { _ -> fail("it shouldn't have been called") }

data class TestError(val error: String) : TerminalError {
    companion object {
        fun <I, O> of(err: String): Parse<I, O> =
            { _ -> TestError(err) }
    }
}
