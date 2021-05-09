// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.util.stream.Stream

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
            Arguments.of(Theme.BLACK, DayOfWeek.MONDAY, 2131361827),
            Arguments.of(Theme.BLACK, DayOfWeek.TUESDAY, 2131361827),
            Arguments.of(Theme.BLACK, DayOfWeek.WEDNESDAY, 2131361827),
            Arguments.of(Theme.BLACK, DayOfWeek.THURSDAY, 2131361827),
            Arguments.of(Theme.BLACK, DayOfWeek.FRIDAY, 2131361827),
            Arguments.of(Theme.BLACK, DayOfWeek.SATURDAY, 2131361828),
            Arguments.of(Theme.BLACK, DayOfWeek.SUNDAY, 2131361829),
            Arguments.of(Theme.GREY, DayOfWeek.MONDAY, 2131361839),
            Arguments.of(Theme.GREY, DayOfWeek.TUESDAY, 2131361839),
            Arguments.of(Theme.GREY, DayOfWeek.WEDNESDAY, 2131361839),
            Arguments.of(Theme.GREY, DayOfWeek.THURSDAY, 2131361839),
            Arguments.of(Theme.GREY, DayOfWeek.FRIDAY, 2131361839),
            Arguments.of(Theme.GREY, DayOfWeek.SATURDAY, 2131361840),
            Arguments.of(Theme.GREY, DayOfWeek.SUNDAY, 2131361841),
            Arguments.of(Theme.WHITE, DayOfWeek.MONDAY, 2131361862),
            Arguments.of(Theme.WHITE, DayOfWeek.TUESDAY, 2131361862),
            Arguments.of(Theme.WHITE, DayOfWeek.WEDNESDAY, 2131361862),
            Arguments.of(Theme.WHITE, DayOfWeek.THURSDAY, 2131361862),
            Arguments.of(Theme.WHITE, DayOfWeek.FRIDAY, 2131361862),
            Arguments.of(Theme.WHITE, DayOfWeek.SATURDAY, 2131361863),
            Arguments.of(Theme.WHITE, DayOfWeek.SUNDAY, 2131361864)
        )

        @JvmStatic
        @Suppress("unused", "LongMethod")
        fun getCombinationOfThemesAndDayStatuses(): Stream<Arguments> = Stream.of(
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.MONDAY, 2131361826),
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.TUESDAY, 2131361826),
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.WEDNESDAY, 2131361826),
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.THURSDAY, 2131361826),
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.FRIDAY, 2131361826),
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.SATURDAY, 2131361822),
            Arguments.of(Theme.BLACK, true, true, DayOfWeek.SUNDAY, 2131361824),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.MONDAY, 2131361825),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.TUESDAY, 2131361825),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.WEDNESDAY, 2131361825),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.THURSDAY, 2131361825),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.FRIDAY, 2131361825),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.SATURDAY, 2131361821),
            Arguments.of(Theme.BLACK, false, true, DayOfWeek.SUNDAY, 2131361823),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.MONDAY, 2131361820),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.TUESDAY, 2131361820),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.WEDNESDAY, 2131361820),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.THURSDAY, 2131361820),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.FRIDAY, 2131361820),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.SATURDAY, 2131361820),
            Arguments.of(Theme.BLACK, false, false, DayOfWeek.SUNDAY, 2131361820),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.MONDAY, 2131361838),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.TUESDAY, 2131361838),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.WEDNESDAY, 2131361838),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.THURSDAY, 2131361838),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.FRIDAY, 2131361838),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.SATURDAY, 2131361834),
            Arguments.of(Theme.GREY, true, true, DayOfWeek.SUNDAY, 2131361836),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.MONDAY, 2131361837),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.TUESDAY, 2131361837),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.WEDNESDAY, 2131361837),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.THURSDAY, 2131361837),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.FRIDAY, 2131361837),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.SATURDAY, 2131361833),
            Arguments.of(Theme.GREY, false, true, DayOfWeek.SUNDAY, 2131361835),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.MONDAY, 2131361832),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.TUESDAY, 2131361832),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.WEDNESDAY, 2131361832),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.THURSDAY, 2131361832),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.FRIDAY, 2131361832),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.SATURDAY, 2131361832),
            Arguments.of(Theme.GREY, false, false, DayOfWeek.SUNDAY, 2131361832),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.MONDAY, 2131361861),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.TUESDAY, 2131361861),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.WEDNESDAY, 2131361861),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.THURSDAY, 2131361861),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.FRIDAY, 2131361861),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.SATURDAY, 2131361857),
            Arguments.of(Theme.WHITE, true, true, DayOfWeek.SUNDAY, 2131361859),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.MONDAY, 2131361860),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.TUESDAY, 2131361860),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.WEDNESDAY, 2131361860),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.THURSDAY, 2131361860),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.FRIDAY, 2131361860),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.SATURDAY, 2131361856),
            Arguments.of(Theme.WHITE, false, true, DayOfWeek.SUNDAY, 2131361858),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.MONDAY, 2131361855),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.TUESDAY, 2131361855),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.WEDNESDAY, 2131361855),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.THURSDAY, 2131361855),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.FRIDAY, 2131361855),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.SATURDAY, 2131361855),
            Arguments.of(Theme.WHITE, false, false, DayOfWeek.SUNDAY, 2131361855)
        )
    }
}
