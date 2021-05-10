package parsix.test

import parsix.core.OneError
import parsix.core.Parse

import kotlin.test.fail

fun <I, O> neverCalled(): Parse<I, O> =
    { _ -> fail("it shouldn't have been called") }

data class TestError(val error: String) : OneError() {
    companion object {
        fun <I, O> of(err: String): Parse<I, O> =
            { _ -> TestError(err) }
    }
}
