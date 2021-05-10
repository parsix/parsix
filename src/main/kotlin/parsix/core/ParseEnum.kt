package parsix.core

interface ParsableEnum {
    val key: String
}

data class EnumArgs(val expected: Set<String>)
inline fun <reified T> parseEnum(): Parse<String, T>
    where T : Enum<T>, T : ParsableEnum {
    val map = buildMap<T>()

    return { inp ->
        map[inp]
            ?.let(::Ok)
            ?: OneError(
                CommonErrors.enumInvalid,
                EnumArgs(map.keys)
            )
    }
}

inline fun <reified T> buildMap(): Map<String, T>
    where T : Enum<T>, T : ParsableEnum =
    mapOf(
        *enumValues<T>()
            .map { enum -> enum.key to enum }
            .toTypedArray()
    )