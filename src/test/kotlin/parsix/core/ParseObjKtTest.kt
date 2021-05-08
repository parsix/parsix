package parsix.core

import org.junit.Test
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
            { _ -> OneError("first") }
        val failSecond: Parse<String, Int> =
            { _ -> OneError("second") }

        assertEquals(
            ManyErrors(setOf(
                FieldError("a", OneError("first")),
                FieldError("b", OneError("second")),
            )),
            parseObj(TestSrc::class, ::TestDst.curry())
                .required(TestSrc::a, failFirst)
                .optional(TestSrc::b, failSecond)
                .invoke(TestSrc("ok", "10"))
        )
    }
}