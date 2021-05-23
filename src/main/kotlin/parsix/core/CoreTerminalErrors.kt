package parsix.core

sealed interface CoreTerminalError : TerminalError

/**
 * @see notNullable
 */
object RequiredError : CoreTerminalError

/**
 * @see parseEnum
 */
data class EnumError(
    val inp: String,
    val expected: Set<String>
) : CoreTerminalError

/**
 * @see parseString
 */
data class StringError(val inp: Any) : CoreTerminalError

/**
 * @see parseBool
 */
data class BoolError(val inp: Any) : CoreTerminalError

/**
 * @see parseInt
 */
data class IntError(val inp: Any) : CoreTerminalError

/**
 * @see parseUInt
 */
data class UIntError(val inp: Any) : CoreTerminalError

/**
 * @see parseDouble
 */
data class DoubleError(val inp: Any) : CoreTerminalError

/**
 * @see parseLong
 */
data class LongError(val inp: Any) : CoreTerminalError

/**
 * @see parseMin
 */
data class MinError<T : Comparable<T>>(
    val inp: Any,
    val min: T
) : CoreTerminalError

/**
 * @see parseMax
 */
data class MaxError<T : Comparable<T>>(
    val inp: Any,
    val max: T
) : CoreTerminalError

/**
 * @see parseBetween
 */
data class BetweenError<T : Comparable<T>>(
    val inp: Any,
    val min: T,
    val max: T
) : CoreTerminalError