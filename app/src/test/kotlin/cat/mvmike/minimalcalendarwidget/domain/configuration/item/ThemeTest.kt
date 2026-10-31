// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.CellStyle
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme.DARK
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme.LIGHT
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY

internal class ThemeTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getThemeAndDayOfWeekWithExpectedCellHeader")
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
    @MethodSource("getThemeAndInMonthAndDayOfWeekWithExpectedCellDay")
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

    @ParameterizedTest
    @MethodSource("getThemeWithExpectedCellWeekNumberTextColour")
    fun getCellWeekNumber(
        widgetTheme: Theme,
        expectedCellThemeTextColour: Int
    ) {
        val expectedResult = CellStyle(
            textColour = expectedCellThemeTextColour,
            background = null
        )

        val result = widgetTheme.getCellWeekNumber()

        assertThat(result).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @MethodSource("getThemeWithExpectedStringResource")
    fun getDisplayValue_shouldReturnCapitalisedTranslation(
        widgetTheme: Theme,
        stringResourceId: Int
    ) {
        every { context.getString(stringResourceId) } returns "some theme"

        val result = widgetTheme.getDisplayValue(context)

        assertThat(result).isEqualTo("Some theme")
        verify { context.getString(stringResourceId) }
    }

    @Test
    fun getThemeDisplayValues_shouldReturnCapitalisedTranslationOfEveryTheme() {
        every { context.getString(R.string.dark) } returns "dark"
        every { context.getString(R.string.light) } returns "light"

        val result = getThemeDisplayValues(context)

        assertThat(result).containsExactly("Dark", "Light")
        verify { context.getString(R.string.dark) }
        verify { context.getString(R.string.light) }
    }

    private fun getThemeWithExpectedStringResource() = listOf(
        of(DARK, R.string.dark),
        of(LIGHT, R.string.light)
    )

    private fun getThemeAndDayOfWeekWithExpectedCellHeader() = listOf(
        of(DARK, MONDAY, R.color.text_colour_dark, null),
        of(DARK, TUESDAY, R.color.text_colour_dark, null),
        of(DARK, WEDNESDAY, R.color.text_colour_dark, null),
        of(DARK, THURSDAY, R.color.text_colour_dark, null),
        of(DARK, FRIDAY, R.color.text_colour_dark, null),
        of(DARK, SATURDAY, R.color.text_colour_dark, R.color.background_saturday_this_month_dark),
        of(DARK, SUNDAY, R.color.text_colour_dark, R.color.background_sunday_this_month_dark),
        of(LIGHT, MONDAY, R.color.text_colour_light, null),
        of(LIGHT, TUESDAY, R.color.text_colour_light, null),
        of(LIGHT, WEDNESDAY, R.color.text_colour_light, null),
        of(LIGHT, THURSDAY, R.color.text_colour_light, null),
        of(LIGHT, FRIDAY, R.color.text_colour_light, null),
        of(LIGHT, SATURDAY, R.color.text_colour_light, R.color.background_saturday_this_month_light),
        of(LIGHT, SUNDAY, R.color.text_colour_light, R.color.background_sunday_this_month_light)
    )

    private fun getThemeAndInMonthAndDayOfWeekWithExpectedCellDay() = listOf(
        of(DARK, true, MONDAY, R.color.text_colour_dark, R.color.background_this_month_dark),
        of(DARK, true, TUESDAY, R.color.text_colour_dark, R.color.background_this_month_dark),
        of(DARK, true, WEDNESDAY, R.color.text_colour_dark, R.color.background_this_month_dark),
        of(DARK, true, THURSDAY, R.color.text_colour_dark, R.color.background_this_month_dark),
        of(DARK, true, FRIDAY, R.color.text_colour_dark, R.color.background_this_month_dark),
        of(DARK, true, SATURDAY, R.color.text_colour_dark, R.color.background_saturday_this_month_dark),
        of(DARK, true, SUNDAY, R.color.text_colour_dark, R.color.background_sunday_this_month_dark),
        of(DARK, false, MONDAY, R.color.text_colour_semi_dark, null),
        of(DARK, false, TUESDAY, R.color.text_colour_semi_dark, null),
        of(DARK, false, WEDNESDAY, R.color.text_colour_semi_dark, null),
        of(DARK, false, THURSDAY, R.color.text_colour_semi_dark, null),
        of(DARK, false, FRIDAY, R.color.text_colour_semi_dark, null),
        of(DARK, false, SATURDAY, R.color.text_colour_semi_dark, R.color.background_saturday_dark),
        of(DARK, false, SUNDAY, R.color.text_colour_semi_dark, R.color.background_sunday_dark),
        of(LIGHT, true, MONDAY, R.color.text_colour_light, R.color.background_this_month_light),
        of(LIGHT, true, TUESDAY, R.color.text_colour_light, R.color.background_this_month_light),
        of(LIGHT, true, WEDNESDAY, R.color.text_colour_light, R.color.background_this_month_light),
        of(LIGHT, true, THURSDAY, R.color.text_colour_light, R.color.background_this_month_light),
        of(LIGHT, true, FRIDAY, R.color.text_colour_light, R.color.background_this_month_light),
        of(LIGHT, true, SATURDAY, R.color.text_colour_light, R.color.background_saturday_this_month_light),
        of(LIGHT, true, SUNDAY, R.color.text_colour_light, R.color.background_sunday_this_month_light),
        of(LIGHT, false, MONDAY, R.color.text_colour_semi_light, null),
        of(LIGHT, false, TUESDAY, R.color.text_colour_semi_light, null),
        of(LIGHT, false, WEDNESDAY, R.color.text_colour_semi_light, null),
        of(LIGHT, false, THURSDAY, R.color.text_colour_semi_light, null),
        of(LIGHT, false, FRIDAY, R.color.text_colour_semi_light, null),
        of(LIGHT, false, SATURDAY, R.color.text_colour_semi_light, R.color.background_saturday_light),
        of(LIGHT, false, SUNDAY, R.color.text_colour_semi_light, R.color.background_sunday_light)
    )

    private fun getThemeWithExpectedCellWeekNumberTextColour() = listOf(
        of(DARK, R.color.text_colour_semi_dark),
        of(LIGHT, R.color.text_colour_semi_light)
    )
}