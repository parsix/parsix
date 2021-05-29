package parsix.async.lazy

import parsix.async.CoParse
import parsix.async.coNotNullable
import parsix.async.coNullable
import parsix.async.coParseProp
import parsix.core.Parsed
import kotlin.reflect.KProperty1

/**
 * [parse] the given property [prop] of input object [I].
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see lazyAsyncPluck
 * @see coParseProp
 */
fun <I, P, A, B> CoParse<I, (A) -> B>.lazyPluckProp(
    prop: KProperty1<I, P>,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.lazyAsyncPluck(coParseProp(prop, parse))

@JvmName("flatPluckProp")
fun <I, P, A, B> CoParse<I, (A) -> Parsed<B>>.lazyPluckProp(
    prop: KProperty1<I, P>,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.lazyAsyncPluck(coParseProp(prop, parse))

/**
 * When [prop] is nullable, it will refuse it an fail with [RequiredError][parsix.core.RequiredError],
 * otherwise it will [parse] the returned value.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseProp
 * @see coNotNullable
 */
fun <I, P : Any, A : Any, B> CoParse<I, (A) -> B>.lazyRequired(
    prop: KProperty1<I, P?>,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.lazyPluckProp(prop, coNotNullable(parse))

@JvmName("lazyFlatRequired")
fun <I, P : Any, A : Any, B> CoParse<I, (A) -> Parsed<B>>.lazyRequired(
    prop: KProperty1<I, P?>,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.lazyPluckProp(prop, coNotNullable(parse))

/**
 * When [prop] is nullable, it will just provide [null] as an argument, otherwise it
 * will [parse] the returned value.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseProp
 * @see coNullable
 */
fun <I, P : Any, A : Any, B> CoParse<I, (A?) -> B>.lazyOptional(
    prop: KProperty1<I, P?>,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.lazyPluckProp(prop, coNullable(parse))

@JvmName("lazyFlatOptional")
fun <I, P : Any, A : Any, B> CoParse<I, (A?) -> Parsed<B>>.lazyOptional(
    prop: KProperty1<I, P?>,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.lazyPluckProp(prop, coNullable(parse))

/**
 * When [prop] is nullable, it will just provide [default] as an argument, otherwise it
 * will [parse] the returned value.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see coParseProp
 * @see coNullable
 */
fun <I, P : Any, A : Any, B> CoParse<I, (A) -> B>.lazyOptional(
    prop: KProperty1<I, P?>,
    default: A,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.lazyPluckProp(prop, coNullable(default, parse))

@JvmName("lazyFlatOptional")
fun <I, P : Any, A : Any, B> CoParse<I, (A) -> Parsed<B>>.lazyOptional(
    prop: KProperty1<I, P?>,
    default: A,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.lazyPluckProp(prop, coNullable(default, parse))