package parsix.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class StringErrorHandlerKtTest {
    @ParameterizedTest
    @MethodSource("oneErrorToStringProvider")
    fun `it can translate common OneError`(err: ParseError, expected: String) {
        val errToString =
            makeStringErrorHandler(
                { fail("unexpected one") },
                { fail("unexpected composite") }
            )
        assertEquals(
            listOf(expected),
            errToString(err)
        )
    }

    @Test
    fun `it allows user to parse their own OneError`() {
        class MyError : TerminalError

        val errToString = makeStringErrorHandler(
            { "Supports MyError" },
            { fail("unexpected composite") }
        )

        assertEquals(
            listOf("Supports MyError"),
            errToString(MyError())
        )
    }

    @ParameterizedTest
    @MethodSource("compositeErrorToStringProvider")
    fun `it can translate common CompositeError`(
        err: ParseError,
        expected: List<String>
    ) {
        val errToString = makeStringErrorHandler(
            { fail("unexpected one") },
            { fail("unexpected composite") }
        )

        assertEquals(expected, errToString(err))
    }

    @Test
    fun `it can translate custom CompositeError`() {
        class MyCompositeError(override val error: ParseError) : CompositeError

        val errToString = makeStringErrorHandler(
            { fail("unexpected one") },
            { "Composite error" }
        )

        assertEquals(
            listOf("Composite error: Required value"),
            errToString(MyCompositeError(RequiredError))
        )
    }


    companion object {
        data class TestData(val a: Int, val b: Map<String, String>)
        enum class TestEnum {
            One, Two, Three
        }

        @JvmStatic
        fun oneErrorToStringProvider() = Stream.of(
            Arguments.of(
                RequiredError,
                "Required value",
            ),
            Arguments.of(
                EnumError("unknown", setOf("hello", "world")),
                "Invalid value `unknown`, please provide one of: hello, world"
            ),
            Arguments.of(
                StringError(Unit),
                "Invalid value, it must be a string"
            ),
            Arguments.of(
                MinError(1, 10),
                "Value must be greater than or equal to `10`, got `1`"
            ),
            Arguments.of(
                MinError(TestEnum.One, TestEnum.Three),
                "Value must be greater than or equal to `Three`, got `One`"
            ),
            Arguments.of(
                MaxError(10, 1),
                "Value must be smaller than or equal to `1`, got `10`"
            ),
            Arguments.of(
                MaxError(TestEnum.Three, TestEnum.Two),
                "Value must be smaller than or equal to `Two`, got `Three`"
            ),
            Arguments.of(
                BetweenError("zero", "one", "ten"),
                "Value must be between `one` and `ten`, inclusive, got `zero`"
            ),
            Arguments.of(
                IntError(Unit),
                "Invalid value, it must be an integer"
            ),
            Arguments.of(
                UIntError(Unit),
                "Invalid value, it must be an unsigned integer"
            ),
            Arguments.of(
                LongError(Unit),
                "Invalid value, it must be an integer"
            ),
            Arguments.of(
                DoubleError(Unit),
                "Invalid value, it must be a decimal number"
            ),
        )

        @JvmStatic
        fun compositeErrorToStringProvider() = Stream.of(
            Arguments.of(
                KeyError("test", RequiredError),
                listOf("Error on map key 'test': Required value"),
            ),
            Arguments.of(
                KeyError(
                    "test", ManyErrors(
                        setOf(
                            KeyError("sub1", RequiredError),
                            KeyError("sub2", IntError(UInt)),
                        )
                    )
                ),
                listOf(
                    "Error on map key 'test': Error on map key 'sub1': Required value",
                    "Error on map key 'test': Error on map key 'sub2': Invalid value, it must be an integer",
                ),
            ),
            Arguments.of(
                PropError(TestData::a, RequiredError),
                listOf("Error on property 'a': Required value"),
            ),
            Arguments.of(
                PropError(
                    TestData::b, ManyErrors(
                        setOf(
                            KeyError("one", RequiredError),
                            KeyError("two", PropError(TestData::a, LongError('x'))),
                        )
                    )
                ),
                listOf(
                    "Error on property 'b': Error on map key 'one': Required value",
                    "Error on property 'b': Error on map key 'two': Error on property 'a': Invalid value, it must be an integer",
                ),
            ),
        )
    }
}