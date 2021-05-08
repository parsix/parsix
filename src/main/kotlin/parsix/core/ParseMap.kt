package parsix.core

typealias ParseMap<O> = Parse<Map<String, Any?>, O>

fun <A, B> parseMap(f: (A) -> B): ParseMap<(A) -> B> =
    { _ -> Ok(f) }

fun <A, B> ParseMap<(A) -> B>.pluckKey(key: String, parse: Parse<Any?, A>): ParseMap<B> =
    this.pluck(parseKey(key, parse))

fun <O> parseKey(key: String, parse: Parse<Any?, O>): ParseMap<O> =
    { inp ->
        parse(inp[key]).mapError {
            FieldError(key, it)
        }
    }

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