// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.CellStyle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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
    @CsvSource(
        "DARK,MONDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,",
        "DARK,TUESDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,",
        "DARK,WEDNESDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,",
        "DARK,THURSDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,",
        "DARK,FRIDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,",
        "DARK,SATURDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,$SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND",
        "DARK,SUNDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,$SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND",
        "LIGHT,MONDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,",
        "LIGHT,TUESDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,",
        "LIGHT,WEDNESDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,",
        "LIGHT,THURSDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,",
        "LIGHT,FRIDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,",
        "LIGHT,SATURDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,$SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND",
        "LIGHT,SUNDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,$SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND"
    )
    fun getCellHeader(
        widgetTheme: Theme,
        dayOfWeek: DayOfWeek,
        expectedCellThemeTextColour: Int,
        expectedCellThemeBackground: Int?
    ) {
        val expectedResult = CellStyle(
            textColour = expectedCellThemeTextColour,
            background = expectedCellThemeBackground
        )

        val result = widgetTheme.getCellHeader(dayOfWeek)

        assertThat(result).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @CsvSource(
        "DARK,true,MONDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,$IN_MONTH_DARK_THEME_CELL_BACKGROUND",
        "DARK,true,TUESDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,$IN_MONTH_DARK_THEME_CELL_BACKGROUND",
        "DARK,true,WEDNESDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,$IN_MONTH_DARK_THEME_CELL_BACKGROUND",
        "DARK,true,THURSDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,$IN_MONTH_DARK_THEME_CELL_BACKGROUND",
        "DARK,true,FRIDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,$IN_MONTH_DARK_THEME_CELL_BACKGROUND",
        "DARK,true,SATURDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,$SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND",
        "DARK,true,SUNDAY,$DARK_THEME_IN_MONTH_TEXT_COLOUR,$SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND",
        "DARK,false,MONDAY,$DARK_THEME_TEXT_COLOUR,",
        "DARK,false,TUESDAY,$DARK_THEME_TEXT_COLOUR,",
        "DARK,false,WEDNESDAY,$DARK_THEME_TEXT_COLOUR,",
        "DARK,false,THURSDAY,$DARK_THEME_TEXT_COLOUR,",
        "DARK,false,FRIDAY,$DARK_THEME_TEXT_COLOUR,",
        "DARK,false,SATURDAY,$DARK_THEME_TEXT_COLOUR,$SATURDAY_DARK_THEME_CELL_BACKGROUND",
        "DARK,false,SUNDAY,$DARK_THEME_TEXT_COLOUR,$SUNDAY_DARK_THEME_CELL_BACKGROUND",
        "LIGHT,true,MONDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,$IN_MONTH_LIGHT_THEME_CELL_BACKGROUND",
        "LIGHT,true,TUESDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,$IN_MONTH_LIGHT_THEME_CELL_BACKGROUND",
        "LIGHT,true,WEDNESDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,$IN_MONTH_LIGHT_THEME_CELL_BACKGROUND",
        "LIGHT,true,THURSDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,$IN_MONTH_LIGHT_THEME_CELL_BACKGROUND",
        "LIGHT,true,FRIDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,$IN_MONTH_LIGHT_THEME_CELL_BACKGROUND",
        "LIGHT,true,SATURDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,$SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND",
        "LIGHT,true,SUNDAY,$LIGHT_THEME_IN_MONTH_TEXT_COLOUR,$SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND",
        "LIGHT,false,MONDAY,$LIGHT_THEME_TEXT_COLOUR,",
        "LIGHT,false,TUESDAY,$LIGHT_THEME_TEXT_COLOUR,",
        "LIGHT,false,WEDNESDAY,$LIGHT_THEME_TEXT_COLOUR,",
        "LIGHT,false,THURSDAY,$LIGHT_THEME_TEXT_COLOUR,",
        "LIGHT,false,FRIDAY,$LIGHT_THEME_TEXT_COLOUR,",
        "LIGHT,false,SATURDAY,$LIGHT_THEME_TEXT_COLOUR,$SATURDAY_LIGHT_THEME_CELL_BACKGROUND",
        "LIGHT,false,SUNDAY,$LIGHT_THEME_TEXT_COLOUR,$SUNDAY_LIGHT_THEME_CELL_BACKGROUND"
    )
    fun getCellDay(
        widgetTheme: Theme,
        inMonth: Boolean,
        dayOfWeek: DayOfWeek,
        expectedCellThemeTextColour: Int,
        expectedCellThemeBackground: Int?
    ) {
        val expectedResult = CellStyle(
            textColour = expectedCellThemeTextColour,
            background = expectedCellThemeBackground
        )

        val result = widgetTheme.getCellDay(
            inMonth = inMonth,
            dayOfWeek = dayOfWeek
        )

        assertThat(result).isEqualTo(expectedResult)
    }
}