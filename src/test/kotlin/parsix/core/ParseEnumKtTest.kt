package parsix.core

import kotlin.test.Test
import kotlin.test.assertEquals


internal class ParseEnumKtTest {
    enum class TestEnum(override val key: String) : ParsableEnum {
        Test1("one"),
        Test2("two")
    }

    @Test
    fun `parseEnum returns Test1 on one`() {
        assertEquals(
            Ok(TestEnum.Test1),
            parseEnum<TestEnum>()("one")
        )
    }

    @Test
    fun `parseEnum returns Test2 on two`() {
        assertEquals(
            Double.MAX_VALUE,
            Double.MAX_VALUE.toLong().toDouble()
        )
        assertEquals(
            Ok(TestEnum.Test2),
            parseEnum<TestEnum>()("two")
        )
    }

    @Test
    fun `parseEnum fails on unknown value`() {
        assertEquals(
            EnumError(setOf("one", "two")),
            parseEnum<TestEnum>()("unknown")
        )
    }
}