package parsix.core.greedy

import parsix.core.Parse
import parsix.core.notNullable
import parsix.core.nullable
import parsix.core.parseKey
import parsix.core.parseString
import parsix.core.then

typealias ParseMap<O> = Parse<Map<String, Any?>, O>

/**
 * Pluck one argument away from your complex parse builder.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see greedyPluck
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 */
fun <A, B> ParseMap<(A) -> B>.pluckKey(key: String, parse: Parse<Any?, A>): ParseMap<B> =
    this.greedyPluck(parseKey(key, parse))

/**
 * Ensure the input Map contains a non-null [key], fails with [parxi.core.RequiredError] otherwise.
 * [parse] will receive the value associated with [key].
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 * @see parsix.core.notNullable
 */
@JvmName("genericRequired")
fun <A : Any, B> ParseMap<(A) -> B>.required(
    key: String,
    parse: Parse<Any, A>
): ParseMap<B> =
    this.pluckKey(key, notNullable(parse))

/**
 * Ensure the input Map contains a non-null [key], fails with [parxi.core.RequiredError] otherwise.
 * [parse] will receive the value associated with [key].
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 * @see parsix.core.notNullable
 */
@JvmName("stringRequired")
fun <A : Any, B> ParseMap<(A) -> B>.required(
    key: String,
    parse: Parse<String, A>
): ParseMap<B> =
    this.required(key, ::parseString then parse)

/**
 * In case [key] is not contained in Map or null, it will immediately provide [null] as an
 * argument, otherwise it will [parse] it.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 * @see parsix.core.nullable
 */
@JvmName("genericOptional")
fun <A : Any, B> ParseMap<(A?) -> B>.optional(
    key: String,
    parse: Parse<Any, A>
): ParseMap<B> =
    this.pluckKey(key, nullable(parse))

/**
 * In case [key] is not contained in Map or null, it will immediately provide [null] as
 * an argument, otherwise it will [parse] it.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 * @see parsix.core.nullable
 */
@JvmName("stringOptional")
fun <A : Any, B> ParseMap<(A?) -> B>.optional(
    key: String,
    parse: Parse<String, A>
): ParseMap<B> =
    this.optional(key, ::parseString then parse)

/**
 * In case [key] is not contained in Map or null, it will immediately provide [default] as
 * an argument, otherwise it will [parse] it.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 * @see parsix.core.nullable
 */
@JvmName("genericDefault")
fun <A : Any, B> ParseMap<(A) -> B>.optional(
    key: String,
    default: A,
    parse: Parse<Any, A>
): ParseMap<B> =
    this.pluckKey(key, nullable(default, parse))

/**
 * In case [key] is not contained in Map or null, it will immediately provide [default] as
 * an argument, otherwise it will [parse] it.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 * @see parsix.core.nullable
 */
@JvmName("stringDefault")
fun <A : Any, B> ParseMap<(A) -> B>.optional(
    key: String,
    default: A,
    parse: Parse<String, A>
): ParseMap<B> =
    this.optional(key, default, ::parseString then parse)