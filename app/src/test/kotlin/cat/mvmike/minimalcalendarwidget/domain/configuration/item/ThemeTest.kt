// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.util.stream.Stream

const val cellViewId = 16908308

const val saturdayInMonthDarkThemeCellBackground = 2131034150
const val sundayInMonthDarkThemeCellBackground = 2131034156
const val saturdayDarkThemeCellBackground = 2131034148
const val sundayDarkThemeCellBackground = 2131034154
const val saturdayInMonthLightThemeCellBackground = 2131034151
const val sundayInMonthLightThemeCellBackground = 2131034157

const val todayDarkThemeCellBackground = 2131034162
const val todayDarkThemeCellLayout = 2131427360
const val inMonthDarkThemeCellBackground = 2131034160
const val inMonthDarkThemeCellLayout = 2131427359
const val darkThemeCellLayout = 2131427358

private const val darkThemeHeaderLayout = 2131427361
private const val lightThemeHeaderLayout = 2131427366

private const val todayLightThemeCellBackground = 2131034163
private const val todayLightThemeCellLayout = 2131427365
private const val inMonthLightThemeCellBackground = 2131034161
private const val inMonthLightThemeCellLayout = 2131427364
private const val lightThemeCellLayout = 2131427363

internal class ThemeTest : BaseTest() {

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
            Cell(id = cellViewId, layout = darkThemeHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = darkThemeHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = darkThemeHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = darkThemeHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = darkThemeHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = darkThemeHeaderLayout, background = saturdayInMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = darkThemeHeaderLayout, background = sundayInMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = lightThemeHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = lightThemeHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = lightThemeHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = lightThemeHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = lightThemeHeaderLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = lightThemeHeaderLayout, background = saturdayInMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = lightThemeHeaderLayout, background = sundayInMonthLightThemeCellBackground)
        )
    )

    @Suppress("unused", "LongMethod")
    private fun getCombinationOfThemesAndDayStatuses(): Stream<Arguments> = Stream.of(
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = todayDarkThemeCellLayout, background = todayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = todayDarkThemeCellLayout, background = todayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = todayDarkThemeCellLayout, background = todayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = todayDarkThemeCellLayout, background = todayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = todayDarkThemeCellLayout, background = todayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = todayDarkThemeCellLayout, background = 2131034152)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = todayDarkThemeCellLayout, background = 2131034158)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = inMonthDarkThemeCellLayout, background = inMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = inMonthDarkThemeCellLayout, background = inMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = inMonthDarkThemeCellLayout, background = inMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = inMonthDarkThemeCellLayout, background = inMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = inMonthDarkThemeCellLayout, background = inMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = inMonthDarkThemeCellLayout, background = saturdayInMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = inMonthDarkThemeCellLayout, background = sundayInMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = darkThemeCellLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = darkThemeCellLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = darkThemeCellLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = darkThemeCellLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = darkThemeCellLayout, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = darkThemeCellLayout, background = saturdayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = darkThemeCellLayout, background = sundayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = todayLightThemeCellLayout, background = todayLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = todayLightThemeCellLayout, background = todayLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = todayLightThemeCellLayout, background = todayLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = todayLightThemeCellLayout, background = todayLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = todayLightThemeCellLayout, background = todayLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = todayLightThemeCellLayout, background = 2131034153)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = todayLightThemeCellLayout, background = 2131034159)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = inMonthLightThemeCellLayout, background = inMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = inMonthLightThemeCellLayout, background = inMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = inMonthLightThemeCellLayout, background = inMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = inMonthLightThemeCellLayout, background = inMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = inMonthLightThemeCellLayout, background = inMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = inMonthLightThemeCellLayout, background = saturdayInMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = inMonthLightThemeCellLayout, background = sundayInMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.MONDAY,
            Cell(id = cellViewId, layout = lightThemeCellLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.TUESDAY,
            Cell(id = cellViewId, layout = lightThemeCellLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.WEDNESDAY,
            Cell(id = cellViewId, layout = lightThemeCellLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.THURSDAY,
            Cell(id = cellViewId, layout = lightThemeCellLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.FRIDAY,
            Cell(id = cellViewId, layout = lightThemeCellLayout, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.SATURDAY,
            Cell(id = cellViewId, layout = lightThemeCellLayout, background = 2131034149)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.SUNDAY,
            Cell(id = cellViewId, layout = lightThemeCellLayout, background = 2131034155)
        )
    )
}
