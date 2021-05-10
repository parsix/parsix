package parsix.core.greedy

import parsix.core.FieldError
import parsix.core.ManyErrors
import parsix.core.Ok
import parsix.core.TestError
import parsix.core.Parse
import parsix.core.curry
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ParseObjKtTest {
    data class TestSrc(val a: String, val b: String)
    data class TestDst(val name: String, val age: Int?)

    @Test
    fun `it successfully parse the object`() {
        data class TestDest(val name: String, val age: Int?)

        val first: Parse<String, String> =
            { _ -> Ok("ok") }
        val second: Parse<String, Int> =
            { _ -> Ok(10) }

        assertEquals(
            Ok(TestDest("ok", 10)),
            parseObj(TestSrc::class, ::TestDest.curry())
                .required(TestSrc::a, first)
                .optional(TestSrc::b, second)
                .invoke(TestSrc("ok", "10"))
        )
    }

    @Test
    fun `it greedily collect errors`() {
        val failFirst: Parse<String, String> =
            { _ -> TestError("first") }
        val failSecond: Parse<String, Int> =
            { _ -> TestError("second") }

        assertEquals(
            ManyErrors(
                setOf(
                    FieldError("a", TestError("first")),
                    FieldError("b", TestError("second")),
                )
            ),
            parseObj(TestSrc::class, ::TestDst.curry())
                .required(TestSrc::a, failFirst)
                .optional(TestSrc::b, failSecond)
                .invoke(TestSrc("ok", "10"))
        )
    }
}