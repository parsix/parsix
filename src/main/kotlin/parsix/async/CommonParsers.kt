package parsix.async

import parsix.core.RequiredError
import parsix.fp.result.Failure
import parsix.fp.result.Ok

/**
 * The most basic parse, it will always succeed with [result]
 */
fun <I, O> coSucceed(result: O): CoParse<I, O> =
    { _ -> Ok(result) }

/**
 * Enhance [parse] so that it can handle a nullable input.
 * The final parser will return [RequiredError] if the input is null.
 */
inline fun <I : Any, O> coNotNullable(crossinline parse: CoParse<I, O>): CoParse<I?, O> =
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
inline fun <I : Any, O : Any> coNullable(
    default: O,
    crossinline parse: CoParse<I, O>
): CoParse<I?, O> =
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
inline fun <I : Any, O : Any> coNullable(crossinline parse: CoParse<I, O>): CoParse<I?, O?> =
    { inp ->
        if (inp == null)
            Ok(null)
        else
            parse(inp)
    }