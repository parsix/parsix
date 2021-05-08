package parsix.core

import kotlin.test.Test
import kotlin.test.assertEquals


internal class CommonParsersKtTest {
    enum class TestEnum(override val key: String) : ParsableEnum {
        Test1("one"),
        Test2("two")
    }

    @Test
    fun `test parseEnum returns Test1 on one`() {
        assertEquals(
            Ok(TestEnum.Test1),
            parseEnum<TestEnum>()("one")
        )
    }

    @Test
    fun `test parseEnum returns Test2 on two`() {
        assertEquals(
            Ok(TestEnum.Test2),
            parseEnum<TestEnum>()("two")
        )
    }

    @Test
    fun `test parseEnum fails on unknown value`() {
        assertEquals(
            OneError(
                CommonErrors.enumInvalid,
                ("expected" to setOf("one", "two"))
            ),
            parseEnum<TestEnum>()("unknown")
        )
    }
}