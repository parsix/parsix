package parsix.async

import kotlinx.coroutines.delay
import org.junit.jupiter.api.Assertions
import parsix.fp.result.Ok
import parsix.test.TestError

internal fun <T> brokenIn(ms: Long): CoParse<Any, T> =
    { _ ->
        delay(ms)
        Assertions.fail("this should not run")
    }

internal fun <T> failureIn(ms: Long, msg: String = "failed"): CoParse<Any, T> =
    { _ ->
        delay(ms)
        TestError.of(msg)
    }

internal fun <T> succeedIn(ms: Long, value: T): CoParse<Any, T> =
    { _ ->
        delay(ms)
        Ok(value)
    }
