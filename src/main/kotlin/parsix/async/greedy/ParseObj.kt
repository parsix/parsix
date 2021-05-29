package parsix.async.greedy

import parsix.async.CoParse
import parsix.async.coNotNullable
import parsix.async.coNullable
import parsix.async.coParseProp
import parsix.core.Parsed
import kotlin.reflect.KProperty1

/**
 * [parse] the given property [prop] of input object [I].
 *
 * This method is greedy and will gather all failures.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see greedyAsyncPluck
 * @see parsix.async.coParseInto
 * @see parsix.async.coParseProp
 */
fun <I, P, A, B> CoParse<I, (A) -> B>.pluckProp(
    prop: KProperty1<I, P>,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.greedyAsyncPluck(coParseProp(prop, parse))

@JvmName("flatPluckProp")
fun <I, P, A, B> CoParse<I, (A) -> Parsed<B>>.pluckProp(
    prop: KProperty1<I, P>,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.greedyAsyncPluck(coParseProp(prop, parse))

/**
 * When [prop] is nullable, it will refuse it an fail with [RequiredError][parsix.core.RequiredError],
 * otherwise it will [parse] the returned value.
 *
 * This method is greedy and will gather all failures.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see parsix.async.coParseProp
 * @see coNotNullable
 */
fun <I, P : Any, A : Any, B> CoParse<I, (A) -> B>.required(
    prop: KProperty1<I, P?>,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.pluckProp(prop, coNotNullable(parse))

@JvmName("flatRequired")
fun <I, P : Any, A : Any, B> CoParse<I, (A) -> Parsed<B>>.required(
    prop: KProperty1<I, P?>,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.pluckProp(prop, coNotNullable(parse))

/**
 * When [prop] is nullable, it will just provide [null] as an argument, otherwise it
 * will [parse] the returned value.
 *
 * This method is greedy and will gather all failures.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see parsix.async.coParseProp
 * @see coNullable
 */
fun <I, P : Any, A : Any, B> CoParse<I, (A?) -> B>.optional(
    prop: KProperty1<I, P?>,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.pluckProp(prop, coNullable(parse))

@JvmName("flatOptional")
fun <I, P : Any, A : Any, B> CoParse<I, (A?) -> Parsed<B>>.optional(
    prop: KProperty1<I, P?>,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.pluckProp(prop, coNullable(parse))

/**
 * When [prop] is nullable, it will just provide [default] as an argument, otherwise it
 * will [parse] the returned value.
 *
 * This method is greedy and will gather all failures.
 * Commonly used together with [coParseInto][parsix.async.coParseInto]
 *
 * @see parsix.async.coParseInto
 * @see parsix.async.coParseProp
 * @see coNullable
 */
fun <I, P : Any, A : Any, B> CoParse<I, (A) -> B>.optional(
    prop: KProperty1<I, P?>,
    default: A,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.pluckProp(prop, coNullable(default, parse))

@JvmName("flatDefault")
fun <I, P : Any, A : Any, B> CoParse<I, (A) -> Parsed<B>>.optional(
    prop: KProperty1<I, P?>,
    default: A,
    parse: CoParse<P, A>
): CoParse<I, B> =
    this.pluckProp(prop, coNullable(default, parse))