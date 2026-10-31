// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.LocalDate

internal class DayTest : BaseTest() {

    @ParameterizedTest
    @CsvSource(
        "2018-01-01,MONDAY",
        "2017-12-02,SATURDAY",
        "2018-12-04,TUESDAY",
        "2012-07-05,THURSDAY",
        "2018-05-05,SATURDAY",
        "2020-12-09,WEDNESDAY",
        "2021-11-11,THURSDAY",
        "2030-02-12,TUESDAY",
        "2015-03-15,SUNDAY",
        "2016-06-21,TUESDAY",
        "1994-04-23,SATURDAY",
        "2000-08-27,SUNDAY",
        "2018-12-28,FRIDAY",
        "2019-12-31,TUESDAY"
    )
    fun getDayOfWeek(dayLocalDate: LocalDate, expectedDayOfWeek: DayOfWeek) {
        val day = Day(dayLocalDate)

        val result = day.getDayOfWeek()

        assertThat(result).isEqualTo(expectedDayOfWeek)
    }

    @ParameterizedTest
    @CsvSource(
        "2018-01-01,1",
        "2017-12-02,2",
        "2018-12-04,4",
        "2012-07-05,5",
        "2018-05-05,5",
        "2020-12-09,9",
        "2021-11-11,11",
        "2030-02-12,12",
        "2015-03-15,15",
        "2016-06-21,21",
        "1994-04-23,23",
        "2000-08-27,27",
        "2018-12-28,28",
        "2019-12-31,31"
    )
    fun getDayOfMonthString(dayLocalDate: LocalDate, expectedDayOfMonthString: String) {
        val day = Day(dayLocalDate)

        val result = day.getDayOfMonthString()

        assertThat(result).isEqualTo(expectedDayOfMonthString)
    }

    @ParameterizedTest
    @CsvSource(
        "2017-12-02,false",
        "2018-11-30,false",
        "2018-12-01,true",
        "2018-12-04,true",
        "2018-12-31,true",
        "2019-01-01,false",
        "2030-02-12,false"
    )
    fun isInMonth(dayLocalDate: LocalDate, expectedIsInMonth: Boolean) {
        val day = Day(dayLocalDate)

        val result = day.isInMonth(systemLocalDate)

        assertThat(result).isEqualTo(expectedIsInMonth)
    }

    @ParameterizedTest
    @CsvSource(
        "2018-01-01,false",
        "2017-12-02,false",
        "2018-12-04,true",
        "2012-07-05,false",
        "2018-05-05,false"
    )
    fun isToday(dayLocalDate: LocalDate, expectedIsToday: Boolean) {
        val day = Day(dayLocalDate)

        val result = day.isToday(systemLocalDate)

        assertThat(result).isEqualTo(expectedIsToday)
    }

    @ParameterizedTest
    @CsvSource(
        "2018-01-01,false",
        "2017-12-02,true",
        "2018-12-04,false",
        "2012-07-05,false",
        "2018-05-05,true",
        "2020-12-09,false",
        "2021-11-11,false",
        "2030-02-12,false",
        "2015-03-15,true",
        "2016-06-21,false",
        "1994-04-23,true",
        "2000-08-27,true",
        "2018-12-28,false",
        "2019-12-31,false"
    )
    fun isWeekend(dayLocalDate: LocalDate, expectedIsWeekend: Boolean) {
        val day = Day(dayLocalDate)

        val result = day.isWeekend()

        assertThat(result).isEqualTo(expectedIsWeekend)
    }

    @ParameterizedTest
    @MethodSource("getDaysOfWeekWithExpectedStringResources")
    fun getDisplayValues_shouldReturnCapitalisedTranslationAndUpperCasedAbbreviation(
        dayOfWeek: DayOfWeek,
        stringResourceId: Int,
        abbreviatedStringResourceId: Int
    ) {
        every { context.getString(stringResourceId) } returns "some day"
        every { context.getString(abbreviatedStringResourceId) } returns "sdy"

        val displayValue = dayOfWeek.getDisplayValue(context)
        val abbreviatedDisplayValue = dayOfWeek.getAbbreviatedDisplayValue(context)

        assertThat(displayValue).isEqualTo("Some day")
        assertThat(abbreviatedDisplayValue).isEqualTo("SDY")
        verify { context.getString(stringResourceId) }
        verify { context.getString(abbreviatedStringResourceId) }
    }

    @Test
    fun getDayOfWeekDisplayValues_shouldReturnCapitalisedTranslationOfEveryDayOfWeekStartingOnMonday() {
        val translations = mapOf(
            R.string.monday to "monday",
            R.string.tuesday to "tuesday",
            R.string.wednesday to "wednesday",
            R.string.thursday to "thursday",
            R.string.friday to "friday",
            R.string.saturday to "saturday",
            R.string.sunday to "sunday"
        )
        translations.forEach { (stringResourceId, translation) ->
            every { context.getString(stringResourceId) } returns translation
        }

        val result = getDayOfWeekDisplayValues(context)

        assertThat(result).containsExactly("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        translations.keys.forEach { stringResourceId ->
            verify { context.getString(stringResourceId) }
        }
    }

    private fun getDaysOfWeekWithExpectedStringResources() = listOf(
        of(MONDAY, R.string.monday, R.string.monday_abb),
        of(TUESDAY, R.string.tuesday, R.string.tuesday_abb),
        of(WEDNESDAY, R.string.wednesday, R.string.wednesday_abb),
        of(THURSDAY, R.string.thursday, R.string.thursday_abb),
        of(FRIDAY, R.string.friday, R.string.friday_abb),
        of(SATURDAY, R.string.saturday, R.string.saturday_abb),
        of(SUNDAY, R.string.sunday, R.string.sunday_abb)
    )
}