package parsix.core.greedy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import parsix.core.ManyErrors
import parsix.core.Ok
import parsix.core.PropError
import parsix.core.curry
import parsix.core.parseInto
import parsix.core.succeed
import parsix.test.TestError

internal class ParseObjKtTest {
    data class TestSrc(val a: String, val b: String)
    data class TestDst(val name: String, val age: Int?)

    @Test
    fun `it successfully parses the object`() {
        assertEquals(
            Ok(TestDst("ok", 10)),
            parseInto(TestSrc::class, ::TestDst.curry())
                .required(TestSrc::a, succeed("ok"))
                .optional(TestSrc::b, succeed(10))
                .invoke(TestSrc("ok", "10"))
        )
    }

    @Test
    fun `it greedily collects errors`() {
        assertEquals(
            ManyErrors(
                setOf(
                    PropError(TestSrc::a, TestError("first")),
                    PropError(TestSrc::b, TestError("second")),
                )
            ),
            parseInto(TestSrc::class, ::TestDst.curry())
                .required(TestSrc::a, TestError.of("first"))
                .optional(TestSrc::b, TestError.of("second"))
                .invoke(TestSrc("ok", "10"))
        )
    }
}