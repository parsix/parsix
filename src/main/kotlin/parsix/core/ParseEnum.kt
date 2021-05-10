package parsix.core

/**
 * @see parseEnum
 */
interface ParsableEnum {
    val key: String
}

/**
 * @see parseEnum
 */
data class EnumError(val expected: Set<String>) : OneError()

/**
 * Make a `parse` for Enum [T].
 * The returned parse we will match string value against [ParsableEnum.key]
 * and return [EnumError] in case of failure
 */
inline fun <reified T> parseEnum(): Parse<String, T>
    where T : Enum<T>, T : ParsableEnum {
    val map = mapOf(
        *enumValues<T>()
            .map { enum -> enum.key to enum }
            .toTypedArray()
    )

    return { inp -> map[inp]?.let(::Ok) ?: EnumError(map.keys) }
}