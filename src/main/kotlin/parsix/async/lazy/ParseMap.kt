package parsix.async.lazy

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
 * This method fails fast in case parsing fails.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see lazyCoPluck
 * @see parsix.async.coParseInto
 * @see coParseKey
 */
fun <A, B> CoParseMap<(A) -> B>.lazyPluckKey(
    key: String,
    parse: CoParse<Any?, A>
): CoParseMap<B> =
    this.lazyCoPluck(coParseKey(key, parse))

@JvmName("lazyFlatPluckKey")
fun <A, B> CoParseMap<(A) -> Parsed<B>>.lazyPluckKey(
    key: String,
    parse: CoParse<Any?, A>
): CoParseMap<B> =
    this.lazyCoPluck(coParseKey(key, parse))

/**
 * Ensure the input Map contains a non-null [key], fails with [parsix.core.RequiredError] otherwise.
 * [parse] will receive the value associated with [key].
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseKey
 * @see coNotNullable
 */
@JvmName("lazyGenericRequired")
fun <A : Any, B> CoParseMap<(A) -> B>.lazyRequired(
    key: String,
    parse: CoParse<Any, A>
): CoParseMap<B> =
    this.lazyPluckKey(key, coNotNullable(parse))

@JvmName("lazyFlatGenericRequired")
fun <A : Any, B> CoParseMap<(A) -> Parsed<B>>.lazyRequired(
    key: String,
    parse: CoParse<Any, A>
): CoParseMap<B> =
    this.lazyPluckKey(key, coNotNullable(parse))

/**
 * Ensure the input Map contains a non-null [key], fails with [parsix.core.RequiredError] otherwise.
 * [parse] will receive the value associated with [key].
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseKey
 * @see coNotNullable
 */
@JvmName("lazyStringRequired")
fun <A : Any, B> CoParseMap<(A) -> B>.lazyRequired(
    key: String,
    parse: CoParse<String, A>
): CoParseMap<B> =
    this.lazyRequired(key, ::parseString then parse)

@JvmName("lazyFlatStringRequired")
fun <A : Any, B> CoParseMap<(A) -> Parsed<B>>.lazyRequired(
    key: String,
    parse: CoParse<String, A>
): CoParseMap<B> =
    this.lazyRequired(key, ::parseString then parse)

/**
 * In case [key] is not contained in Map or null, it will immediately provide [null] as an
 * argument, otherwise it will [parse] it.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseKey
 * @see coNullable
 */
@JvmName("lazyGenericOptional")
fun <A : Any, B> CoParseMap<(A?) -> B>.lazyOptional(
    key: String,
    parse: CoParse<Any, A>
): CoParseMap<B> =
    this.lazyPluckKey(key, coNullable(parse))

@JvmName("lazyFlatGenericOptional")
fun <A : Any, B> CoParseMap<(A?) -> Parsed<B>>.lazyOptional(
    key: String,
    parse: CoParse<Any, A>
): CoParseMap<B> =
    this.lazyPluckKey(key, coNullable(parse))

/**
 * In case [key] is not contained in Map or null, it will immediately provide [null] as
 * an argument, otherwise it will [parse] it.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseKey
 * @see coNullable
 */
@JvmName("lazyStringOptional")
fun <A : Any, B> CoParseMap<(A?) -> B>.lazyOptional(
    key: String,
    parse: CoParse<String, A>
): CoParseMap<B> =
    this.lazyOptional(key, ::parseString then parse)

@JvmName("lazyFlatStringOptional")
fun <A : Any, B> CoParseMap<(A?) -> Parsed<B>>.lazyOptional(
    key: String,
    parse: CoParse<String, A>
): CoParseMap<B> =
    this.lazyOptional(key, ::parseString then parse)

/**
 * In case [key] is not contained in Map or null, it will immediately provide [default] as
 * an argument, otherwise it will [parse] it.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseKey
 * @see coNullable
 */
@JvmName("lazyGenericDefault")
fun <A : Any, B> CoParseMap<(A) -> B>.lazyOptional(
    key: String,
    default: A,
    parse: CoParse<Any, A>
): CoParseMap<B> =
    this.lazyPluckKey(key, coNullable(default, parse))

@JvmName("lazyFlatGenericDefault")
fun <A : Any, B> CoParseMap<(A) -> Parsed<B>>.lazyOptional(
    key: String,
    default: A,
    parse: CoParse<Any, A>
): CoParseMap<B> =
    this.lazyPluckKey(key, coNullable(default, parse))

/**
 * In case [key] is not contained in Map or null, it will immediately provide [default] as
 * an argument, otherwise it will [parse] it.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseKey
 * @see coNullable
 */
@JvmName("lazyStringDefault")
fun <A : Any, B> CoParseMap<(A) -> B>.lazyOptional(
    key: String,
    default: A,
    parse: CoParse<String, A>
): CoParseMap<B> =
    this.lazyOptional(key, default, ::parseString then parse)

@JvmName("lazyFlatStringDefault")
fun <A : Any, B> CoParseMap<(A) -> Parsed<B>>.lazyOptional(
    key: String,
    default: A,
    parse: CoParse<String, A>
): CoParseMap<B> =
    this.lazyOptional(key, default, ::parseString then parse)