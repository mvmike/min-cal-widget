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

internal const val DARK_THEME_MAIN_TEXT_COLOUR = 2131034226
internal const val LIGHT_THEME_MAIN_TEXT_COLOUR = 2131034227

private const val HEADER_LAYOUT = 2131427357

// CELL

internal const val CELL_VIEW_ID = 16908308
internal const val CELL_LAYOUT = 2131427356

internal const val DARK_THEME_CELL_TEXT_COLOUR = 2131034228
internal const val LIGHT_THEME_CELL_TEXT_COLOUR = 2131034229

internal const val SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND = 2131034149
internal const val SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND = 2131034155
internal const val SATURDAY_DARK_THEME_CELL_BACKGROUND = 2131034147
internal const val SUNDAY_DARK_THEME_CELL_BACKGROUND = 2131034153
internal const val TODAY_DARK_THEME_CELL_BACKGROUND = 2131034161
internal const val IN_MONTH_DARK_THEME_CELL_BACKGROUND = 2131034159

private const val TODAY_LIGHT_THEME_CELL_BACKGROUND = 2131034162
private const val IN_MONTH_LIGHT_THEME_CELL_BACKGROUND = 2131034160
internal const val SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND = 2131034150
internal const val SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND = 2131034156

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
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.TUESDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.WEDNESDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.THURSDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.FRIDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.SATURDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, DayOfWeek.SUNDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.MONDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.TUESDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.WEDNESDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.THURSDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.FRIDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.SATURDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, DayOfWeek.SUNDAY,
            Cell(CELL_VIEW_ID, layout = HEADER_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
        )
    )

    @Suppress("UnusedPrivateMember", "LongMethod")
    private fun getCombinationOfThemesAndDayStatusesWithExpectedCellDay(): Stream<Arguments> = Stream.of(
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.MONDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = TODAY_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.TUESDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = TODAY_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.WEDNESDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = TODAY_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.THURSDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = TODAY_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.FRIDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = TODAY_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.SATURDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = 2131034151)
        ),
        Arguments.of(
            Theme.DARK, true, true, DayOfWeek.SUNDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = 2131034157)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.MONDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = IN_MONTH_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.TUESDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = IN_MONTH_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.WEDNESDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = IN_MONTH_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.THURSDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = IN_MONTH_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.FRIDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = IN_MONTH_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.SATURDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, false, true, DayOfWeek.SUNDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_MAIN_TEXT_COLOUR, background = SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.MONDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_CELL_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.TUESDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_CELL_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.WEDNESDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_CELL_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.THURSDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_CELL_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.FRIDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_CELL_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.SATURDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_CELL_TEXT_COLOUR, background = SATURDAY_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.DARK, false, false, DayOfWeek.SUNDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = DARK_THEME_CELL_TEXT_COLOUR, background = SUNDAY_DARK_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.MONDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = TODAY_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.TUESDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = TODAY_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.WEDNESDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = TODAY_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.THURSDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = TODAY_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.FRIDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = TODAY_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.SATURDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = 2131034152)
        ),
        Arguments.of(
            Theme.LIGHT, true, true, DayOfWeek.SUNDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = 2131034158)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.MONDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.TUESDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.WEDNESDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.THURSDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.FRIDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.SATURDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, false, true, DayOfWeek.SUNDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_MAIN_TEXT_COLOUR, background = SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.MONDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_CELL_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.TUESDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_CELL_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.WEDNESDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_CELL_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.THURSDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_CELL_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.FRIDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_CELL_TEXT_COLOUR, background = null)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.SATURDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_CELL_TEXT_COLOUR, background = 2131034148)
        ),
        Arguments.of(
            Theme.LIGHT, false, false, DayOfWeek.SUNDAY,
            Cell(CELL_VIEW_ID, layout = CELL_LAYOUT, textColour = LIGHT_THEME_CELL_TEXT_COLOUR, background = 2131034154)
        )
    )
}
