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

// MAIN

internal const val darkThemeMainTextColour = 2131034227
internal const val lightThemeMainTextColour = 2131034228

private const val headerLayout = 2131427357

// CELL

internal const val cellViewId = 16908308
internal const val cellLayout = 2131427356

internal const val darkThemeCellTextColour = 2131034229
internal const val lightThemeCellTextColour = 2131034230

internal const val saturdayInMonthDarkThemeCellBackground = 2131034149
internal const val sundayInMonthDarkThemeCellBackground = 2131034155
internal const val saturdayDarkThemeCellBackground = 2131034147
internal const val sundayDarkThemeCellBackground = 2131034153
internal const val todayDarkThemeCellBackground = 2131034161
internal const val inMonthDarkThemeCellBackground = 2131034159

private const val todayLightThemeCellBackground = 2131034162
private const val inMonthLightThemeCellBackground = 2131034160
internal const val saturdayInMonthLightThemeCellBackground = 2131034150
internal const val sundayInMonthLightThemeCellBackground = 2131034156

internal class ThemeTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndDaysOfWeekWithExpectedCellHeader")
    fun getCellHeader(theme: Theme, dayOfWeek: DayOfWeek, expectedResult: Cell) {
        val result = theme.getCellHeader(dayOfWeek)

        assertThat(result).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndDayStatusesWithExpectedCellDay")
    fun getCellDay(theme: Theme, isToday: Boolean, inMonth: Boolean, dayOfWeek: DayOfWeek, expectedResult: Cell) {
        val result = theme.getCellDay(
            isToday = isToday,
            inMonth = inMonth,
            dayOfWeek = dayOfWeek
        )

        assertThat(result).isEqualTo(expectedResult)
    }

    @Suppress("UnusedPrivateMember")
    private fun getCombinationOfThemesAndDaysOfWeekWithExpectedCellHeader(): Stream<Arguments> = Stream.of(
        Arguments.of(
            Theme.DARK, DayOfWeek.MONDAY,
            Cell(cellViewId, layout = headerLayout, textColour = darkThemeMainTextColour, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.TUESDAY,
            Cell(cellViewId, layout = headerLayout, textColour = darkThemeMainTextColour, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.WEDNESDAY,
            Cell(cellViewId, layout = headerLayout, textColour = darkThemeMainTextColour, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.THURSDAY,
            Cell(cellViewId, layout = headerLayout, textColour = darkThemeMainTextColour, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.FRIDAY,
            Cell(cellViewId, layout = headerLayout, textColour = darkThemeMainTextColour, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.SATURDAY,
            Cell(cellViewId, layout = headerLayout, textColour = darkThemeMainTextColour, background = saturdayInMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.SUNDAY,
            Cell(cellViewId, layout = headerLayout, textColour = darkThemeMainTextColour, background = sundayInMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.MONDAY,
            Cell(cellViewId, layout = headerLayout, textColour = lightThemeMainTextColour, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.TUESDAY,
            Cell(cellViewId, layout = headerLayout, textColour = lightThemeMainTextColour, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.WEDNESDAY,
            Cell(cellViewId, layout = headerLayout, textColour = lightThemeMainTextColour, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.THURSDAY,
            Cell(cellViewId, layout = headerLayout, textColour = lightThemeMainTextColour, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.FRIDAY,
            Cell(cellViewId, layout = headerLayout, textColour = lightThemeMainTextColour, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.SATURDAY,
            Cell(cellViewId, layout = headerLayout, textColour = lightThemeMainTextColour, background = saturdayInMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.SUNDAY,
            Cell(cellViewId, layout = headerLayout, textColour = lightThemeMainTextColour, background = sundayInMonthLightThemeCellBackground)
        )
    )

    @Suppress("UnusedPrivateMember", "LongMethod")
    private fun getCombinationOfThemesAndDayStatusesWithExpectedCellDay(): Stream<Arguments> = Stream.of(
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.MONDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = todayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.TUESDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = todayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.WEDNESDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = todayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.THURSDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = todayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.FRIDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = todayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.SATURDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = 2131034151)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.SUNDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = 2131034157)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.MONDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = inMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.TUESDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = inMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.WEDNESDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = inMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.THURSDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = inMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.FRIDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = inMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.SATURDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = saturdayInMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.SUNDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeMainTextColour, background = sundayInMonthDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.MONDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeCellTextColour, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.TUESDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeCellTextColour, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.WEDNESDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeCellTextColour, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.THURSDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeCellTextColour, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.FRIDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeCellTextColour, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.SATURDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeCellTextColour, background = saturdayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.SUNDAY,
            Cell(cellViewId, layout = cellLayout, textColour = darkThemeCellTextColour, background = sundayDarkThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.MONDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = todayLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.TUESDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = todayLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.WEDNESDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = todayLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.THURSDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = todayLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.FRIDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = todayLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.SATURDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = 2131034152)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.SUNDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = 2131034158)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.MONDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = inMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.TUESDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = inMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.WEDNESDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = inMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.THURSDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = inMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.FRIDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = inMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.SATURDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = saturdayInMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.SUNDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeMainTextColour, background = sundayInMonthLightThemeCellBackground)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.MONDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeCellTextColour, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.TUESDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeCellTextColour, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.WEDNESDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeCellTextColour, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.THURSDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeCellTextColour, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.FRIDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeCellTextColour, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.SATURDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeCellTextColour, background = 2131034148)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.SUNDAY,
            Cell(cellViewId, layout = cellLayout, textColour = lightThemeCellTextColour, background = 2131034154)
        )
    )
}
