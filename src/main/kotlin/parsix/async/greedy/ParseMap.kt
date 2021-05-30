package parsix.async.greedy

import parsix.async.CoParse
import parsix.async.CoParseMap
import parsix.async.coNotNullable
import parsix.async.coNullable
import parsix.async.coParseKey
import parsix.async.then
import parsix.core.Parsed
import parsix.core.parseString

/**
 * Pluck one argument away from your complex parse builder.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see greedyAsyncPluck
 * @see parsix.async.coParseInto
 * @see coParseKey
 */
fun <A, B> CoParseMap<(A) -> B>.pluckKey(
    key: String,
    parse: CoParse<Any?, A>
): CoParseMap<B> =
    this.greedyAsyncPluck(coParseKey(key, parse))

@JvmName("flatPluckKey")
fun <A, B> CoParseMap<(A) -> Parsed<B>>.pluckKey(
    key: String,
    parse: CoParse<Any?, A>
): CoParseMap<B> =
    this.greedyAsyncPluck(coParseKey(key, parse))

/**
 * Ensure the input Map contains a non-null [key], fails with [parsix.core.RequiredError] otherwise.
 * [parse] will receive the value associated with [key].
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseKey
 * @see coNotNullable
 */
@JvmName("genericRequired")
fun <A : Any, B> CoParseMap<(A) -> B>.required(
    key: String,
    parse: CoParse<Any, A>
): CoParseMap<B> =
    this.pluckKey(key, coNotNullable(parse))

@JvmName("flatGenericRequired")
fun <A : Any, B> CoParseMap<(A) -> Parsed<B>>.required(
    key: String,
    parse: CoParse<Any, A>
): CoParseMap<B> =
    this.pluckKey(key, coNotNullable(parse))

/**
 * Ensure the input Map contains a non-null [key], fails with [parsix.core.RequiredError] otherwise.
 * [parse] will receive the value associated with [key].
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseKey
 * @see coNotNullable
 */
@JvmName("stringRequired")
fun <A : Any, B> CoParseMap<(A) -> B>.required(
    key: String,
    parse: CoParse<String, A>
): CoParseMap<B> =
    this.required(key, ::parseString then parse)

@JvmName("flatStringRequired")
fun <A : Any, B> CoParseMap<(A) -> Parsed<B>>.required(
    key: String,
    parse: CoParse<String, A>
): CoParseMap<B> =
    this.required(key, ::parseString then parse)

/**
 * In case [key] is not contained in Map or null, it will immediately provide [null] as an
 * argument, otherwise it will [parse] it.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseKey
 * @see coNullable
 */
@JvmName("genericOptional")
fun <A : Any, B> CoParseMap<(A?) -> B>.optional(
    key: String,
    parse: CoParse<Any, A>
): CoParseMap<B> =
    this.pluckKey(key, coNullable(parse))

@JvmName("flatGenericOptional")
fun <A : Any, B> CoParseMap<(A?) -> Parsed<B>>.optional(
    key: String,
    parse: CoParse<Any, A>
): CoParseMap<B> =
    this.pluckKey(key, coNullable(parse))

/**
 * In case [key] is not contained in Map or null, it will immediately provide [null] as
 * an argument, otherwise it will [parse] it.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseKey
 * @see coNullable
 */
@JvmName("stringOptional")
fun <A : Any, B> CoParseMap<(A?) -> B>.optional(
    key: String,
    parse: CoParse<String, A>
): CoParseMap<B> =
    this.optional(key, ::parseString then parse)

@JvmName("flatStringOptional")
fun <A : Any, B> CoParseMap<(A?) -> Parsed<B>>.optional(
    key: String,
    parse: CoParse<String, A>
): CoParseMap<B> =
    this.optional(key, ::parseString then parse)

/**
 * In case [key] is not contained in Map or null, it will immediately provide [default] as
 * an argument, otherwise it will [parse] it.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseKey
 * @see coNullable
 */
@JvmName("genericDefault")
fun <A : Any, B> CoParseMap<(A) -> B>.optional(
    key: String,
    default: A,
    parse: CoParse<Any, A>
): CoParseMap<B> =
    this.pluckKey(key, coNullable(default, parse))

@JvmName("flatGenericDefault")
fun <A : Any, B> CoParseMap<(A) -> Parsed<B>>.optional(
    key: String,
    default: A,
    parse: CoParse<Any, A>
): CoParseMap<B> =
    this.pluckKey(key, coNullable(default, parse))

/**
 * In case [key] is not contained in Map or null, it will immediately provide [default] as
 * an argument, otherwise it will [parse] it.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseKey
 * @see coNullable
 */
@JvmName("stringDefault")
fun <A : Any, B> CoParseMap<(A) -> B>.optional(
    key: String,
    default: A,
    parse: CoParse<String, A>
): CoParseMap<B> =
    this.optional(key, default, ::parseString then parse)

@JvmName("flatStringDefault")
fun <A : Any, B> CoParseMap<(A) -> Parsed<B>>.optional(
    key: String,
    default: A,
    parse: CoParse<String, A>
): CoParseMap<B> =
    this.optional(key, default, ::parseString then parse)