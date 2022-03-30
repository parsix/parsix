package parsix.core

import parsix.fp.result.Failure
import parsix.fp.result.Ok

/**
 * Models an Enum that can be parsed
 *
 * @see parseEnum
 */
interface ParsableEnum {
    val key: String
}

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

    return { inp -> map[inp]?.let(::Ok) ?: Failure(EnumError(inp, map.keys)) }
}