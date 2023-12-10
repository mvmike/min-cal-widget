// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek

// TEXT COLOUR

private const val DARK_THEME_IN_MONTH_TEXT_COLOUR = 2131034222
private const val DARK_THEME_TEXT_COLOUR = 2131034224

private const val LIGHT_THEME_IN_MONTH_TEXT_COLOUR = 2131034223
private const val LIGHT_THEME_TEXT_COLOUR = 2131034225

// BACKGROUND DARK THEME

private const val IN_MONTH_DARK_THEME_CELL_BACKGROUND = 2131034155
private const val SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND = 2131034149
private const val SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND = 2131034153
private const val SATURDAY_DARK_THEME_CELL_BACKGROUND = 2131034147
private const val SUNDAY_DARK_THEME_CELL_BACKGROUND = 2131034151

// BACKGROUND LIGHT THEME

private const val IN_MONTH_LIGHT_THEME_CELL_BACKGROUND = 2131034156
private const val SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND = 2131034150
private const val SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND = 2131034154
private const val SATURDAY_LIGHT_THEME_CELL_BACKGROUND = 2131034148
private const val SUNDAY_LIGHT_THEME_CELL_BACKGROUND = 2131034152

internal class ThemeTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndDaysOfWeekWithExpectedCellHeader")
    fun getCellHeader(
        widgetTheme: Theme,
        dayOfWeek: DayOfWeek,
        expectedResult: CellTheme
    ) {
        val result = widgetTheme.getCellHeader(dayOfWeek)

        assertThat(result).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndDayStatusesWithExpectedCellDay")
    fun getCellDay(
        widgetTheme: Theme,
        inMonth: Boolean,
        dayOfWeek: DayOfWeek,
        expectedResult: CellTheme
    ) {
        val result = widgetTheme.getCellDay(
            inMonth = inMonth,
            dayOfWeek = dayOfWeek
        )

        assertThat(result).isEqualTo(expectedResult)
    }

    private fun getCombinationOfThemesAndDaysOfWeekWithExpectedCellHeader() = listOf(
        Arguments.of(
            Theme.DARK,
            DayOfWeek.MONDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            DayOfWeek.TUESDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            DayOfWeek.WEDNESDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            DayOfWeek.THURSDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            DayOfWeek.FRIDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            DayOfWeek.SATURDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            DayOfWeek.SUNDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.MONDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.TUESDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.WEDNESDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.THURSDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.FRIDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.SATURDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            DayOfWeek.SUNDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        )
    )

    private fun getCombinationOfThemesAndDayStatusesWithExpectedCellDay() = listOf(
        Arguments.of(
            Theme.DARK,
            true,
            DayOfWeek.MONDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            true,
            DayOfWeek.TUESDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            true,
            DayOfWeek.WEDNESDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            true,
            DayOfWeek.THURSDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            true,
            DayOfWeek.FRIDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            true,
            DayOfWeek.SATURDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            true,
            DayOfWeek.SUNDAY,
            CellTheme(
                textColour = DARK_THEME_IN_MONTH_TEXT_COLOUR,
                background = SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            DayOfWeek.MONDAY,
            CellTheme(
                textColour = DARK_THEME_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            DayOfWeek.TUESDAY,
            CellTheme(
                textColour = DARK_THEME_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            DayOfWeek.WEDNESDAY,
            CellTheme(
                textColour = DARK_THEME_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            DayOfWeek.THURSDAY,
            CellTheme(
                textColour = DARK_THEME_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            DayOfWeek.FRIDAY,
            CellTheme(
                textColour = DARK_THEME_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            DayOfWeek.SATURDAY,
            CellTheme(
                textColour = DARK_THEME_TEXT_COLOUR,
                background = SATURDAY_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.DARK,
            false,
            DayOfWeek.SUNDAY,
            CellTheme(
                textColour = DARK_THEME_TEXT_COLOUR,
                background = SUNDAY_DARK_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            DayOfWeek.MONDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            DayOfWeek.TUESDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            DayOfWeek.WEDNESDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            DayOfWeek.THURSDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            DayOfWeek.FRIDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            DayOfWeek.SATURDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            true,
            DayOfWeek.SUNDAY,
            CellTheme(
                textColour = LIGHT_THEME_IN_MONTH_TEXT_COLOUR,
                background = SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            DayOfWeek.MONDAY,
            CellTheme(
                textColour = LIGHT_THEME_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            DayOfWeek.TUESDAY,
            CellTheme(
                textColour = LIGHT_THEME_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            DayOfWeek.WEDNESDAY,
            CellTheme(
                textColour = LIGHT_THEME_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            DayOfWeek.THURSDAY,
            CellTheme(
                textColour = LIGHT_THEME_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            DayOfWeek.FRIDAY,
            CellTheme(
                textColour = LIGHT_THEME_TEXT_COLOUR,
                background = null
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            DayOfWeek.SATURDAY,
            CellTheme(
                textColour = LIGHT_THEME_TEXT_COLOUR,
                background = SATURDAY_LIGHT_THEME_CELL_BACKGROUND
            )
        ),
        Arguments.of(
            Theme.LIGHT,
            false,
            DayOfWeek.SUNDAY,
            CellTheme(
                textColour = LIGHT_THEME_TEXT_COLOUR,
                background = SUNDAY_LIGHT_THEME_CELL_BACKGROUND
            )
        )
    )
}