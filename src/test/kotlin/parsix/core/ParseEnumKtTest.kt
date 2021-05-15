package parsix.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ParseEnumKtTest {
    enum class TestEnum(override val key: String) : ParsableEnum {
        Test1("one"),
        Test2("two")
    }

    @Test
    fun `it returns Test1 on one`() {
        assertEquals(
            Ok(TestEnum.Test1),
            parseEnum<TestEnum>()("one")
        )
    }

    @Test
    fun `it returns Test2 on two`() {
        assertEquals(
            Ok(TestEnum.Test2),
            parseEnum<TestEnum>()("two")
        )
    }

    @Test
    fun `it fails on unknown value`() {
        assertEquals(
            EnumError("unknown", setOf("one", "two")),
            parseEnum<TestEnum>()("unknown")
        )
    }
}