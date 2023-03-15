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

private const val DARK_THEME_MAIN_TEXT_COLOUR = 2131034226
private const val LIGHT_THEME_MAIN_TEXT_COLOUR = 2131034227

private const val CELL_HEADER_VIEW = 2131230790
private const val CELL_HEADER_LAYOUT = 2131427357

// CELL

private const val CELL_DAY_VIEW = 2131230789
private const val CELL_DAY_LAYOUT = 2131427356

private const val DARK_THEME_CELL_TEXT_COLOUR = 2131034228
private const val LIGHT_THEME_CELL_TEXT_COLOUR = 2131034229

private const val SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND = 2131034149
private const val SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND = 2131034155
private const val SATURDAY_DARK_THEME_CELL_BACKGROUND = 2131034147
private const val SUNDAY_DARK_THEME_CELL_BACKGROUND = 2131034153
private const val TODAY_DARK_THEME_CELL_BACKGROUND = 2131034161
private const val IN_MONTH_DARK_THEME_CELL_BACKGROUND = 2131034159

private const val TODAY_LIGHT_THEME_CELL_BACKGROUND = 2131034162
private const val IN_MONTH_LIGHT_THEME_CELL_BACKGROUND = 2131034160
private const val SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND = 2131034150
private const val SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND = 2131034156

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

    private fun getCombinationOfThemesAndDaysOfWeekWithExpectedCellHeader(): Stream<Arguments> = Stream.of(
        Arguments.of(
            Theme.DARK,
            DayOfWeek.MONDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            DayOfWeek.TUESDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            DayOfWeek.WEDNESDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            DayOfWeek.THURSDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            DayOfWeek.FRIDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            DayOfWeek.SATURDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            DayOfWeek.SUNDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.MONDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.TUESDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.WEDNESDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.THURSDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.FRIDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.SATURDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.SUNDAY,
            Cell(
                id = CELL_HEADER_VIEW,
                layout = CELL_HEADER_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        )
    )

    private fun getCombinationOfThemesAndDayStatusesWithExpectedCellDay(): Stream<Arguments> = Stream.of(
        Arguments.of(
            Theme.DARK,
            true,
            true,
            DayOfWeek.MONDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = TODAY_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            true,
            true,
            DayOfWeek.TUESDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = TODAY_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            true,
            true,
            DayOfWeek.WEDNESDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = TODAY_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            true,
            true,
            DayOfWeek.THURSDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = TODAY_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            true,
            true,
            DayOfWeek.FRIDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = TODAY_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            true,
            true,
            DayOfWeek.SATURDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = 2131034151
            )
        ),
        Arguments.of(
            Theme.DARK,
            true,
            true,
            DayOfWeek.SUNDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = 2131034157
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            true,
            DayOfWeek.MONDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            true,
            DayOfWeek.TUESDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            true,
            DayOfWeek.WEDNESDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            true,
            DayOfWeek.THURSDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            true,
            DayOfWeek.FRIDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            true,
            DayOfWeek.SATURDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            true,
            DayOfWeek.SUNDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_MAIN_TEXT_COLOUR,
                background = SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            false,
            DayOfWeek.MONDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_CELL_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            false,
            DayOfWeek.TUESDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_CELL_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            false,
            DayOfWeek.WEDNESDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_CELL_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            false,
            DayOfWeek.THURSDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_CELL_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            false,
            DayOfWeek.FRIDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_CELL_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            false,
            DayOfWeek.SATURDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_CELL_TEXT_COLOUR,
                background = SATURDAY_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            false,
            DayOfWeek.SUNDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = DARK_THEME_CELL_TEXT_COLOUR,
                background = SUNDAY_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            true,
            DayOfWeek.MONDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = TODAY_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            true,
            DayOfWeek.TUESDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = TODAY_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            true,
            DayOfWeek.WEDNESDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = TODAY_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            true,
            DayOfWeek.THURSDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = TODAY_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            true,
            DayOfWeek.FRIDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = TODAY_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            true,
            DayOfWeek.SATURDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = 2131034152
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            true,
            DayOfWeek.SUNDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = 2131034158
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            true,
            DayOfWeek.MONDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            true,
            DayOfWeek.TUESDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            true,
            DayOfWeek.WEDNESDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            true,
            DayOfWeek.THURSDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            true,
            DayOfWeek.FRIDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            true,
            DayOfWeek.SATURDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            true,
            DayOfWeek.SUNDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_MAIN_TEXT_COLOUR,
                background = SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            false,
            DayOfWeek.MONDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_CELL_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            false,
            DayOfWeek.TUESDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_CELL_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            false,
            DayOfWeek.WEDNESDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_CELL_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            false,
            DayOfWeek.THURSDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_CELL_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            false,
            DayOfWeek.FRIDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_CELL_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            false,
            DayOfWeek.SATURDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_CELL_TEXT_COLOUR,
                background = 2131034148
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            false,
            DayOfWeek.SUNDAY,
            Cell(
                id = CELL_DAY_VIEW,
                layout = CELL_DAY_LAYOUT,
                textColour = LIGHT_THEME_CELL_TEXT_COLOUR,
                background = 2131034154
            )
        )
    )
}
