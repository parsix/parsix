package parsix.test

import org.junit.jupiter.api.Assertions.fail
import parsix.core.Parse
import parsix.core.Parsed
import parsix.core.TerminalError
import parsix.result.Failure

fun <I, O> neverCalled(): Parse<I, O> =
    { _ -> fail("it shouldn't have been called") }

data class TestError(val error: String) : TerminalError {
    companion object {
        fun <I, O> lift(err: String): Parse<I, O> =
            { _ -> TestError.of(err) }

        fun <O> of(err: String): Parsed<O> =
            Failure(TestError(err))
    }
}
