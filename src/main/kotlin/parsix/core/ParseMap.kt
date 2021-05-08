package parsix.core

typealias ParseMap<O> = Parse<Map<String, Any?>, O>

fun <F> parseMap(f: F): ParseMap<F> =
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
fun <A : Any, B> ParseMap<(A) -> B>.required(key: String, parse: Parse<Any, A>): ParseMap<B> =
    this.pluckKey(key, ::parseNotNull.then(parse))

@JvmName("stringRequired")
fun <A : Any, B> ParseMap<(A) -> B>.required(key: String, parse: Parse<String, A>): ParseMap<B> =
    this.pluckKey(key, ::parseNotNull then ::parseString then parse)

@JvmName("genericOptional")
fun <A : Any, B> ParseMap<(A?) -> B>.optional(key: String, parse: Parse<Any, A>): ParseMap<B> =
    this.pluckKey(key, nullable(parse))

@JvmName("stringOptional")
fun <A : Any, B> ParseMap<(A?) -> B>.optional(key: String, parse: Parse<String, A>): ParseMap<B> =
    this.pluckKey(key, nullable(::parseString then parse))
