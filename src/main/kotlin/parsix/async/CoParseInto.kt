package parsix.async

import parsix.fp.result.Ok
import kotlin.reflect.KClass

/**
 * Start building a complex parser using a generic [Map] as input.
 * This is quite useful when you have some unstructured data coming from a CSV, an
 * HTTP Request, etc...
 *
 * @see parsix.core.curry
 */
fun <A, B> coParseInto(f: (A) -> B): CoParseMap<(A) -> B> =
    { _ -> Ok(f) }

/**
 * Start building a complex parser using a generic object [I] as input.
 * This is quite useful when you have already deserialized your raw stream into an object,
 * but needs further refinement before it can be given to your business logic.
 *
 * @see parsix.core.curry
 */
fun <T : Any, A, B> coParseInto(
    @Suppress("UNUSED_PARAMETER") _typeinference: KClass<T>,
    f: (A) -> B
): CoParse<T, (A) -> B> =
    { _ -> Ok(f) }