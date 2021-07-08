// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import java.time.DayOfWeek
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class ThemeTest {

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndDaysOfWeek")
    fun getCellHeader(theme: Theme, dayOfWeek: DayOfWeek, expectedResult: Cell) {
        val result = theme.getCellHeader(dayOfWeek)

        assertThat(result).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndDayStatuses")
    fun getCellDay(theme: Theme, isToday: Boolean, inMonth: Boolean, dayOfWeek: DayOfWeek, expectedResult: Cell) {
        val result = theme.getCellDay(
            isToday = isToday,
            inMonth = inMonth,
            dayOfWeek = dayOfWeek
        )

        assertThat(result).isEqualTo(expectedResult)
    }

    companion object {

        @JvmStatic
        @Suppress("unused", "LongMethod")
        fun getCombinationOfThemesAndDaysOfWeek(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Theme.BLACK, DayOfWeek.MONDAY,
                Cell(id = 16908308, layout = 2131427363, background = null)
            ),
            Arguments.of(
                Theme.BLACK, DayOfWeek.TUESDAY,
                Cell(id = 16908308, layout = 2131427363, background = null)
            ),
            Arguments.of(
                Theme.BLACK, DayOfWeek.WEDNESDAY,
                Cell(id = 16908308, layout = 2131427363, background = null)
            ),
            Arguments.of(
                Theme.BLACK, DayOfWeek.THURSDAY,
                Cell(id = 16908308, layout = 2131427363, background = null)
            ),
            Arguments.of(
                Theme.BLACK, DayOfWeek.FRIDAY,
                Cell(id = 16908308, layout = 2131427363, background = null)
            ),
            Arguments.of(
                Theme.BLACK, DayOfWeek.SATURDAY,
                Cell(id = 16908308, layout = 2131427364, background = 2131034149)
            ),
            Arguments.of(
                Theme.BLACK, DayOfWeek.SUNDAY,
                Cell(id = 16908308, layout = 2131427365, background = 2131034155)
            ),
            Arguments.of(
                Theme.GREY, DayOfWeek.MONDAY,
                Cell(id = 16908308, layout = 2131427375, background = null)
            ),
            Arguments.of(
                Theme.GREY, DayOfWeek.TUESDAY,
                Cell(id = 16908308, layout = 2131427375, background = null)
            ),
            Arguments.of(
                Theme.GREY, DayOfWeek.WEDNESDAY,
                Cell(id = 16908308, layout = 2131427375, background = null)
            ),
            Arguments.of(
                Theme.GREY, DayOfWeek.THURSDAY,
                Cell(id = 16908308, layout = 2131427375, background = null)
            ),
            Arguments.of(
                Theme.GREY, DayOfWeek.FRIDAY,
                Cell(id = 16908308, layout = 2131427375, background = null)
            ),
            Arguments.of(
                Theme.GREY, DayOfWeek.SATURDAY,
                Cell(id = 16908308, layout = 2131427376, background = 2131034150)
            ),
            Arguments.of(
                Theme.GREY, DayOfWeek.SUNDAY,
                Cell(id = 16908308, layout = 2131427377, background = 2131034156)
            ),
            Arguments.of(
                Theme.WHITE, DayOfWeek.MONDAY,
                Cell(id = 16908308, layout = 2131427398, background = null)
            ),
            Arguments.of(
                Theme.WHITE, DayOfWeek.TUESDAY,
                Cell(id = 16908308, layout = 2131427398, background = null)
            ),
            Arguments.of(
                Theme.WHITE, DayOfWeek.WEDNESDAY,
                Cell(id = 16908308, layout = 2131427398, background = null)
            ),
            Arguments.of(
                Theme.WHITE, DayOfWeek.THURSDAY,
                Cell(id = 16908308, layout = 2131427398, background = null)
            ),
            Arguments.of(
                Theme.WHITE, DayOfWeek.FRIDAY,
                Cell(id = 16908308, layout = 2131427398, background = null)
            ),
            Arguments.of(
                Theme.WHITE, DayOfWeek.SATURDAY,
                Cell(id = 16908308, layout = 2131427399, background = 2131034154)
            ),
            Arguments.of(
                Theme.WHITE, DayOfWeek.SUNDAY,
                Cell(id = 16908308, layout = 2131427400, background = 2131034160)
            )
        )

        @JvmStatic
        @Suppress("unused", "LongMethod")
        fun getCombinationOfThemesAndDayStatuses(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Theme.BLACK, true, true, DayOfWeek.MONDAY,
                Cell(id = 16908308, layout = 2131427362, background = 2131034164)
            ),
            Arguments.of(
                Theme.BLACK, true, true, DayOfWeek.TUESDAY,
                Cell(id = 16908308, layout = 2131427362, background = 2131034164)
            ),
            Arguments.of(
                Theme.BLACK, true, true, DayOfWeek.WEDNESDAY,
                Cell(id = 16908308, layout = 2131427362, background = 2131034164)
            ),
            Arguments.of(
                Theme.BLACK, true, true, DayOfWeek.THURSDAY,
                Cell(id = 16908308, layout = 2131427362, background = 2131034164)
            ),
            Arguments.of(
                Theme.BLACK, true, true, DayOfWeek.FRIDAY,
                Cell(id = 16908308, layout = 2131427362, background = 2131034164)
            ),
            Arguments.of(
                Theme.BLACK, true, true, DayOfWeek.SATURDAY,
                Cell(id = 16908308, layout = 2131427358, background = 2131034151)
            ),
            Arguments.of(
                Theme.BLACK, true, true, DayOfWeek.SUNDAY,
                Cell(id = 16908308, layout = 2131427360, background = 2131034157)
            ),
            Arguments.of(
                Theme.BLACK, false, true, DayOfWeek.MONDAY,
                Cell(id = 16908308, layout = 2131427361, background = 2131034161)
            ),
            Arguments.of(
                Theme.BLACK, false, true, DayOfWeek.TUESDAY,
                Cell(id = 16908308, layout = 2131427361, background = 2131034161)
            ),
            Arguments.of(
                Theme.BLACK, false, true, DayOfWeek.WEDNESDAY,
                Cell(id = 16908308, layout = 2131427361, background = 2131034161)
            ),
            Arguments.of(
                Theme.BLACK, false, true, DayOfWeek.THURSDAY,
                Cell(id = 16908308, layout = 2131427361, background = 2131034161)
            ),
            Arguments.of(
                Theme.BLACK, false, true, DayOfWeek.FRIDAY,
                Cell(id = 16908308, layout = 2131427361, background = 2131034161)
            ),
            Arguments.of(
                Theme.BLACK, false, true, DayOfWeek.SATURDAY,
                Cell(id = 16908308, layout = 2131427357, background = 2131034149)
            ),
            Arguments.of(
                Theme.BLACK, false, true, DayOfWeek.SUNDAY,
                Cell(id = 16908308, layout = 2131427359, background = 2131034155)
            ),
            Arguments.of(
                Theme.BLACK, false, false, DayOfWeek.MONDAY,
                Cell(id = 16908308, layout = 2131427356, background = null)
            ),
            Arguments.of(
                Theme.BLACK, false, false, DayOfWeek.TUESDAY,
                Cell(id = 16908308, layout = 2131427356, background = null)
            ),
            Arguments.of(
                Theme.BLACK, false, false, DayOfWeek.WEDNESDAY,
                Cell(id = 16908308, layout = 2131427356, background = null)
            ),
            Arguments.of(
                Theme.BLACK, false, false, DayOfWeek.THURSDAY,
                Cell(id = 16908308, layout = 2131427356, background = null)
            ),
            Arguments.of(
                Theme.BLACK, false, false, DayOfWeek.FRIDAY,
                Cell(id = 16908308, layout = 2131427356, background = null)
            ),
            Arguments.of(
                Theme.BLACK, false, false, DayOfWeek.SATURDAY,
                Cell(id = 16908308, layout = 2131427356, background = null)
            ),
            Arguments.of(
                Theme.BLACK, false, false, DayOfWeek.SUNDAY,
                Cell(id = 16908308, layout = 2131427356, background = null)
            ),
            Arguments.of(
                Theme.GREY, true, true, DayOfWeek.MONDAY,
                Cell(id = 16908308, layout = 2131427374, background = 2131034165)
            ),
            Arguments.of(
                Theme.GREY, true, true, DayOfWeek.TUESDAY,
                Cell(id = 16908308, layout = 2131427374, background = 2131034165)
            ),
            Arguments.of(
                Theme.GREY, true, true, DayOfWeek.WEDNESDAY,
                Cell(id = 16908308, layout = 2131427374, background = 2131034165)
            ),
            Arguments.of(
                Theme.GREY, true, true, DayOfWeek.THURSDAY,
                Cell(id = 16908308, layout = 2131427374, background = 2131034165)
            ),
            Arguments.of(
                Theme.GREY, true, true, DayOfWeek.FRIDAY,
                Cell(id = 16908308, layout = 2131427374, background = 2131034165)
            ),
            Arguments.of(
                Theme.GREY, true, true, DayOfWeek.SATURDAY,
                Cell(id = 16908308, layout = 2131427370, background = 2131034152)
            ),
            Arguments.of(
                Theme.GREY, true, true, DayOfWeek.SUNDAY,
                Cell(id = 16908308, layout = 2131427372, background = 2131034158)
            ),
            Arguments.of(
                Theme.GREY, false, true, DayOfWeek.MONDAY,
                Cell(id = 16908308, layout = 2131427373, background = 2131034162)
            ),
            Arguments.of(
                Theme.GREY, false, true, DayOfWeek.TUESDAY,
                Cell(id = 16908308, layout = 2131427373, background = 2131034162)
            ),
            Arguments.of(
                Theme.GREY, false, true, DayOfWeek.WEDNESDAY,
                Cell(id = 16908308, layout = 2131427373, background = 2131034162)
            ),
            Arguments.of(
                Theme.GREY, false, true, DayOfWeek.THURSDAY,
                Cell(id = 16908308, layout = 2131427373, background = 2131034162)
            ),
            Arguments.of(
                Theme.GREY, false, true, DayOfWeek.FRIDAY,
                Cell(id = 16908308, layout = 2131427373, background = 2131034162)
            ),
            Arguments.of(
                Theme.GREY, false, true, DayOfWeek.SATURDAY,
                Cell(id = 16908308, layout = 2131427369, background = 2131034150)
            ),
            Arguments.of(
                Theme.GREY, false, true, DayOfWeek.SUNDAY,
                Cell(id = 16908308, layout = 2131427371, background = 2131034156)
            ),
            Arguments.of(
                Theme.GREY, false, false, DayOfWeek.MONDAY,
                Cell(id = 16908308, layout = 2131427368, background = null)
            ),
            Arguments.of(
                Theme.GREY, false, false, DayOfWeek.TUESDAY,
                Cell(id = 16908308, layout = 2131427368, background = null)
            ),
            Arguments.of(
                Theme.GREY, false, false, DayOfWeek.WEDNESDAY,
                Cell(id = 16908308, layout = 2131427368, background = null)
            ),
            Arguments.of(
                Theme.GREY, false, false, DayOfWeek.THURSDAY,
                Cell(id = 16908308, layout = 2131427368, background = null)
            ),
            Arguments.of(
                Theme.GREY, false, false, DayOfWeek.FRIDAY,
                Cell(id = 16908308, layout = 2131427368, background = null)
            ),
            Arguments.of(
                Theme.GREY, false, false, DayOfWeek.SATURDAY,
                Cell(id = 16908308, layout = 2131427368, background = null)
            ),
            Arguments.of(
                Theme.GREY, false, false, DayOfWeek.SUNDAY,
                Cell(id = 16908308, layout = 2131427368, background = null)
            ),
            Arguments.of(
                Theme.WHITE, true, true, DayOfWeek.MONDAY,
                Cell(id = 16908308, layout = 2131427397, background = 2131034166)
            ),
            Arguments.of(
                Theme.WHITE, true, true, DayOfWeek.TUESDAY,
                Cell(id = 16908308, layout = 2131427397, background = 2131034166)
            ),
            Arguments.of(
                Theme.WHITE, true, true, DayOfWeek.WEDNESDAY,
                Cell(id = 16908308, layout = 2131427397, background = 2131034166)
            ),
            Arguments.of(
                Theme.WHITE, true, true, DayOfWeek.THURSDAY,
                Cell(id = 16908308, layout = 2131427397, background = 2131034166)
            ),
            Arguments.of(
                Theme.WHITE, true, true, DayOfWeek.FRIDAY,
                Cell(id = 16908308, layout = 2131427397, background = 2131034166)
            ),
            Arguments.of(
                Theme.WHITE, true, true, DayOfWeek.SATURDAY,
                Cell(id = 16908308, layout = 2131427393, background = 2131034153)
            ),
            Arguments.of(
                Theme.WHITE, true, true, DayOfWeek.SUNDAY,
                Cell(id = 16908308, layout = 2131427395, background = 2131034159)
            ),
            Arguments.of(
                Theme.WHITE, false, true, DayOfWeek.MONDAY,
                Cell(id = 16908308, layout = 2131427396, background = 2131034163)
            ),
            Arguments.of(
                Theme.WHITE, false, true, DayOfWeek.TUESDAY,
                Cell(id = 16908308, layout = 2131427396, background = 2131034163)
            ),
            Arguments.of(
                Theme.WHITE, false, true, DayOfWeek.WEDNESDAY,
                Cell(id = 16908308, layout = 2131427396, background = 2131034163)
            ),
            Arguments.of(
                Theme.WHITE, false, true, DayOfWeek.THURSDAY,
                Cell(id = 16908308, layout = 2131427396, background = 2131034163)
            ),
            Arguments.of(
                Theme.WHITE, false, true, DayOfWeek.FRIDAY,
                Cell(id = 16908308, layout = 2131427396, background = 2131034163)
            ),
            Arguments.of(
                Theme.WHITE, false, true, DayOfWeek.SATURDAY,
                Cell(id = 16908308, layout = 2131427392, background = 2131034154)
            ),
            Arguments.of(
                Theme.WHITE, false, true, DayOfWeek.SUNDAY,
                Cell(id = 16908308, layout = 2131427394, background = 2131034160)
            ),
            Arguments.of(
                Theme.WHITE, false, false, DayOfWeek.MONDAY,
                Cell(id = 16908308, layout = 2131427391, background = null)
            ),
            Arguments.of(
                Theme.WHITE, false, false, DayOfWeek.TUESDAY,
                Cell(id = 16908308, layout = 2131427391, background = null)
            ),
            Arguments.of(
                Theme.WHITE, false, false, DayOfWeek.WEDNESDAY,
                Cell(id = 16908308, layout = 2131427391, background = null)
            ),
            Arguments.of(
                Theme.WHITE, false, false, DayOfWeek.THURSDAY,
                Cell(id = 16908308, layout = 2131427391, background = null)
            ),
            Arguments.of(
                Theme.WHITE, false, false, DayOfWeek.FRIDAY,
                Cell(id = 16908308, layout = 2131427391, background = null)
            ),
            Arguments.of(
                Theme.WHITE, false, false, DayOfWeek.SATURDAY,
                Cell(id = 16908308, layout = 2131427391, background = null)
            ),
            Arguments.of(
                Theme.WHITE, false, false, DayOfWeek.SUNDAY,
                Cell(id = 16908308, layout = 2131427391, background = null)
            )
        )
    }
}
