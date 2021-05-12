package parsix.core.lazy

import parsix.core.Parse
import parsix.core.greedy.ParseMap
import parsix.core.notNullable
import parsix.core.nullable
import parsix.core.parseKey
import parsix.core.parseString
import parsix.core.then

/**
 * Pluck one argument away from your complex parse builder.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see lazyPluck
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 */
fun <A, B> ParseMap<(A) -> B>.lazyPluckKey(key: String, parse: Parse<Any?, A>): ParseMap<B> =
    this.lazyPluck(parseKey(key, parse))

/**
 * Ensure the input Map contains a non-null [key], fails with [parsix.core.RequiredError] otherwise.
 * [parse] will receive the value associated with [key].
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 * @see parsix.core.notNullable
 */
@JvmName("genericRequired")
fun <A : Any, B> ParseMap<(A) -> B>.lazyRequired(
    key: String,
    parse: Parse<Any, A>
): ParseMap<B> =
    this.lazyPluckKey(key, notNullable(parse))

/**
 * Ensure the input Map contains a non-null [key], fails with [parsix.core.RequiredError] otherwise.
 * [parse] will receive the value associated with [key].
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 * @see parsix.core.notNullable
 */
@JvmName("stringRequired")
fun <A : Any, B> ParseMap<(A) -> B>.lazyRequired(
    key: String,
    parse: Parse<String, A>
): ParseMap<B> =
    this.lazyRequired(key, ::parseString then parse)

/**
 * In case [key] is not contained in Map or null, it will immediately provide [null] as an
 * argument, otherwise it will [parse] it.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 * @see parsix.core.nullable
 */
@JvmName("genericOptional")
fun <A : Any, B> ParseMap<(A?) -> B>.lazyOptional(
    key: String,
    parse: Parse<Any, A>
): ParseMap<B> =
    this.lazyPluckKey(key, nullable(parse))

/**
 * In case [key] is not contained in Map or null, it will immediately provide [null] as
 * an argument, otherwise it will [parse] it.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 * @see parsix.core.nullable
 */
@JvmName("stringOptional")
fun <A : Any, B> ParseMap<(A?) -> B>.lazyOptional(
    key: String,
    parse: Parse<String, A>
): ParseMap<B> =
    this.lazyOptional(key, ::parseString then parse)

/**
 * In case [key] is not contained in Map or null, it will immediately provide [default] as
 * an argument, otherwise it will [parse] it.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 * @see parsix.core.nullable
 */
@JvmName("genericDefault")
fun <A : Any, B> ParseMap<(A) -> B>.lazyOptional(
    key: String,
    default: A,
    parse: Parse<Any, A>
): ParseMap<B> =
    this.lazyPluckKey(key, nullable(default, parse))

/**
 * In case [key] is not contained in Map or null, it will immediately provide [default] as
 * an argument, otherwise it will [parse] it.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseKey
 * @see parsix.core.nullable
 */
@JvmName("stringDefault")
fun <A : Any, B> ParseMap<(A) -> B>.lazyOptional(
    key: String,
    default: A,
    parse: Parse<String, A>
): ParseMap<B> =
    this.lazyOptional(key, default, ::parseString then parse)