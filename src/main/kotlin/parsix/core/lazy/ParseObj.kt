package parsix.core.lazy

import parsix.core.Ok
import parsix.core.Parse
import parsix.core.notNullable
import parsix.core.nullable
import parsix.core.parseProp
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

fun <T : Any, A, B> lazyParseObj(
    _typeinference: KClass<T>,
    f: (A) -> B
): Parse<T, (A) -> B> =
    { _ -> Ok(f) }

fun <I, P, A, B> Parse<I, (A) -> B>.pluckProp(
    prop: KProperty1<I, P>,
    parse: Parse<P, A>
): Parse<I, B> =
    this.lazyPluck(parseProp(prop, parse))

fun <I, P : Any, A : Any, B> Parse<I, (A) -> B>.required(
    prop: KProperty1<I, P?>,
    parse: Parse<P, A>
): Parse<I, B> =
    this.pluckProp(prop, notNullable(parse))

fun <I, P : Any, A : Any, B> Parse<I, (A?) -> B>.optional(
    prop: KProperty1<I, P?>,
    parse: Parse<P, A>
): Parse<I, B> =
    this.pluckProp(prop, nullable(parse))

fun <I, P : Any, A : Any, B> Parse<I, (A) -> B>.optional(
    prop: KProperty1<I, P?>,
    default: A,
    parse: Parse<P, A>
): Parse<I, B> =
    this.pluckProp(prop, nullable(default, parse))