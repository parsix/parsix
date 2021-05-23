package readme.abstraction

import readme.solution.parseEmail
import readme.solution.storeEmail

fun storeEmailEndpoint(inp: String): Response =
    handleParsed(parseEmail(inp)) {
        storeEmail(it)
        okResponse()
    }