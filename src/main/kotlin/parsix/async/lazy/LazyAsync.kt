package parsix.async.lazy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import parsix.core.ParseError
import parsix.core.Parsed
import parsix.fp.result.Failure
import parsix.fp.result.Ok

internal suspend fun <T> lazyAsyncScope(
    block: suspend CoroutineScope.() -> Parsed<T>
): Parsed<T> =
    try {
        coroutineScope(block)
    } catch (ex: ParseCancellationException) {
        ex.failure
    }

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> CoroutineScope.lazyAsync(
    noinline parse: suspend CoroutineScope.() -> Parsed<T>
): Deferred<T> = lazyAsync(
    parse,
    { it }
)

internal inline fun <T> CoroutineScope.lazyAsync(
    noinline parse: suspend CoroutineScope.() -> Parsed<T>,
    crossinline mapFailure: (ParseError) -> ParseError,
): Deferred<T> =
    async {
        when (val parsed = parse()) {
            is Failure ->
                throw ParseCancellationException(
                    Failure(mapFailure(parsed.error))
                )

            is Ok ->
                parsed.value
        }
    }
