package parsix.core

/**
 * Create an error handler that can translate a ParseError into a list of strings suitable
 * to be consumed by humans.
 *
 * The error handler will translate all errors provided by [parsix.core] by default,
 * however given the open nature of this structure, any custom events must be translated
 * by either [extendComposite] or [extendTerminal].
 *
 * @param extendComposite transform an custom CompositeError into a prefix. The error handler will append ": " and the error to it.
 * @param extendTerminal transform a custom OneError into a valid message
 * @sample parsix.core.StringErrorHandlerKtTest
 */
fun makeStringErrorHandler(
    extendTerminal: (TerminalError) -> String,
    extendComposite: (CompositeError) -> String,
) = { err: ParseError ->
    stringErrorHandler(extendTerminal, extendComposite, err)
}

private fun stringErrorHandler(
    extendTerminal: (TerminalError) -> String,
    extendComposite: (CompositeError) -> String,
    err: ParseError
): List<String> =
    when (err) {
        is CoreTerminalError ->
            listOf(oneErrorToString(err))

        is TerminalError ->
            listOf(extendTerminal(err))

        is CompositeError -> {
            val prefix =
                if (err is CoreCompositeError)
                    compositeErrorToPrefix(err)
                else
                    extendComposite(err)

            stringErrorHandler(extendTerminal, extendComposite, err.error).map {
                "$prefix: $it"
            }
        }

        is ManyErrors ->
            err.unwrap().flatMap { stringErrorHandler(extendTerminal, extendComposite, it) }
    }

private fun compositeErrorToPrefix(
    err: CoreCompositeError
): String =
    when (err) {
        is KeyError ->
            "Error on map key '${err.key}'"

        is PropError<*, *> ->
            "Error on property '${err.prop.name}'"

        is IndexError ->
            "Error at index ${err.index}"
    }

private fun oneErrorToString(
    err: CoreTerminalError
): String =
    when (err) {
        is RequiredError ->
            "Required value"

        is EnumError ->
            err.expected.joinToString(", ").let {
                "Invalid value `${err.inp}`, please provide one of: $it"
            }

        is StringError ->
            "Invalid value, it must be a string"

        is BoolError ->
            "Invalid value, it must be a boolean"

        is IntError ->
            "Invalid value, it must be an integer"

        is UIntError ->
            "Invalid value, it must be an unsigned integer"

        is LongError ->
            "Invalid value, it must be an integer"

        is DoubleError ->
            "Invalid value, it must be a decimal number"

        is MinError<*> ->
            "Value must be greater than or equal to `${err.min}`, got `${err.inp}`"

        is MaxError<*> ->
            "Value must be smaller than or equal to `${err.max}`, got `${err.inp}`"

        is BetweenError<*> ->
            "Value must be between `${err.min}` and `${err.max}`, inclusive, got `${err.inp}`"
    }