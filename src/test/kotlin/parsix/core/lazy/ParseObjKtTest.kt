package parsix.core.lazy

import parsix.core.Ok
import parsix.core.PropError
import parsix.core.curry
import parsix.core.parseInto
import parsix.core.succeed
import parsix.test.TestError
import parsix.test.neverCalled
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ParseObjKtTest {
    data class TestSrc(val a: String, val b: String)
    data class TestDst(val name: String, val age: Int?)

    @Test
    fun `it successfully parses the object`() {
        assertEquals(
            Ok(TestDst("ok", 10)),
            parseInto(TestSrc::class, ::TestDst.curry())
                .lazyRequired(TestSrc::a, succeed("ok"))
                .lazyOptional(TestSrc::b, succeed(10))
                .invoke(TestSrc("ok", "10"))
        )
    }

    @Test
    fun `it short-circuits on first error`() {
        assertEquals(
            PropError(TestSrc::b, TestError("second")),
            parseInto(TestSrc::class, ::TestDst.curry())
                .lazyRequired(TestSrc::a, neverCalled())
                .lazyOptional(TestSrc::b, TestError.of("second"))
                .invoke(TestSrc("ok", "10"))
        )
    }
}