# Parsix
High level parsing to ensure your input is in the right shape and satisfies all constraints that business logic requires.

It is highly inspired by the work of Alexis King "[Parse, don't validate](https://lexi-lambda.github.io/blog/2019/11/05/parse-don-t-validate/)", we recommend reading it even if you are unfamiliar with Haskell.

We have also published a new article specific to Kotlin: [Parse, don't validate in Kotlin](https://pelligra-s.medium.com/parse-dont-validate-in-kotlin-afbac505a04f)

## What's the problem?
Any non-trivial program always needs to validate external inputs. In Kotlin ecosystem it's often a mix of deserializing a stream into an object and then validate it satisfies the more complex business constraints.
Most programs will perform validation in a form like:
```kotlin:src/samples/readme/validations.kt [2]
```
All of them share a critical flaw: **there is no guarantee our program actually run this validation**.

Consider the following example:
```kotlin:src/samples/readme/problem/storeEmail.kt [3]
```
We know that at business level `storeEmail` must receive an e-mail, however our compiler is unaware of it and in fact we can remove `assertEmail` and it will happily compile it successfully.

## How we solve it
Instead of *just* validating, we should **parse** the input into a shape that makes the compiler aware of what we want to do.
In Parsix, the previous example would become:
```kotlin:src/samples/readme/solution/storeEmail.kt [2]
```
We changed `storeEmail` to require an `Email` as its argument, hence we cannot pass the raw input directly anymore, but even more importantly, this function doesn't have to do any further checks to ensure its input is valid, which is particularly important in large codebase.  
If we try to remove parse logic, compilation will fail.  
By using `value class` for our `Email`, we get the best of both worlds: more safety and same performance at runtime as using `String`.

Through [visibility modifiers](https://kotlinlang.org/docs/visibility-modifiers.html) we can ensure that an Email must always be constructed through parsing, so that compiler will force us *do the right thing*. Check [Recommended Style](#recommended-style) for more on this topic.

Please notice that if we want to get access to `Email` after parsing, we are forced to evaluate it and explicitly handle the failure case.

This may look as more code, but it's quite easy to abstract given that most programs will have a single exit point for their errors.
For example, if we are working on a Web Server, code may look like:
```kotlin:src/samples/readme/abstraction/handleParsed.kt [2]
```

Then you can use it in all your endpoints:
```kotlin:src/samples/readme/abstraction/storeEmail.kt [2]
```
This is just a simple abstraction and, depending on your codebase, you can make it even better by, for example, handling parsing input at route definition level.

Given that we are working with simple data types, possibilities for abstraction and composition are really high.

## Library core values
- **Do the right thing**: as library authors, we strive to make best-practices easy to follow and foster a culture that sees the compiler as an invaluable friend, rather than a foe.

- **Composable**: people can only handle a certain amount of information at a time, hence we will have better results and fewer mistakes if we keep it low. That's why is important to decompose problems into smaller, digestible solutions and then put all of them together to efficiently solve the main issue.

- **Extensible**: we want our users to be able to extend our library and bend it in a way that fit their problems, not the other way around.

- **Simple**: developers should be able to understand the overall implementation by jumping through functions and reading our code.

- **Explicit**: we prefer to make code explicit and to hide only what makes sense; we are not afraid of a little more verbose code if it helps avoid mistakes.

## Terminology
We would like to make sure we are on the same page when using certain terms:
- **Validation**: check if some piece of data satisfy a set of constraints.  
 Examples: Object is String, Integer is less than 100, String is a valid e-mail.
 
- **Assertion**: a strong kind of validation, it will stop execution if the condition isn't met.

- **Data parsing**: try to transform a particular piece of data into something more refined. This process can fail, but when it succeeds it will increase our knowledge about our data. We refer to this as just "parsing".  
 Examples: Stream<Byte> to JSON, String to Email, Name to FirstName
 
- **Deserialization**: a specific form of parsing which usually go from a low-level data representation suitable for storage or transmission, to a more high level one.  
 Examples: HTTP Request Body to UserForm, SQLRow to UserModel

- **Sanitization**: clean up data so that they are free of harms. This is another specific form of parsing, one that always succeed and is very effective in preventing common software exploitations, like the infamous [SQL Injections](https://owasp.org/www-community/attacks/SQL_Injection) and [Cross Site Scripting](https://owasp.org/www-community/attacks/xss/).  
Examples: String to HTML, String to SQLString
 
 As you may see, data parsing will also need to inspect data and ensure satisfy all constraints required by the more refined types, which is very similar to what validation does. The two processes are indeed related and we could consider parsing as a super-set of validation, in fact `validate` can always be implemented in terms of an equivalent `parse`:
 ```kotlin
val parseEmail: Parse<String, Email>

fun validateEmail(str: String): bool =
    when (parseEmail(str)) {
        is Ok -> true
        is ParseError -> false
    }
```

## Build your own Parse
Parsix focus on composition and extension, therefore coming up with a new parser is as straightforward as implementing the following function:
```kotlin
import parsix.core.Parsed

fun <I, O> parse(input: I): Parsed<I, O>
```
The `Parse` we used in previous example is just a typealias over it.

### What is Parsed?
Parsed is a sealed interface, it models our parse result and can only have two shapes:
 * `Ok(value)` models the success case
 * `ParseFailure` another sealed interface, models the failure case

If you are familiar with functional programming, this type is a specialised `Result` (also known as `Either`).

### A simple parse
Given that each business domain is different from one another, Parsix offers only low level parsers and combinators that makes it easy to implement more complex ones.

Let's say we have `Age` concept and we want to ensure that in a particular flow only adults (Age >= 18) can enter it:
```kotlin
import parsix.core.TerminalError
import parsix.core.Parsed

@JvmInline
value class Age(val value: UInt)

@JvmInline
value class AdultAge(val value: UInt)
data class NotAdultError(val inp: Age) : TerminalError

fun parseAdultAge(inp: Age): Parsed<AdultAge> =
    if (inp.value >= 18)
        Ok(AdultAge(inp.value))
    else
        NotAdultError(inp)
```

Implementing new parse function is quite simple and straightforward, that's all there is.
This also shows that you can parse any kind of data, doesn't have to be a primitive type.

It is worth mentioning that errors are first class citizens in Parsix: each parser should return a specific ParseError type and capture the overall context in a way that allow us to create very informative error messages for our customers. The good part is that it's the library user that will decide which will be the final format.

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
import parsix.core.TerminalError
import parsix.core.parseBetween

/** Make it type-safe to use this value after parsing */
@JvmInline
value class Name(val raw: String)

/** Model our new error for better error messages */
data class NameError(val inp: String, override val error: ParseError) : CompositeError

/** Make a new Parse that will parse length according to business logic */
val parseLength: Parse<Int, Int> = parseBetween(3, 255)

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
@JvmInline
value class Name(val value: String)
@JvmInline
value class Age(val value: UInt)
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

The greedy versions have no prefix and will greedily collect errors, will not stop after a failure and instead try to gather as many errors as possible. In case we have more than one failure, the result will be `ManyErrors`.

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
* `b` will always run, however because this is lazy, it will stop immediately in case of failure. This is nice because our next parse will be quite slow
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
The result of `evalThen` must be another `Parse` that receives the same `input` as the initial `Parse`, so in this case it will be a Map. The result is a normal parse and can be used as usual:
```kotlin
parseAttribute(attrInt) // is an IntAttribute
parseAttribute(attrStr) // is a StrAttribute
```

By transforming a `type` into `AttrType` first and by using `when`, the compiler will complain in case we add a new type but don't update this code.  
Always use `when` with enums and sealed classes to make your code easy to maintain.

Please note that `evalThen` can work with any parser, it doesn't have to be an enum and the lambda code can be as complex as you need.

### Combine Parses
We value composability, so everything can compose, including our `Parse`, we have a handy extension function for that.

Suppose we need to parse `SomeEnum`, however our initial input is `Any`. In this case we can't just use `parseEnum` because it requires a `String` as input. Thankfully we already have a way to parse `Any` to a `String`, `parseString`.  
Let's glue them together:
```kotlin
val parseEnumFromAny: Parse<Any, SomeEnum> =
    ::parseString.then(parseEnum<SomeEnum>())
```
This reads quite naturally and should be clear what it does: parse the input and if it succeeds, forward the result to the next `Parse`. 

This operator is an infix one, so we can remove some parentheses if you prefer:
```kotlin
val parseEnumFromAny =
    ::parseString then parseEnum<SomeEnum>()
```

Composing smaller, simpler parsers is our preferred style, it will increase code reusability and make overall codebase cleaner.

Please have a look at [common parsers](/src/main/kotlin/parsix/core/CommonParsers.kt) to see what's available.

## Handling Errors
All errors returned by this library and its extensions must be informative and capture all the context needed to create top-notch error messages.

We are well aware that business domains are quite different from one another, that's why we decided to provide an extensible hierarchy for errors, while still constraining it for a nice dev experience.

The bottom type for parse errors is `ParseError`, which is a sealed interface and can have 3 shapes:
- **TerminalError** is an open interface that models an error that has all information you need to generate an error message out of it, such as `RequiredError`

- **CompositeError** is an open interface that models an error that wraps other errors and give them more structure, like `PropError`.

- **ManyErrors** is a final class that models a collection of errors, useful for greedy parsers.

### String error handler
Out of the box we provide only an error handler capable of translating all errors packaged in `parsix.core` into a list of messages suitable to display to English-speaking customers.

However errors are extensible and we encourage library users to create new errors specific to their business domain.
That's why users must provide two extension functions to get the error handler. In its simplest form, we can just provide a default value for unknown errors:
```kotlin
import parsix.core.makeStringErrorHandler

val myHandler = makeStringErrorHandler(
    { "Unknown error, something went wrong" },
    { "Unknown" },
)
```
The first lamba will receive a `TerminalError` that we weren't able to parse and should return a suitable error message for it.  
The second lambda instead will receive a `CompositeError` that we weren't able to parse and should return a suitable message to be used as a prefix, we will then recursively parse the wrapped error and construct a final message like: `"$prefix: $message"`.
```kotlin
import parsix.core.RequiredError
import parsix.core.TerminalError
import parsix.core.CompositeError

data class MyTerminalError : TerminalError
data class MyCompositeError(override val error: ParseError) : CompositeError

myHandler(MyTerminalError()) == listOf("Unknown error, something went wrong")
myHandler(MyCompositeError(RequiredError)) == listOf("Unknown: Required value")
```

## Recommended Style
When using this library you are free and encouraged to experiment, to come up with your own ways. Nonetheless, we would like to recommend a style that works quite well for us.
We recommend this style especially if you are working in a large codebase with many contributors.

Our main goal is to make mistakes less likely to happen, use the compiler to both guide implementation and remind us to not be too lazy :)

The main idea is that all inputs must be checked and sanitized as close as the source as possible, while business logic should consume well structured data.

Imagine we want to implement a Signup form in a web server and that we receive raw JSON data:
 ```
{
  "email": String,
  "password": String,
  "verifyPassword": String
}
```

Small disclaimer about code: we will omit all imports for simplicity sakes.  
In this case we would deserialize the incoming request data into a `Map<String, Any?>`, then:
```kotlin
package app.http.signup

fun signupEndpoint(request: Map<String, Any?>): Response {
    when (val parsed = Signup.parse(request)) {
        is Ok ->
            signup(parsed.value)
            response(HttpStatus.Created)
        is ParseError ->
            parseFailureResponse(parsed)
    }
}

fun parseFailureResponse(err: ParseError): Reponse =
    TODO("implement error handling")
```
As for the first principle, we are immediately parsing our request and exit immediately with a response error if something went wrong. Due to the shape of Parsed, the compiler is helping us remember to handle the error branch, no other way around it!
We are then forwarding to `signup` business logic the fully parsed and valid object, making it more lean and robust.

Let's look more closely at `signup`:
```kotlin
package app.feature.signup

fun signup(data: Signup) =
    TODO("signup our customer")

class PasswordDoesntMatchError : TerminalError
data class Signup private constructor(val email: Email, val password: Password) {
    companion object {
        val parse: ParseMap<Signup>
            get() = parseInto(this::buildFromReq.curry())
                .required("email", Email.parse)
                .required("pass", Password.parse)
                .required("pass_verify", ::parseString)

        private fun buildFromReq(
            email: Email,
            password: Password, verify: String
        ): Parsed<Signup> =
            if (verify == pass.unwrap)
                Ok(Signup(email, pass))
            else
                PasswordDoesntMatchError()
    }
}
```
We have created a new data class for our feature and set the main constructor to private, therefore we can build an instance of Signup if and only if we use `Signup.parse`.
By protecting the constructor, the compiler will prevent us from mistakenly pass around values that haven't been properly parsed, so we have to *do the right thing*.

In previous examples we always used constructors reference with `parseInto`, but that's just convention. Another aspect worth mentioning is that `parseInto` is capable of handling a `Parsed` result, which makes it pretty easy to compare passwords.

Let's have a look at `Email`:
```kotlin
package app.domain

@JvmInline
value class Email private constructor(val unwrap: String) {
    companion object {
        val parse: Parse<String, Email>
            get() = TODO("implement parser")

        fun buildForTest(val email: String) =
            Email(email)
    }
}
```
Email is a general concept and that's why is in `app.domain` package. Because it is generic, we will need it for testing and that's why we have `Email.buildForTest`, which may look controversial, but also shows us another benefit of this pattern: *by separating validation logic from constructors, we can build the object according to our use case and make our intent explicit*.  
As it is clear by the name, that method is only meant for tests and it will be pretty easy to spot any illegal usage of it through code review. That's good enough for almost all projects, but we could make it bullet proof through a custom linting rule.

Password will look very similar, we will skip it.

In our example we used a `companion object`, but perhaps you would prefer a Factory instead. As usual, this is just an example and even if you decide to adopt this style, please tweak it based on your project needs! ;)