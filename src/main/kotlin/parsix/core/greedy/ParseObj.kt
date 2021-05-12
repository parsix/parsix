package parsix.core.greedy

import parsix.core.Parse
import parsix.core.notNullable
import parsix.core.nullable
import parsix.core.parseProp
import kotlin.reflect.KProperty1

/**
 * [parse] the given property [prop] of input object [I].
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see greedyPluck
 * @see parsix.core.parseInto
 * @see parsix.core.parseProp
 */
fun <I, P, A, B> Parse<I, (A) -> B>.pluckProp(
    prop: KProperty1<I, P>,
    parse: Parse<P, A>
): Parse<I, B> =
    this.greedyPluck(parseProp(prop, parse))

/**
 * When [prop] is nullable, it will refuse it an fail with [RequiredError][parsix.core.RequiredError],
 * otherwise it will [parse] the returned value.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseProp
 * @see parsix.core.notNullable
 */
fun <I, P : Any, A : Any, B> Parse<I, (A) -> B>.required(
    prop: KProperty1<I, P?>,
    parse: Parse<P, A>
): Parse<I, B> =
    this.pluckProp(prop, notNullable(parse))

/**
 * When [prop] is nullable, it will just provide [null] as an argument, * otherwise it
 * will [parse] the returned value.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseProp
 * @see parsix.core.nullable
 */
fun <I, P : Any, A : Any, B> Parse<I, (A?) -> B>.optional(
    prop: KProperty1<I, P?>,
    parse: Parse<P, A>
): Parse<I, B> =
    this.pluckProp(prop, nullable(parse))

/**
 * When [prop] is nullable, it will just provide [default] as an argument, * otherwise it
 * will [parse] the returned value.
 *
 * This method is greedy and will gather all parse failures.
 * Commonly used together with [parseInto][parsix.core.parseInto]
 *
 * @see parsix.core.parseInto
 * @see parsix.core.parseProp
 * @see parsix.core.nullable
 */
fun <I, P : Any, A : Any, B> Parse<I, (A) -> B>.optional(
    prop: KProperty1<I, P?>,
    default: A,
    parse: Parse<P, A>
): Parse<I, B> =
    this.pluckProp(prop, nullable(default, parse))