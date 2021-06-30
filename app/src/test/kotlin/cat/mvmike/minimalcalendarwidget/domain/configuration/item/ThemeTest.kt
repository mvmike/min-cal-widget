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
    fun getCellHeader(theme: Theme, dayOfWeek: DayOfWeek, expectedResult: Int) {
        assertThat(theme.getCellHeader(dayOfWeek)).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndDayStatuses")
    fun getCellDay(theme: Theme, isToday: Boolean, inMonth: Boolean, dayOfWeek: DayOfWeek, expectedResult: Int) {
        assertThat(
            theme.getCellDay(
                isToday = isToday,
                inMonth = inMonth,
                dayOfWeek = dayOfWeek
            )
        ).isEqualTo(expectedResult)
    }

    companion object {

        @JvmStatic
        @Suppress("unused", "LongMethod")
        fun getCombinationOfThemesAndDaysOfWeek(): Stream<Arguments> = Stream.of(
            Arguments.of(Theme.BLACK, DayOfWeek.MONDAY, 2131427363),
            Arguments.of(Theme.BLACK, DayOfWeek.TUESDAY, 2131427363),
            Arguments.of(Theme.BLACK, DayOfWeek.WEDNESDAY, 2131427363),
            Arguments.of(Theme.BLACK, DayOfWeek.THURSDAY, 2131427363),
            Arguments.of(Theme.BLACK, DayOfWeek.FRIDAY, 2131427363),
            Arguments.of(Theme.BLACK, DayOfWeek.SATURDAY, 2131427364),
            Arguments.of(Theme.BLACK, DayOfWeek.SUNDAY, 2131427365),
            Arguments.of(Theme.GREY, DayOfWeek.MONDAY, 2131427375),
            Arguments.of(Theme.GREY, DayOfWeek.TUESDAY, 2131427375),
            Arguments.of(Theme.GREY, DayOfWeek.WEDNESDAY, 2131427375),
            Arguments.of(Theme.GREY, DayOfWeek.THURSDAY, 2131427375),
            Arguments.of(Theme.GREY, DayOfWeek.FRIDAY, 2131427375),
            Arguments.of(Theme.GREY, DayOfWeek.SATURDAY, 2131427376),
            Arguments.of(Theme.GREY, DayOfWeek.SUNDAY, 2131427377),
            Arguments.of(Theme.WHITE, DayOfWeek.MONDAY, 2131427398),
            Arguments.of(Theme.WHITE, DayOfWeek.TUESDAY, 2131427398),
            Arguments.of(Theme.WHITE, DayOfWeek.WEDNESDAY, 2131427398),
            Arguments.of(Theme.WHITE, DayOfWeek.THURSDAY, 2131427398),
            Arguments.of(Theme.WHITE, DayOfWeek.FRIDAY, 2131427398),
            Arguments.of(Theme.WHITE, DayOfWeek.SATURDAY, 2131427399),
            Arguments.of(Theme.WHITE, DayOfWeek.SUNDAY, 2131427400)
        )

        @JvmStatic
        @Suppress("unused", "LongMethod")
        fun getCombinationOfThemesAndDayStatuses(): Stream<Arguments> = Stream.of(
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.MONDAY, 2131427362),
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.TUESDAY, 2131427362),
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.WEDNESDAY, 2131427362),
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.THURSDAY, 2131427362),
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.FRIDAY, 2131427362),
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.SATURDAY, 2131427358),
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.SUNDAY, 2131427360),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.MONDAY, 2131427361),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.TUESDAY, 2131427361),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.WEDNESDAY, 2131427361),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.THURSDAY, 2131427361),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.FRIDAY, 2131427361),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.SATURDAY, 2131427357),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.SUNDAY, 2131427359),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.MONDAY, 2131427356),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.TUESDAY, 2131427356),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.WEDNESDAY, 2131427356),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.THURSDAY, 2131427356),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.FRIDAY, 2131427356),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.SATURDAY, 2131427356),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.SUNDAY, 2131427356),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.MONDAY, 2131427374),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.TUESDAY, 2131427374),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.WEDNESDAY, 2131427374),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.THURSDAY, 2131427374),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.FRIDAY, 2131427374),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.SATURDAY, 2131427370),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.SUNDAY, 2131427372),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.MONDAY, 2131427373),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.TUESDAY, 2131427373),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.WEDNESDAY, 2131427373),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.THURSDAY, 2131427373),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.FRIDAY, 2131427373),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.SATURDAY, 2131427369),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.SUNDAY, 2131427371),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.MONDAY, 2131427368),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.TUESDAY, 2131427368),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.WEDNESDAY, 2131427368),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.THURSDAY, 2131427368),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.FRIDAY, 2131427368),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.SATURDAY, 2131427368),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.SUNDAY, 2131427368),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.MONDAY, 2131427397),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.TUESDAY, 2131427397),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.WEDNESDAY, 2131427397),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.THURSDAY, 2131427397),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.FRIDAY, 2131427397),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.SATURDAY, 2131427393),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.SUNDAY, 2131427395),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.MONDAY, 2131427396),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.TUESDAY, 2131427396),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.WEDNESDAY, 2131427396),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.THURSDAY, 2131427396),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.FRIDAY, 2131427396),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.SATURDAY, 2131427392),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.SUNDAY, 2131427394),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.MONDAY, 2131427391),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.TUESDAY, 2131427391),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.WEDNESDAY, 2131427391),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.THURSDAY, 2131427391),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.FRIDAY, 2131427391),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.SATURDAY, 2131427391),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.SUNDAY, 2131427391)
        )
    }
}
