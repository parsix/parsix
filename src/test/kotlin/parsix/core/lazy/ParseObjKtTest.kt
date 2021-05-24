package parsix.core.lazy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import parsix.core.PropError
import parsix.core.curry
import parsix.core.parseInto
import parsix.core.succeed
import parsix.result.Failure
import parsix.result.Ok
import parsix.test.TestError
import parsix.test.neverCalled

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
            Failure(PropError(TestSrc::b, TestError("second"))),
            parseInto(TestSrc::class, ::TestDst.curry())
                .lazyRequired(TestSrc::a, neverCalled())
                .lazyOptional(TestSrc::b, TestError.lift("second"))
                .invoke(TestSrc("ok", "10"))
        )
    }
}