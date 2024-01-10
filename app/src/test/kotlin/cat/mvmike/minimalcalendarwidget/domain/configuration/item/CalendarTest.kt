// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.LocalDate.parse

internal class CalendarTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getSpreadCalendarsAndDatesWithExpectedText")
    fun getYear_shouldReturnExpectedString(
        calendar: Calendar,
        localDate: LocalDate,
        expectedYear: String
    ) {
        assertThat(calendar.getYear(localDate)).isEqualTo(expectedYear)
    }

    private fun getSpreadCalendarsAndDatesWithExpectedText() = listOf(
        of(Calendar.GREGORIAN, parse("2018-01-26"), "2018"),
        of(Calendar.HOLOCENE, parse("2018-01-26"), "12018"),
        of(Calendar.HOLOCENE, parse("2005-02-19"), "12005"),
        of(Calendar.GREGORIAN, parse("2005-02-19"), "2005"),
        of(Calendar.GREGORIAN, parse("2027-03-05"), "2027"),
        of(Calendar.HOLOCENE, parse("2027-03-05"), "12027"),
        of(Calendar.HOLOCENE, parse("2099-04-30"), "12099"),
        of(Calendar.GREGORIAN, parse("2099-04-30"), "2099"),
        of(Calendar.GREGORIAN, parse("2000-05-01"), "2000"),
        of(Calendar.HOLOCENE, parse("2000-05-01"), "12000"),
        of(Calendar.GREGORIAN, parse("1998-06-02"), "1998"),
        of(Calendar.HOLOCENE, parse("1998-06-02"), "11998"),
        of(Calendar.HOLOCENE, parse("1992-07-07"), "11992"),
        of(Calendar.GREGORIAN, parse("1992-07-07"), "1992"),
        of(Calendar.GREGORIAN, parse("2018-08-01"), "2018"),
        of(Calendar.HOLOCENE, parse("2018-08-01"), "12018"),
        of(Calendar.HOLOCENE, parse("1987-09-12"), "11987"),
        of(Calendar.GREGORIAN, parse("1987-09-12"), "1987"),
        of(Calendar.GREGORIAN, parse("2017-10-01"), "2017"),
        of(Calendar.HOLOCENE, parse("2017-10-01"), "12017"),
        of(Calendar.HOLOCENE, parse("1000-11-12"), "11000"),
        of(Calendar.GREGORIAN, parse("1000-11-12"), "1000"),
        of(Calendar.GREGORIAN, parse("1994-12-13"), "1994"),
        of(Calendar.GREGORIAN, parse("1994-12-13"), "1994")
    )
}