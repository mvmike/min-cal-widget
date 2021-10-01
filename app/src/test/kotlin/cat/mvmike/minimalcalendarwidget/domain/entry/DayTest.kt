// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.entry

import cat.mvmike.minimalcalendarwidget.BaseTest
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class DayTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getLocalDatesWithTheirProperties")
    fun getDayOfWeek(dayProperties: DayTestProperties) {
        val day = Day(
            dayLocalDate = dayProperties.localDate
        )

        val result = day.getDayOfWeek()

        assertThat(result).isEqualTo(dayProperties.expectedDayOfWeek)
    }

    @ParameterizedTest
    @MethodSource("getLocalDatesWithTheirProperties")
    fun getDayOfMonthString(dayProperties: DayTestProperties) {
        val day = Day(
            dayLocalDate = dayProperties.localDate
        )

        val result = day.getDayOfMonthString()

        assertThat(result).isEqualTo(dayProperties.expectedDayOfMonthString)
    }

    @ParameterizedTest
    @MethodSource("getLocalDatesWithTheirProperties")
    fun isInMonth(dayProperties: DayTestProperties) {
        val day = Day(
            dayLocalDate = dayProperties.localDate
        )

        val result = day.isInMonth(systemLocalDate)

        assertThat(result).isEqualTo(dayProperties.expectedIsInMonth)
    }

    @ParameterizedTest
    @MethodSource("getLocalDatesWithTheirProperties")
    fun isToday(dayProperties: DayTestProperties) {
        val day = Day(
            dayLocalDate = dayProperties.localDate
        )

        val result = day.isToday(systemLocalDate)

        assertThat(result).isEqualTo(dayProperties.expectedIsToday)
    }

    @ParameterizedTest
    @MethodSource("getLocalDatesWithTheirProperties")
    fun isSingleDigitDay(dayProperties: DayTestProperties) {
        val day = Day(
            dayLocalDate = dayProperties.localDate
        )

        val result = day.isSingleDigitDay()

        assertThat(result).isEqualTo(dayProperties.expectedIsSingleDigitDay)
    }

    companion object {

        @JvmStatic
        @Suppress("unused", "LongMethod")
        fun getLocalDatesWithTheirProperties(): Stream<DayTestProperties> = Stream.of(
            DayTestProperties(
                localDate = LocalDate.of(2018, 1, 1),
                expectedDayOfMonthString = "01",
                expectedIsSingleDigitDay = true,
                expectedDayOfWeek = DayOfWeek.MONDAY,
                expectedIsInMonth = false,
                expectedIsToday = false
            ),
            DayTestProperties(
                localDate = LocalDate.of(2017, 12, 2),
                expectedDayOfMonthString = "02",
                expectedIsSingleDigitDay = true,
                expectedDayOfWeek = DayOfWeek.SATURDAY,
                expectedIsInMonth = false,
                expectedIsToday = false
            ),
            DayTestProperties(
                localDate = LocalDate.of(2018, 12, 4),
                expectedDayOfMonthString = "04",
                expectedIsSingleDigitDay = true,
                expectedDayOfWeek = DayOfWeek.TUESDAY,
                expectedIsInMonth = true,
                expectedIsToday = true
            ),
            DayTestProperties(
                localDate = LocalDate.of(2012, 7, 5),
                expectedDayOfMonthString = "05",
                expectedIsSingleDigitDay = true,
                expectedDayOfWeek = DayOfWeek.THURSDAY,
                expectedIsInMonth = false,
                expectedIsToday = false
            ),
            DayTestProperties(
                localDate = LocalDate.of(2018, 5, 5),
                expectedDayOfMonthString = "05",
                expectedIsSingleDigitDay = true,
                expectedDayOfWeek = DayOfWeek.SATURDAY,
                expectedIsInMonth = false,
                expectedIsToday = false
            ),
            DayTestProperties(
                localDate = LocalDate.of(2020, 12, 9),
                expectedDayOfMonthString = "09",
                expectedIsSingleDigitDay = true,
                expectedDayOfWeek = DayOfWeek.WEDNESDAY,
                expectedIsInMonth = false,
                expectedIsToday = false
            ),
            DayTestProperties(
                localDate = LocalDate.of(2021, 11, 11),
                expectedDayOfMonthString = "11",
                expectedIsSingleDigitDay = false,
                expectedDayOfWeek = DayOfWeek.THURSDAY,
                expectedIsInMonth = false,
                expectedIsToday = false
            ),
            DayTestProperties(
                localDate = LocalDate.of(2030, 2, 12),
                expectedDayOfMonthString = "12",
                expectedIsSingleDigitDay = false,
                expectedDayOfWeek = DayOfWeek.TUESDAY,
                expectedIsInMonth = false,
                expectedIsToday = false
            ),
            DayTestProperties(
                localDate = LocalDate.of(2015, 3, 15),
                expectedDayOfMonthString = "15",
                expectedIsSingleDigitDay = false,
                expectedDayOfWeek = DayOfWeek.SUNDAY,
                expectedIsInMonth = false,
                expectedIsToday = false
            ),
            DayTestProperties(
                localDate = LocalDate.of(2016, 6, 21),
                expectedDayOfMonthString = "21",
                expectedIsSingleDigitDay = false,
                expectedDayOfWeek = DayOfWeek.TUESDAY,
                expectedIsInMonth = false,
                expectedIsToday = false
            ),
            DayTestProperties(
                localDate = LocalDate.of(1994, 4, 23),
                expectedDayOfMonthString = "23",
                expectedIsSingleDigitDay = false,
                expectedDayOfWeek = DayOfWeek.SATURDAY,
                expectedIsInMonth = false,
                expectedIsToday = false
            ),
            DayTestProperties(
                localDate = LocalDate.of(2000, 8, 27),
                expectedDayOfMonthString = "27",
                expectedIsSingleDigitDay = false,
                expectedDayOfWeek = DayOfWeek.SUNDAY,
                expectedIsInMonth = false,
                expectedIsToday = false
            ),
            DayTestProperties(
                localDate = LocalDate.of(2018, 12, 28),
                expectedDayOfMonthString = "28",
                expectedIsSingleDigitDay = false,
                expectedDayOfWeek = DayOfWeek.FRIDAY,
                expectedIsInMonth = true,
                expectedIsToday = false
            ),
            DayTestProperties(
                localDate = LocalDate.of(2019, 12, 31),
                expectedDayOfMonthString = "31",
                expectedIsSingleDigitDay = false,
                expectedDayOfWeek = DayOfWeek.TUESDAY,
                expectedIsInMonth = false,
                expectedIsToday = false
            )
        )
    }

    internal data class DayTestProperties(
        val localDate: LocalDate,
        val expectedDayOfMonthString: String,
        val expectedIsSingleDigitDay: Boolean,
        val expectedDayOfWeek: DayOfWeek,
        val expectedIsInMonth: Boolean,
        val expectedIsToday: Boolean
    )
}
