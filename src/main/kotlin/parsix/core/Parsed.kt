package parsix.core

/**
 * Model the result of a [Parse].
 *
 * @see Ok
 * @see ParseError
 */
sealed class Parsed<out T>

/**
 * Transform a successful value into something else
 * @see [Parse.map]
 */
inline fun <A, B> Parsed<A>.map(
    crossinline f: (A) -> B
): Parsed<B> =
    when (this) {
        is Ok ->
            Ok(f(this.value))
        is ParseError ->
            this
    }

/**
 * Transform a failure into another failure
 * @see Parse.mapError
 */
inline fun <T> Parsed<T>.mapError(
    crossinline f: (ParseError) -> ParseError
): Parsed<T> =
    when (this) {
        is Ok ->
            this
        is ParseError ->
            f(this)
    }

/**
 * Model the successful case
 */
data class Ok<T>(val value: T) : Parsed<T>()

/**
 * Base class for modeling the failure case
 */
sealed class ParseError : Parsed<Nothing>()

/**
 * This error will be extended by all the other errors
 */
abstract class OneError : ParseError()

/**
 * Model a collection of errors
 */
class ManyErrors(errors: Set<ParseError>) : ParseError() {
    private val errors = errors.toMutableSet()

    fun add(err: ParseError): ParseError = this.also {
        when (err) {
            is OneError ->
                this.errors.add(err)
            is ManyErrors ->
                this.errors.addAll(err.errors)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }

        other as ManyErrors

        if (errors != other.errors) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        return errors.hashCode()
    }

    override fun toString(): String {
        val errs = this.errors.joinToString("\n") {
            it.toString().prependIndent()
        }
        return "ManyErrors(\n$errs\n)"
    }
}

fun combineErrors(a: ParseError, b: ParseError): ParseError =
    when (a) {
        is OneError ->
            when (b) {
                is OneError ->
                    ManyErrors(setOf(a, b))

                is ManyErrors ->
                    b.add(a)
            }

        is ManyErrors ->
            a.add(b)
    }