package parsix.core

import parsix.result.Result

/**
 * Model the result of a [Parse]
 *
 * @see Ok
 * @see ParseError
 */
typealias Parsed<T> = Result<ParseError, T>

/**
 * Base interface for modeling parse errors
 */
sealed interface ParseError

/**
 * This error will be implemented by all the other errors
 */
interface TerminalError : ParseError

/**
 * This error will be implemented by all errors that wrap other errors
 */
interface CompositeError : ParseError {
    val error: ParseError
}

/**
 * Models a collection of errors, useful for greedy parsers.
 */
class ManyErrors(errors: Set<ParseError>) : ParseError {
    private val errors = errors.toMutableSet()

    /**
     * Add a new error in the set of errors.
     * Please be aware that it will mutate the instance and should be used with care!
     * @return self
     */
    fun add(err: ParseError): ParseError = this.also {
        // this assignment ensures `when` will complain in case there is a missing branch
        @Suppress("UNUSED_VARIABLE")
        val x = when (err) {
            is TerminalError, is CompositeError  ->
                this.errors.add(err)
            is ManyErrors ->
                this.errors.addAll(err.errors)
        }
    }

    fun unwrap(): Set<ParseError> =
        this.errors

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
        is TerminalError, is CompositeError ->
            when (b) {
                is TerminalError, is CompositeError ->
                    ManyErrors(setOf(a, b))

                is ManyErrors ->
                    b.add(a)
            }

        is ManyErrors ->
            a.add(b)
    }