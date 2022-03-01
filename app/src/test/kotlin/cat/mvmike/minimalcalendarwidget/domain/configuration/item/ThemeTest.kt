// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import java.time.DayOfWeek
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

private const val cellViewId = 16908308

private const val darkHeaderLayout = 2131427361
private const val lightHeaderLayout = 2131427365

private const val darkTodayBackground = 2131034162
private const val darkInMonthBackground = 2131034160
private const val darkTodayLayout = 2131427360
private const val darkInMonthLayout = 2131427359
private const val darkLayout = 2131427358

private const val lightTodayBackground = 2131034163
private const val lightInMonthBackground = 2131034161
private const val lightTodayLayout = 2131427364
private const val lightInMonthLayout = 2131427363
private const val lightLayout = 2131427362

internal class ThemeTest: BaseTest() {

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

    @Suppress("unused")
    private fun getCombinationOfThemesAndDaysOfWeek(): Stream<Arguments> = Stream.of(
        Arguments.of(
            Theme.DARK, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = darkHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = darkHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = darkHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = darkHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = darkHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = darkHeaderLayout, background = 2131034150)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = darkHeaderLayout, background = 2131034156)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = lightHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = lightHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = lightHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = lightHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = lightHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = lightHeaderLayout, background = 2131034151)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = lightHeaderLayout, background = 2131034157)
        )
    )

    @Suppress("unused", "LongMethod")
    private fun getCombinationOfThemesAndDayStatuses(): Stream<Arguments> = Stream.of(
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = darkTodayLayout, background = darkTodayBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = darkTodayLayout, background = darkTodayBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = darkTodayLayout, background = darkTodayBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = darkTodayLayout, background = darkTodayBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = darkTodayLayout, background = darkTodayBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = darkTodayLayout, background = 2131034152)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = darkTodayLayout, background = 2131034158)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = darkInMonthLayout, background = darkInMonthBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = darkInMonthLayout, background = darkInMonthBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = darkInMonthLayout, background = darkInMonthBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = darkInMonthLayout, background = darkInMonthBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = darkInMonthLayout, background = darkInMonthBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = darkInMonthLayout, background = 2131034150)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = darkInMonthLayout, background = 2131034156)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = darkLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = darkLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = darkLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = darkLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = darkLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = darkLayout, background = 2131034148)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = darkLayout, background = 2131034154)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = lightTodayLayout, background = lightTodayBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = lightTodayLayout, background = lightTodayBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = lightTodayLayout, background = lightTodayBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = lightTodayLayout, background = lightTodayBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = lightTodayLayout, background = lightTodayBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = lightTodayLayout, background = 2131034153)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = lightTodayLayout, background = 2131034159)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = lightInMonthLayout, background = lightInMonthBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = lightInMonthLayout, background = lightInMonthBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = lightInMonthLayout, background = lightInMonthBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = lightInMonthLayout, background = lightInMonthBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = lightInMonthLayout, background = lightInMonthBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = lightInMonthLayout, background = 2131034151)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = lightInMonthLayout, background = 2131034157)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = lightLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = lightLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = lightLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = lightLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = lightLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = lightLayout, background = 2131034149)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = lightLayout, background = 2131034155)
        )
    )
}
