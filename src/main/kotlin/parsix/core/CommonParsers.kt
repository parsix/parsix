package parsix.core

object CommonErrors {
    const val required = "required"
    const val boolInvalid = "bool.invalid"
    const val stringInvalid = "string.invalid"
    const val intInvalid = "int.invalid"
    const val uintInvalid = "unit.invalid"
    const val uintNegative = "unit.negative"

    const val minValue = "min-value"
    const val maxValue = "max-value"
    const val betweenValue = "btwn-value"

    const val enumInvalid = "enum.invalid"
}

fun <I : Any, O> notNullable(parse: Parse<I, O>): Parse<I?, O> =
    { inp ->
        if (inp == null)
            OneError(CommonErrors.required)
        else
            parse(inp)
    }

fun <I : Any, O : Any> nullable(default: O, parse: Parse<I, O>): Parse<I?, O> =
    { inp ->
        if (inp == null)
            Ok(default)
        else
            parse(inp)
    }

fun <I : Any, O : Any> nullable(parse: Parse<I, O>): Parse<I?, O?> =
    { inp ->
        if (inp == null)
            Ok(null)
        else
            parse(inp)
    }

fun parseString(inp: Any): Parsed<String> =
    parseTyped(inp, CommonErrors.stringInvalid)

fun parseBool(inp: Any): Parsed<Boolean> =
    parseTyped(inp, CommonErrors.boolInvalid)

inline fun <reified T> parseTyped(inp: Any, err: String): Parsed<T> =
    if (inp is T)
        Ok(inp)
    else
        OneError(err)

fun parseInt(inp: Any): Parsed<Int> =
    when (inp) {
        is Int ->
            Ok(inp)
        is UInt ->
            Ok(inp.toInt())

        is Long ->
            when {
                Int.MIN_VALUE > inp ->
                    OneError(CommonErrors.minValue, ("min" to Int.MIN_VALUE))
                Int.MAX_VALUE < inp ->
                    OneError(CommonErrors.maxValue, ("max" to Int.MAX_VALUE))
                else ->
                    Ok(inp.toInt())
            }

        is String ->
            try {
                Ok(inp.toInt())
            } catch (ex: NumberFormatException) {
                OneError(CommonErrors.intInvalid)
            }
        else ->
            OneError(CommonErrors.intInvalid)
    }

fun parseUInt(inp: Any): Parsed<UInt> =
    when (inp) {
        is UInt ->
            Ok(inp)
        is Int ->
            if (inp >= 0)
                Ok(inp.toUInt())
            else
                OneError(CommonErrors.uintNegative)

        is Long ->
            when {
                inp < 0 ->
                    OneError(CommonErrors.uintNegative)
                UInt.MAX_VALUE.toLong() < inp ->
                    OneError(CommonErrors.maxValue, ("max" to UInt.MAX_VALUE))
                else ->
                    Ok(inp.toUInt())
            }

        is String ->
            try {
                Ok(inp.toUInt())
            } catch (ex: NumberFormatException) {
                OneError(CommonErrors.uintInvalid)
            }
        else ->
            OneError(CommonErrors.uintInvalid)
    }

fun <T : Comparable<T>> parseMin(min: T): Parse<T, T> = { inp ->
    if (inp < min)
        OneError(CommonErrors.minValue, mapOf("min" to min))
    else
        Ok(inp)
}

fun <T : Comparable<T>> parseMax(max: T): Parse<T, T> = { inp ->
    if (inp > max)
        OneError(CommonErrors.maxValue, mapOf("max" to max))
    else
        Ok(inp)
}

fun <T : Comparable<T>> parseBetween(min: T, max: T): Parse<T, T> = { inp ->
    when {
        inp < min ->
            OneError(
                CommonErrors.betweenValue, mapOf(
                    "min" to min,
                    "max" to max
                )
            )

        inp > max ->
            OneError(
                CommonErrors.betweenValue, mapOf(
                    "min" to min,
                    "max" to max
                )
            )

        else ->
            Ok(inp)
    }
}