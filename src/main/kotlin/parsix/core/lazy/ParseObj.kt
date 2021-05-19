package parsix.core.lazy

import parsix.core.Parse
import parsix.core.Parsed
import parsix.core.notNullable
import parsix.core.nullable
import parsix.core.parseProp
import kotlin.reflect.KProperty1


/**
 * [parse] the given property [prop] of input object [I].
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see lazyPluck
 * @see parsix.core.parseInto
 * @see parsix.core.parseProp
 */
fun <I, P, A, B> Parse<I, (A) -> B>.lazyPluckProp(
    prop: KProperty1<I, P>,
    parse: Parse<P, A>
): Parse<I, B> =
    this.lazyPluck(parseProp(prop, parse))

@JvmName("flatPluckProp")
fun <I, P, A, B> Parse<I, (A) -> Parsed<B>>.lazyPluckProp(
    prop: KProperty1<I, P>,
    parse: Parse<P, A>
): Parse<I, B> =
    this.lazyPluck(parseProp(prop, parse))

/**
 * When [prop] is nullable, it will refuse it an fail with [RequiredError][parsix.core.RequiredError],
 * otherwise it will [parse] the returned value.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseProp
 * @see parsix.core.notNullable
 */
fun <I, P : Any, A : Any, B> Parse<I, (A) -> B>.lazyRequired(
    prop: KProperty1<I, P?>,
    parse: Parse<P, A>
): Parse<I, B> =
    this.lazyPluckProp(prop, notNullable(parse))

@JvmName("lazyFlatRequired")
fun <I, P : Any, A : Any, B> Parse<I, (A) -> Parsed<B>>.lazyRequired(
    prop: KProperty1<I, P?>,
    parse: Parse<P, A>
): Parse<I, B> =
    this.lazyPluckProp(prop, notNullable(parse))

/**
 * When [prop] is nullable, it will just provide [null] as an argument, otherwise it
 * will [parse] the returned value.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseProp
 * @see parsix.core.nullable
 */
fun <I, P : Any, A : Any, B> Parse<I, (A?) -> B>.lazyOptional(
    prop: KProperty1<I, P?>,
    parse: Parse<P, A>
): Parse<I, B> =
    this.lazyPluckProp(prop, nullable(parse))

@JvmName("lazyFlatOptional")
fun <I, P : Any, A : Any, B> Parse<I, (A?) -> Parsed<B>>.lazyOptional(
    prop: KProperty1<I, P?>,
    parse: Parse<P, A>
): Parse<I, B> =
    this.lazyPluckProp(prop, nullable(parse))

/**
 * When [prop] is nullable, it will just provide [default] as an argument, otherwise it
 * will [parse] the returned value.
 *
 * This method fails fast in case parsing fails.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseProp
 * @see parsix.core.nullable
 */
fun <I, P : Any, A : Any, B> Parse<I, (A) -> B>.lazyOptional(
    prop: KProperty1<I, P?>,
    default: A,
    parse: Parse<P, A>
): Parse<I, B> =
    this.lazyPluckProp(prop, nullable(default, parse))

@JvmName("lazyFlatOptional")
fun <I, P : Any, A : Any, B> Parse<I, (A) -> Parsed<B>>.lazyOptional(
    prop: KProperty1<I, P?>,
    default: A,
    parse: Parse<P, A>
): Parse<I, B> =
    this.lazyPluckProp(prop, nullable(default, parse))