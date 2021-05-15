package parsix.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class FocusedParseKtTest {
    data class EmailUserError(
        val inp: String,
        override val error: ParseError
    ) : CompositeError

    @Test
    fun `it parses the subsection`() {
        assertEquals(
            Ok("hello@world.com"),
            parseEmailUser("hello@world.com")
        )
    }

    @Test
    fun `it properly maps failure`() {
        assertEquals(
            EmailUserError("@world.com", RequiredError),
            parseEmailUser("@world.com")
        )
    }

    private fun parseEmailUser(inp: String) =
        focusedParse(
            { it.split('@')[0] },
            { if (it.length > 1) Ok(it) else RequiredError },
            { it },
            ::EmailUserError
        )(inp)
}