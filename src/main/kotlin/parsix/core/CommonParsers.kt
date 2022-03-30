package parsix.core

import parsix.fp.result.Failure
import parsix.fp.result.Ok

/**
 * The most basic parse, it will always succeed with [result]
 */
fun <I, O> succeed(result: O): Parse<I, O> =
    { _ -> Ok(result) }

/**
 * Enhance [parse] so that it can handle a nullable input.
 * The final parser will return [RequiredError] if the input is null.
 */
inline fun <I : Any, O> notNullable(crossinline parse: Parse<I, O>): Parse<I?, O> =
    { inp ->
        if (inp == null)
            Failure(RequiredError)
        else
            parse(inp)
    }

/**
 * Enhance [parse] so that it can handle a nullable input.
 * If the input is null, it will use [default] as value.
 */
inline fun <I : Any, O : Any> nullable(
    default: O,
    crossinline parse: Parse<I, O>
): Parse<I?, O> =
    { inp ->
        if (inp == null)
            Ok(default)
        else
            parse(inp)
    }

/**
 * Enhance [parse] so that it can handle a nullable input.
 * If the input is null, that will be the result.
 */
inline fun <I : Any, O : Any> nullable(crossinline parse: Parse<I, O>): Parse<I?, O?> =
    { inp ->
        if (inp == null)
            Ok(null)
        else
            parse(inp)
    }

/**
 * Parse [Any] into [String].
 * @return [StringError] in case of failure.
 */
fun parseString(inp: Any): Parsed<String> =
    when (inp) {
        is String ->
            Ok(inp)

        is Number ->
            Ok(inp.toString())

        is Char ->
            Ok(inp.toString())

        else ->
            Failure(StringError(inp))
    }

/**
 * Parse [Any] into [Boolean].
 * @return [BoolError] in case of failure.
 */
fun parseBool(inp: Any): Parsed<Boolean> =
    parseTyped(inp, ::BoolError)

/**
 * Generic parser, it can be used to easily convert from [Any] to a specific type [T]
 */
inline fun <reified T> parseTyped(
    inp: Any,
    crossinline mkErr: (Any) -> TerminalError
): Parsed<T> =
    if (inp is T)
        Ok(inp)
    else
        Failure(mkErr(inp))

/**
 * Make a `parse` that ensures a [Comparable] is greater than or equal to [min]
 * and returns [MinError] otherwise
 *
 * A common case is to use it with numbers, for example:
 * ```
 * parseMin(10)(4)      // => MinError(10)
 * parseMin(10.5)(11.0) // => Ok(10.5)
 * ```
 *
 * @see parseBetween if you need a range
 */
fun <T : Comparable<T>> parseMin(min: T): Parse<T, T> = { inp ->
    if (inp < min)
        Failure(MinError(inp, min))
    else
        Ok(inp)
}

/**
 * Make a `parse` that ensures a [Comparable] is less a than or equal to [max]
 * and returns [MaxError] otherwise
 *
 * A common case is to use it with numbers, for example:
 * ```
 * parseMax(10)(4)      // => MaxError(10)
 * parseMin(10.5)(11.0) // => Ok(10.5)
 * ```
 *
 * @see parseBetween if you need a range
 */
fun <T : Comparable<T>> parseMax(max: T): Parse<T, T> = { inp ->
    if (inp > max)
        Failure(MaxError(inp, max))
    else
        Ok(inp)
}

/**
 * Make a `parse` that ensures [Comparable] is between [min] and [max], inclusive,
 * and returns [BetweenError] otherwise.
 *
 * @see parseMin
 * @see parseMax
 */
fun <T : Comparable<T>> parseBetween(min: T, max: T): Parse<T, T> = { inp ->
    when {
        inp < min ->
            Failure(BetweenError(inp, min, max))

        inp > max ->
            Failure(BetweenError(inp, min, max))

        else ->
            Ok(inp)
    }
}

/**
 * Parse an [Any] into a [Int].
 * It supports the following types:
 * - [Int], returns it
 * - [UInt], value must be no greater than [Int.MAX_VALUE], [MaxError] otherwise
 * - [Long], value must be between [Int.MIN_VALUE] and [Int.MAX_VALUE], [BetweenError] otherwise
 * - [Double], value must be between [Int.MIN_VALUE] and [Int.MAX_VALUE], [BetweenError] otherwise
 * - [String], must be a valid int, [IntError] otherwise
 *
 * Anything else will fail with [IntError]
 */
fun parseInt(inp: Any): Parsed<Int> =
    when (inp) {
        is String ->
            try {
                Ok(inp.toInt())
            } catch (ex: NumberFormatException) {
                Failure(IntError(inp))
            }

        is Int ->
            Ok(inp)

        is UInt ->
            if (inp > Int.MAX_VALUE.toUInt())
                Failure(MaxError(inp, Int.MAX_VALUE))
            else
                Ok(inp.toInt())

        is Long ->
            if (Int.MIN_VALUE > inp || Int.MAX_VALUE < inp)
                Failure(BetweenError(inp, Int.MIN_VALUE, Int.MAX_VALUE))
            else
                Ok(inp.toInt())

        is Double ->
            if (Int.MIN_VALUE > inp || Int.MAX_VALUE < inp)
                Failure(BetweenError(inp, Int.MIN_VALUE, Int.MAX_VALUE))
            else
                Ok(inp.toInt())

        else ->
            Failure(IntError(inp))
    }

/**
 * Parse an [Any] into a [UInt].
 * It supports the following types:
 * - [UInt], returns it
 * - [Int], value cannot be less than 0, [MinError] otherwise
 * - [Long], value must be between 0 and [UInt.MAX_VALUE], [BetweenError] otherwise
 * - [String], must be a valid unsigned int, [UIntError] otherwise
 *
 * Anything else will fail with [UIntError]
 */
fun parseUInt(inp: Any): Parsed<UInt> =
    when (inp) {
        is String ->
            try {
                Ok(inp.toUInt())
            } catch (ex: NumberFormatException) {
                Failure(UIntError(inp))
            }

        is UInt ->
            Ok(inp)

        is Int ->
            if (inp < 0)
                Failure(MinError(inp, 0))
            else
                Ok(inp.toUInt())

        is Long ->
            if (inp < 0 || inp > UInt.MAX_VALUE.toLong())
                Failure(BetweenError(inp, UInt.MIN_VALUE, UInt.MAX_VALUE))
            else
                Ok(inp.toUInt())

        is Double ->
            if (inp < UInt.MIN_VALUE.toDouble() || inp > UInt.MAX_VALUE.toDouble())
                Failure(BetweenError(inp, UInt.MIN_VALUE, UInt.MAX_VALUE))
            else
                Ok(inp.toUInt())

        else ->
            Failure(UIntError(inp))
    }

/**
 * Parse an [Any] into a [Long].
 * It supports the following types:
 * - [Long], [Int], [UInt]
 * - [Double], must be between [Long.MIN_VALUE] and [Long.MAX_VALUE], [BetweenError] otherwise
 * - [String], must be a valid long int, [LongError] otherwise
 *
 * Anything else will fail with [LongError]
 */
fun parseLong(inp: Any): Parsed<Long> =
    when (inp) {
        is String ->
            try {
                Ok(inp.toLong())
            } catch (ex: NumberFormatException) {
                Failure(LongError(inp))
            }

        is Long ->
            Ok(inp)

        is UInt ->
            Ok(inp.toLong())

        is Int ->
            Ok(inp.toLong())

        is Double ->
            if (inp < Long.MIN_VALUE.toDouble() || inp > Long.MAX_VALUE.toDouble())
                Failure(BetweenError(inp, Long.MIN_VALUE, Long.MAX_VALUE))
            else
                Ok(inp.toLong())
        else ->
            Failure(LongError(inp))
    }

/**
 * Parse an [Any] into a [Double].
 * It supports the following types:
 * - [Double], [Float], [Int], [UInt], [Long]
 * - [String], must be a valid double, [DoubleError] otherwise
 *
 * Anything else will fail with [DoubleError]
 */
fun parseDouble(inp: Any): Parsed<Double> =
    when (inp) {
        is String ->
            try {
                Ok(inp.toDouble())
            } catch (ex: NumberFormatException) {
                Failure(LongError(inp))
            }

        is Double ->
            Ok(inp)

        is Float ->
            Ok(inp.toDouble())

        is Long ->
            Ok(inp.toDouble())

        is UInt ->
            Ok(inp.toDouble())

        is Int ->
            Ok(inp.toDouble())

        else ->
            Failure(DoubleError(inp))
    }
