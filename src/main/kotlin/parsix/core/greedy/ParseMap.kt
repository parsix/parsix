package parsix.core.greedy

import parsix.core.Ok
import parsix.core.Parse
import parsix.core.notNullable
import parsix.core.nullable
import parsix.core.parseKey
import parsix.core.parseString
import parsix.core.then

typealias ParseMap<O> = Parse<Map<String, Any?>, O>

fun <A, B> parseMap(f: (A) -> B): ParseMap<(A) -> B> =
    { _ -> Ok(f) }

fun <A, B> ParseMap<(A) -> B>.pluckKey(key: String, parse: Parse<Any?, A>): ParseMap<B> =
    this.greedyPluck(parseKey(key, parse))

@JvmName("genericRequired")
fun <A : Any, B> ParseMap<(A) -> B>.required(
    key: String,
    parse: Parse<Any, A>
): ParseMap<B> =
    this.pluckKey(key, notNullable(parse))

@JvmName("stringRequired")
fun <A : Any, B> ParseMap<(A) -> B>.required(
    key: String,
    parse: Parse<String, A>
): ParseMap<B> =
    this.required(key, ::parseString then parse)

@JvmName("genericOptional")
fun <A : Any, B> ParseMap<(A?) -> B>.optional(
    key: String,
    parse: Parse<Any, A>
): ParseMap<B> =
    this.pluckKey(key, nullable(parse))

@JvmName("stringOptional")
fun <A : Any, B> ParseMap<(A?) -> B>.optional(
    key: String,
    parse: Parse<String, A>
): ParseMap<B> =
    this.optional(key, ::parseString then parse)

@JvmName("genericDefault")
fun <A : Any, B> ParseMap<(A) -> B>.optional(
    key: String,
    default: A,
    parse: Parse<Any, A>
): ParseMap<B> =
    this.pluckKey(key, nullable(default, parse))

@JvmName("stringDefault")
fun <A : Any, B> ParseMap<(A) -> B>.optional(
    key: String,
    default: A,
    parse: Parse<String, A>
): ParseMap<B> =
    this.optional(key, default, ::parseString then parse)