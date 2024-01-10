// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDate.parse

internal class DayTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getLocalDatesWithExpectations")
    fun getDayOfWeek(dayProperties: DayTestProperties) {
        val day = Day(
            dayLocalDate = dayProperties.localDate
        )

        val result = day.getDayOfWeek()

        assertThat(result).isEqualTo(dayProperties.expectedDayOfWeek)
    }

    @ParameterizedTest
    @MethodSource("getLocalDatesWithExpectations")
    fun getDayOfMonthString(dayProperties: DayTestProperties) {
        val day = Day(
            dayLocalDate = dayProperties.localDate
        )

        val result = day.getDayOfMonthString()

        assertThat(result).isEqualTo(dayProperties.expectedDayOfMonthString)
    }

    @ParameterizedTest
    @MethodSource("getLocalDatesWithExpectations")
    fun isInMonth(dayProperties: DayTestProperties) {
        val day = Day(
            dayLocalDate = dayProperties.localDate
        )

        val result = day.isInMonth(systemLocalDate)

        assertThat(result).isEqualTo(dayProperties.expectedIsInMonth)
    }

    @ParameterizedTest
    @MethodSource("getLocalDatesWithExpectations")
    fun isToday(dayProperties: DayTestProperties) {
        val day = Day(
            dayLocalDate = dayProperties.localDate
        )

        val result = day.isToday(systemLocalDate)

        assertThat(result).isEqualTo(dayProperties.expectedIsToday)
    }

    @ParameterizedTest
    @MethodSource("getLocalDatesWithExpectations")
    fun isWeekend(dayProperties: DayTestProperties) {
        val day = Day(
            dayLocalDate = dayProperties.localDate
        )

        val result = day.isWeekend()

        assertThat(result).isEqualTo(dayProperties.expectedIsWeekend)
    }

    private fun getLocalDatesWithExpectations() = listOf(
        DayTestProperties(
            localDate = parse("2018-01-01"),
            expectedDayOfMonthString = "1",
            expectedDayOfWeek = DayOfWeek.MONDAY,
            expectedIsInMonth = false,
            expectedIsToday = false,
            expectedIsWeekend = false
        ),
        DayTestProperties(
            localDate = parse("2017-12-02"),
            expectedDayOfMonthString = "2",
            expectedDayOfWeek = DayOfWeek.SATURDAY,
            expectedIsInMonth = false,
            expectedIsToday = false,
            expectedIsWeekend = true
        ),
        DayTestProperties(
            localDate = parse("2018-12-04"),
            expectedDayOfMonthString = "4",
            expectedDayOfWeek = DayOfWeek.TUESDAY,
            expectedIsInMonth = true,
            expectedIsToday = true,
            expectedIsWeekend = false
        ),
        DayTestProperties(
            localDate = parse("2012-07-05"),
            expectedDayOfMonthString = "5",
            expectedDayOfWeek = DayOfWeek.THURSDAY,
            expectedIsInMonth = false,
            expectedIsToday = false,
            expectedIsWeekend = false
        ),
        DayTestProperties(
            localDate = parse("2018-05-05"),
            expectedDayOfMonthString = "5",
            expectedDayOfWeek = DayOfWeek.SATURDAY,
            expectedIsInMonth = false,
            expectedIsToday = false,
            expectedIsWeekend = true
        ),
        DayTestProperties(
            localDate = parse("2020-12-09"),
            expectedDayOfMonthString = "9",
            expectedDayOfWeek = DayOfWeek.WEDNESDAY,
            expectedIsInMonth = false,
            expectedIsToday = false,
            expectedIsWeekend = false
        ),
        DayTestProperties(
            localDate = parse("2021-11-11"),
            expectedDayOfMonthString = "11",
            expectedDayOfWeek = DayOfWeek.THURSDAY,
            expectedIsInMonth = false,
            expectedIsToday = false,
            expectedIsWeekend = false
        ),
        DayTestProperties(
            localDate = parse("2030-02-12"),
            expectedDayOfMonthString = "12",
            expectedDayOfWeek = DayOfWeek.TUESDAY,
            expectedIsInMonth = false,
            expectedIsToday = false,
            expectedIsWeekend = false
        ),
        DayTestProperties(
            localDate = parse("2015-03-15"),
            expectedDayOfMonthString = "15",
            expectedDayOfWeek = DayOfWeek.SUNDAY,
            expectedIsInMonth = false,
            expectedIsToday = false,
            expectedIsWeekend = true
        ),
        DayTestProperties(
            localDate = parse("2016-06-21"),
            expectedDayOfMonthString = "21",
            expectedDayOfWeek = DayOfWeek.TUESDAY,
            expectedIsInMonth = false,
            expectedIsToday = false,
            expectedIsWeekend = false
        ),
        DayTestProperties(
            localDate = parse("1994-04-23"),
            expectedDayOfMonthString = "23",
            expectedDayOfWeek = DayOfWeek.SATURDAY,
            expectedIsInMonth = false,
            expectedIsToday = false,
            expectedIsWeekend = true
        ),
        DayTestProperties(
            localDate = parse("2000-08-27"),
            expectedDayOfMonthString = "27",
            expectedDayOfWeek = DayOfWeek.SUNDAY,
            expectedIsInMonth = false,
            expectedIsToday = false,
            expectedIsWeekend = true
        ),
        DayTestProperties(
            localDate = parse("2018-12-28"),
            expectedDayOfMonthString = "28",
            expectedDayOfWeek = DayOfWeek.FRIDAY,
            expectedIsInMonth = true,
            expectedIsToday = false,
            expectedIsWeekend = false
        ),
        DayTestProperties(
            localDate = parse("2019-12-31"),
            expectedDayOfMonthString = "31",
            expectedDayOfWeek = DayOfWeek.TUESDAY,
            expectedIsInMonth = false,
            expectedIsToday = false,
            expectedIsWeekend = false
        )
    )

    internal data class DayTestProperties(
        val localDate: LocalDate,
        val expectedDayOfMonthString: String,
        val expectedDayOfWeek: DayOfWeek,
        val expectedIsInMonth: Boolean,
        val expectedIsToday: Boolean,
        val expectedIsWeekend: Boolean
    )
}