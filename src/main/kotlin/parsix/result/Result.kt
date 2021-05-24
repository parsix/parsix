package parsix.result

sealed interface Result<out E, out T>
data class Ok<T>(val value: T) : Result<Nothing, T>
data class Failure<E>(val error: E) : Result<E, Nothing>

/**
 * Transform a successful result into another [Result]
 */
inline fun <E, A, B> Result<E, A>.flatMap(
    crossinline f: (A) -> Result<E, B>
): Result<E, B> =
    when (this) {
        is Ok ->
            f(this.value)
        is Failure ->
            this
    }

/**
 * Transform a successful result into something else
 */
inline fun <E, A, B> Result<E, A>.map(
    crossinline f: (A) -> B
): Result<E, B> =
    when (this) {
        is Ok ->
            Ok(f(this.value))
        is Failure ->
            this
    }

/**
 * Transform a failure into another failure
 */
inline fun <A, B, T> Result<A, T>.mapError(
    crossinline f: (A) -> B
): Result<B, T> =
    when (this) {
        is Ok ->
            this
        is Failure ->
            Failure(f(this.error))
    }