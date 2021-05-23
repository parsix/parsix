package readme.problem
import readme.assertEmail

fun storeEmailEndpoint(email: String) {
    assertEmail(email)
    storeEmail(email)
}

fun storeEmail(email: String) {
    TODO("store it somewhere")
}