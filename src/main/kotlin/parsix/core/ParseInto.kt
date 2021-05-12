package parsix.core

import kotlin.reflect.KClass

typealias ParseMap<O> = Parse<Map<String, Any?>, O>

/**
 * Start building a complex parser using a generic [Map] as input.
 * This is quite useful when you have some unstructured data coming from a CSV, an
 * HTTP Request, etc...
 *
 * See [tests][ParseMapKtTest] to understand how to use it.
 * @see curry
 */
fun <A, B> parseInto(f: (A) -> B): ParseMap<(A) -> B> =
    { _ -> Ok(f) }

/**
 * Start building a complex parser using a generic object [I] as input.
 * This is quite useful when you have already deserialized your raw stream into an object,
 * but needs further refinement before it can be given to your business logic.
 *
 * See [tests][parsix.core.greedy.ParseObjKtTest] to understand how to use it.
 * @see curry
 */
fun <T : Any, A, B> parseInto(
    _typeinference: KClass<T>,
    f: (A) -> B
): Parse<T, (A) -> B> =
    { _ -> Ok(f) }