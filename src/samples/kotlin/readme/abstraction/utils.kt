package readme.abstraction

interface Response
interface Email

fun storeEmail(email: Email) {
    TODO("Store it somewhere")
}

fun okResponse(): Response =
    TODO("response")