// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.stream.Stream

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

    @Suppress("UnusedPrivateMember", "LongMethod")
    private fun getLocalDatesWithExpectations(): Stream<DayTestProperties> = Stream.of(
        DayTestProperties(
            localDate = LocalDate.of(2018, 1, 1),
            expectedDayOfMonthString = " 1",
            expectedDayOfWeek = DayOfWeek.MONDAY,
            expectedIsInMonth = false,
            expectedIsToday = false
        ),
        DayTestProperties(
            localDate = LocalDate.of(2017, 12, 2),
            expectedDayOfMonthString = " 2",
            expectedDayOfWeek = DayOfWeek.SATURDAY,
            expectedIsInMonth = false,
            expectedIsToday = false
        ),
        DayTestProperties(
            localDate = LocalDate.of(2018, 12, 4),
            expectedDayOfMonthString = " 4",
            expectedDayOfWeek = DayOfWeek.TUESDAY,
            expectedIsInMonth = true,
            expectedIsToday = true
        ),
        DayTestProperties(
            localDate = LocalDate.of(2012, 7, 5),
            expectedDayOfMonthString = " 5",
            expectedDayOfWeek = DayOfWeek.THURSDAY,
            expectedIsInMonth = false,
            expectedIsToday = false
        ),
        DayTestProperties(
            localDate = LocalDate.of(2018, 5, 5),
            expectedDayOfMonthString = " 5",
            expectedDayOfWeek = DayOfWeek.SATURDAY,
            expectedIsInMonth = false,
            expectedIsToday = false
        ),
        DayTestProperties(
            localDate = LocalDate.of(2020, 12, 9),
            expectedDayOfMonthString = " 9",
            expectedDayOfWeek = DayOfWeek.WEDNESDAY,
            expectedIsInMonth = false,
            expectedIsToday = false
        ),
        DayTestProperties(
            localDate = LocalDate.of(2021, 11, 11),
            expectedDayOfMonthString = "11",
            expectedDayOfWeek = DayOfWeek.THURSDAY,
            expectedIsInMonth = false,
            expectedIsToday = false
        ),
        DayTestProperties(
            localDate = LocalDate.of(2030, 2, 12),
            expectedDayOfMonthString = "12",
            expectedDayOfWeek = DayOfWeek.TUESDAY,
            expectedIsInMonth = false,
            expectedIsToday = false
        ),
        DayTestProperties(
            localDate = LocalDate.of(2015, 3, 15),
            expectedDayOfMonthString = "15",
            expectedDayOfWeek = DayOfWeek.SUNDAY,
            expectedIsInMonth = false,
            expectedIsToday = false
        ),
        DayTestProperties(
            localDate = LocalDate.of(2016, 6, 21),
            expectedDayOfMonthString = "21",
            expectedDayOfWeek = DayOfWeek.TUESDAY,
            expectedIsInMonth = false,
            expectedIsToday = false
        ),
        DayTestProperties(
            localDate = LocalDate.of(1994, 4, 23),
            expectedDayOfMonthString = "23",
            expectedDayOfWeek = DayOfWeek.SATURDAY,
            expectedIsInMonth = false,
            expectedIsToday = false
        ),
        DayTestProperties(
            localDate = LocalDate.of(2000, 8, 27),
            expectedDayOfMonthString = "27",
            expectedDayOfWeek = DayOfWeek.SUNDAY,
            expectedIsInMonth = false,
            expectedIsToday = false
        ),
        DayTestProperties(
            localDate = LocalDate.of(2018, 12, 28),
            expectedDayOfMonthString = "28",
            expectedDayOfWeek = DayOfWeek.FRIDAY,
            expectedIsInMonth = true,
            expectedIsToday = false
        ),
        DayTestProperties(
            localDate = LocalDate.of(2019, 12, 31),
            expectedDayOfMonthString = "31",
            expectedDayOfWeek = DayOfWeek.TUESDAY,
            expectedIsInMonth = false,
            expectedIsToday = false
        )
    )

    internal data class DayTestProperties(
        val localDate: LocalDate,
        val expectedDayOfMonthString: String,
        val expectedDayOfWeek: DayOfWeek,
        val expectedIsInMonth: Boolean,
        val expectedIsToday: Boolean
    )
}
