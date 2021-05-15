package parsix.core

/**
 * Models an Enum that can be parsed
 *
 * @see parseEnum
 */
interface ParsableEnum {
    val key: String
}

/**
 * @see parseEnum
 */
data class EnumError(val inp: String, val expected: Set<String>) : TerminalError

/**
 * Make a `parse` for Enum [T].
 * The returned parse we will match string value against [ParsableEnum.key]
 * and return [EnumError] in case of failure
 *
 * @sample parsix.core.ParseEnumKtTest
 */
inline fun <reified T> parseEnum(): Parse<String, T>
    where T : Enum<T>, T : ParsableEnum {
    val map = enumValues<T>().associateBy { it.key }

    return { inp -> map[inp]?.let(::Ok) ?: EnumError(inp, map.keys) }
}