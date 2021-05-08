package parsix.core

sealed class Parsed<out T>

fun <A, B> Parsed<A>.map(f: (A) -> B): Parsed<B> =
    when (this) {
        is Ok ->
            Ok(f(this.value))
        is ParseError ->
            this
    }
fun <T> Parsed<T>.mapError(f: (ParseError) -> ParseError): Parsed<T> =
    when (this) {
        is Ok ->
            this
        is ParseError ->
            f(this)
    }

data class Ok<T>(val value: T) : Parsed<T>()
sealed class ParseError : Parsed<Nothing>()

data class OneError(
    val error: String,
    val args: Map<String, Any> = emptyMap()
) : ParseError() {
    constructor(error: String, arg: Pair<String, Any>) : this(error, mapOf(arg))
}

data class FieldError(val field: String, val error: ParseError) : ParseError()

data class IndexError(val index: Int, val error: ParseError) : ParseError()

class ManyErrors(errors: Set<ParseError>) : ParseError() {
    private val errors = errors.toMutableSet()

    fun add(err: ParseError): ParseError = this.also {
        when (err) {
            is OneError, is FieldError, is IndexError ->
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
        is OneError, is FieldError, is IndexError ->
            when (b) {
                is OneError, is FieldError, is IndexError ->
                    ManyErrors(setOf(a, b))

                is ManyErrors ->
                    b.add(a)
            }

        is ManyErrors ->
            a.add(b)
    }