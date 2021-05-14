# Parsix
High level parsing to ensure your input is in the right shape and satisfies all constraints that business logic requires.

It is highly inspired by the work of Alexis King "[Parse, don't validate](https://lexi-lambda.github.io/blog/2019/11/05/parse-don-t-validate/)", we recommend reading it even if you are unfamiliar with Haskell.

## What's the problem?
Any non-trivial program always needs to validate external inputs. In Kotlin ecosystem it's often a mix of deserializing a stream into an object and then validate it satisfies the more complex business constraints.
Most programs will perform validation in a form like:
```kotlin
fun validateEmail(str: String): Bool
/**
 * @throws IllegalStateException
 */
fun assertEmail(str: String)
fun validateEmail(str: String): Validated<String>
```
All of them share a critical flaw: **there is no guarantee our program actually run this validation**.

Consider the following example:
```kotlin
fun storeEmailEndpoint(email: String) {
    assertEmail(email)
    storeEmail(email)
}

fun storeEmail(email: String) {
    TODO("store it somewhere")
}
```
We know that at business level `storeEmail` must receive an e-mail, however our compiler is unaware of it and in fact we can remove `assertEmail` and it will happily compile it successfully.

## How we solve it
Instead of *just* validating, we should instead **parse** the input into a shape that makes the compiler aware of what we want to do.
In Parsix, the previous example would become:
```kotlin
import parsix.core.Parse

@JvmInline
value class Email(val email: String)

val parseEmail: Parse<String, Email>
fun storeEmailEndpoint(inp: String) {
    when (val parsed = parseEmail(inp)) {
        is Ok ->
            storeEmail(parsed.value)

        is ParseError ->
            TODO("handle failure")
    }
}

fun storeEmail(email: Email) {
    TODO("store it somewhere")
}
```
The most important change is that now `storeEmail` expects an `Email` as its argument, hence we cannot pass the raw input directly anymore, but even more importantly, this function doesn't have to do any further check to ensure its input is valid, which is particularly important in large codebase.  
If we try to remove parse logic, compilation will fail.

Through [visibility modifiers](https://kotlinlang.org/docs/visibility-modifiers.html) we can ensure that an Email must always be constructed through parsing, so that compiler will force us *do the right thing*. 

Please notice that if we want to get access to `Email` after parsing, we are forced to evaluate it and explicitly handle the failure case.

This may look as more code, but it's quite easy to abstract given that most programs will have a single exit point for their errors.
For example, if we are working on a Web Server, code may look like:
```kotlin
fun <T> handleParsed(parsed: Parsed<T>, happyCase: (T) -> Response): Response =
    when (parsed) {
        is Ok ->
            happyCase(parsed.value)
        is ParseFailure ->
            parseFailureToResponse(parsed)
    }
```

Then you can use this code in all your endpoints:
```kotlin
fun storeEmailEndpoint(inp: String): Response = 
    handleParsed(parseEmail(inp)) {
        storeEmail(it)
        okResponse()
    }
```
This is just a simple abstraction and, depending on your codebase, you can make it even better by, for example, handling parsing input at route definition level.

Given that we are working with simple data types, possibilities for abstraction and composition are really high.

## Library core values
- **Do the right thing**: as library authors, we strive to make best-practices easy to follow and foster a culture that sees the compiler as an invaluable friend, rather than a foe.

- **Composable**: people can only handle a certain amount of information at a time, the lower the better. That's why is important to decompose problems into smaller, digestible solutions and then put all of them together to efficiently solve the main issue.

- **Extensible**: we want our users to be able to extend our library and bend it in a way that fit their problems, not the other way around.

- **Simple**: developers should be able to understand the overall implementation by jumping through functions and reading our code.

- **Explicit**: we prefer making code explicit and to hide only what makes sense; we are not afraid of making code a little more verbose if it helps avoid mistakes.

## Build your own Parse
Parsix focus on composition and extension, therefore coming up with a new parser is as straightforward as implementing the following function:
```kotlin
import parsix.core.Parsed

fun <I, O> parse(input: I): Parsed<I, O>
```
The `Parse` we used in previous example is just a typealias over it.

### What is a Parsed?
Parsed is a sealed class, it models our parse result and can have only two shapes:
 * `Ok(value)` models the success case
 * `ParseFailure` another sealed class, models the failure case

If you are familiar with functional programming, this type is a specialised type of `Result` (also known as `Either`).

### A simple parse
Given that each business domain is different from one another, Parsix offers only low level parsers and combinators that makes it easy to implement more complex ones.

Let's say we have `Age` concept and we want to ensure that in a particular flow only adults (Age >= 18) can enter it:
```kotlin
import parsix.core.OneError
import parsix.core.Parsed

data class Age(val value: UInt)

data class AdultAge(val value: UInt)
data class NotAdultError(val inp: Age) : OneError()

fun parseAdultAge(inp: Age): Parsed<AdultAge> =
    if (inp.value >= 18)
        Ok(AdultAge(inp.value))
    else
        NotAdultError(inp)
```

Implementing new parse function is quite simple and straightforward, that's all it needs.
This also shows that you can parse any kind of data, doesn't have to be a primitive type.

### Parse Enum
We have a specific parser capable of parsing any Enum, as long as it implements `ParsableEnum` interface.
```kotlin
enum class AttrType(override val key: String) : ParsableEnum {
    IntType("int"),
    StrType("str"),
}

val parseAttributeType: Parse<String, AttrType> =
    parseEnum<AttrType>()
```
No extra annotation required.
 
Some may suggest to use `Enum.name` instead of having to extend from a particular interface, however is good practice to not tie your external API with a low level detail as `Enum.name`.   
 Explicitly providing `key` makes code rename-safe: you are free to change your enums without inadvertently breaking your API contract.

### Parse based on object projection
Let's say in our domain we need to work with Name, it must be a string and it has length between 3 and 255 chars:
```kotlin
import parsix.core.Parsed
import parsix.core.Ok
import parsix.core.ParseError
import parsix.core.OneError
import parsix.core.parseBetween

/** Make it type-safe to use this value after parsing */
@JvmInline
value class Name(val raw: String)

/** Model our new error for better error messages */
data class NameError(val inp: String, val err: ParseError) : OneError()
parse

/** Make a parse for our length, according to business logic */
val parseLength = parseBetween(3, 255)

fun parseName(inp: String): Parsed<Name> =
    when (parsed = parseLength(inp.length)) {
        is Ok ->
            Ok(Name(inp))

        is ParseError ->
            NameError(inp, parsed)
    }
```
That's all you need. We can also write the same using `focusedParse` combinator:
```kotlin
import parsix.core.focusedParse

val parseName: Parse<String, Name> =
    focusedParse(
        // we want to constraint the string length
        focus = { inp -> inp.length },
        // parse length
        parse = parseBetween(3, 255),
        // successful value
        mapOk = ::Name,
        // package error for better error messages
        mapErr = ::NameError
    )
```
The benefit of using this combinator is to guide the library user towards the right path and ensure we augment both input and error.

### Parse complex type from Map
Parsing a single value is ok, but more often we will need to build types that require more than one element. For example:
```kotlin
data class User(val name: Name, val age: Age)
data class Name(val value: String)
data class Age(val value: UInt)
```
Suppose we will ingest data from a CSV and each row will be a `Map<String, String>` like:
```kotlin
val rawUser = mapOf("name" to "Test", "age" to "42")
```
This can be parsed easily:
```kotlin
import parsix.core.carry
import parsix.core.ParseMap
import parsix.core.parseInto
import parsix.core.parseString
import parsix.core.parseUInt
import parsix.core.greedy.required

val parseUser: ParseMap<User> = parseInto(::User.carry())
        .required("name", ::parseString.map(::Name))
        .required("age", ::parseUInt.map(::Age))
```
One important thing to note is `::User.carry()`: it will destruct `User` constructor into multiple functions, each receiving a single argument.
Each `required` call *plucks* away one argument and is completely type-safe, the compiler will complain if types do not match; by having a specific type for each argument we also ensure that we cannot swap lines.

The following will not compile:
```kotlin
val parseUser: ParseMap<User> =
    parseInto(::User.carry())
        .required("age", ::parseUInt.map(::Age)) // <- expected type Name, got Age
        .required("name", ::parseString.map(::Name))
```

Please be aware, we have included an explicit type for `parseUser` just for example purpose, compiler will infer the type without problems.

### Parse complex type from another Object
Same example as previous section, but this time we have deserialized the raw input already into:
```kotlin
data class RawUser(val name: String, val age: String)
```
We can use `parseInto` again, but with a slight variation:
```kotlin
val parseUser: Parse<RawUser, User> = 
    parseInto(RawUser::class, ::User.carry())
        .required(RawUser::name, ::parseString.map(::Name))
        .required(RawUser::age, ::parseUInt.map(::Age))
```
As before, `parseUser` type is superfluous and all types must match, otherwise there will be a compiler error.

### Collecting errors
When using `parseInto`, we can have an error at each step. Some applications would like to gather all of them, while others would prefer to stop immediately.

For this reason we offer two different set of extension functions, you can find them in [parsix.core.greedy](/src/main/kotlin/parsix/core/lazy) and [parsix.core.lazy](/src/main/kotlin/parsix/core/lazy).

The greedy versions have no prefix and will greedily collect errors, will not stop and instead try to gather as many errors as possible. In case we have more than one failure, the result will be `ManyErrors`.

The lazy versions are prefixed with `lazy` and will short-circuit execution as soon as they fail. They are more efficient and should be preferred when we need fast feedback or to skip particularly expensive operations.

Both lazy and greedy versions can work together, but you have to be careful with how `parseInto` executes parsers: they run from last to first.
```kotlin
parseInto(::SomeData.curry())
    .required("a", slowParse)
    .lazyRequired("b", fastParse)
    .required("c", fastParse)
```
In this case, execution will be `c -> b -> a`:
* `c` will run first and execute the next parser even if parsing fails, but that's fine because `b` is also fast
* `b` will then run, however because this is lazy, it will stop immediately in case of failure. This is nice because our next parse will be quite slow
* `a` is the last parse to run, it doesn't matter if it's greedy or lazy

Therefore, in case all parsers fail, we will only collect errors from `c` and `b`.

### Parse a dynamic structure
Suppose we have the following structure:
```kotlin
sealed class Attribute
data class IntAttribute(val value: Int) : Attribute()
data class StrAttribute(val value: String) : Attribute()
```
Creating a parser for `IntAttribute` and `StrAttribute` should be easy by now, but how can we make a parser for `Attribute`?

Usually we will have some sort of type coming from our input to specify which variation current data is referring to:
```kotlin
enum class AttrType(override val key: String) : ParsableEnum {
    IntType("int"),
    StrType("str"),
}
```
Suppose our input will be a simple map:
```kotlin
val attrInt = mapOf("type" to "int", "val" to "10")
val attrStr = mapOf("type" to "str", "val" to "ten")
```
We should partially evaluate our input and based on `type` value, parse the input once more.

This is a common pattern and we have `evalThen` (ie: *evaluate, then continue with ...*) operator for that:
```kotlin
val parseAttribute: ParseMap<Attribute> = parseKey("type", parseType)
    .evalThen {
        when (it) {
            AttrType.IntType ->
                parseKey("val", parseIntAttr)
            AttrType.StrType ->
                parseKey("val", parseStrAttr)
        }
    }
```
`parseKey` is a low-level operator that creates a `Parse` that lookup the given `key` in input Map and parse it.
The result of `evalThen` must be another `Parse` that receives the same `input` as the initial `Parse`, so in this case it will be a Map.

By transforming a `type` into `AttrType` first and by using `when`, the compiler will complain in case we add a new type but don't update this code.  
Always use `when` with enums and sealed classes to make your code easy to maintain.

Please note that `evalThen` can work with any parser, it doesn't have to be an enum and the lambda code can be as complex as you need.

### Combine Parses
We value composability, so everything can compose, including our `Parse`, we have a handy extension function for that.

Suppose we need to parse `SomeEnum`, however our initial input can be `Any`. In this case we can't just use `parseEnum` because it requires a `String` as input. Thankfully we already have a way to parse `Any` to a `String`, `parseString`.  
Let's glue them together:
```kotlin
val parseEnumFromAny: Parse<Any, SomeEnum> =
    ::parseString.then(parseEnum<SomeEnum>())
```
This reads quite naturally and should be clear what it does: parse the input and if it succeeds, forward the result to the next `Parse`. 

This operator is an infix one, so we can remove some parentheses if you prefer:
```kotlin
val parseEnumFromAny: Parse<Any, SomeEnum> =
    ::parseString then parseEnum<SomeEnum>()
```

Composing smaller, simpler parsers is our preferred style, it will increase code reusability and make overall codebase cleaner.

Please have a look at [common parsers](/src/main/kotlin/parsix/core/CommonParsers.kt) to see what's available.