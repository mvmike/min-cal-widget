// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Instant

internal class CalendarTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getSpreadCalendarsAndDatesWithExpectedText")
    fun getYear_shouldReturnExpectedString(
        calendar: Calendar,
        instant: Instant,
        expectedYear: String
    ) {
        assertThat(calendar.getYear(instant, zoneId)).isEqualTo(expectedYear)
    }

    private fun getSpreadCalendarsAndDatesWithExpectedText() = listOf(
        Arguments.of(Calendar.GREGORIAN, "2018-01-26".toInstant(), "2018"),
        Arguments.of(Calendar.HOLOCENE, "2018-01-26".toInstant(), "12018"),
        Arguments.of(Calendar.HOLOCENE, "2005-02-19".toInstant(), "12005"),
        Arguments.of(Calendar.GREGORIAN, "2005-02-19".toInstant(), "2005"),
        Arguments.of(Calendar.GREGORIAN, "2027-03-05".toInstant(), "2027"),
        Arguments.of(Calendar.HOLOCENE, "2027-03-05".toInstant(), "12027"),
        Arguments.of(Calendar.HOLOCENE, "2099-04-30".toInstant(), "12099"),
        Arguments.of(Calendar.GREGORIAN, "2099-04-30".toInstant(), "2099"),
        Arguments.of(Calendar.GREGORIAN, "2000-05-01".toInstant(), "2000"),
        Arguments.of(Calendar.HOLOCENE, "2000-05-01".toInstant(), "12000"),
        Arguments.of(Calendar.GREGORIAN, "1998-06-02".toInstant(), "1998"),
        Arguments.of(Calendar.HOLOCENE, "1998-06-02".toInstant(), "11998"),
        Arguments.of(Calendar.HOLOCENE, "1992-07-07".toInstant(), "11992"),
        Arguments.of(Calendar.GREGORIAN, "1992-07-07".toInstant(), "1992"),
        Arguments.of(Calendar.GREGORIAN, "2018-08-01".toInstant(), "2018"),
        Arguments.of(Calendar.HOLOCENE, "2018-08-01".toInstant(), "12018"),
        Arguments.of(Calendar.HOLOCENE, "1987-09-12".toInstant(), "11987"),
        Arguments.of(Calendar.GREGORIAN, "1987-09-12".toInstant(), "1987"),
        Arguments.of(Calendar.GREGORIAN, "2017-10-01".toInstant(), "2017"),
        Arguments.of(Calendar.HOLOCENE, "2017-10-01".toInstant(), "12017"),
        Arguments.of(Calendar.HOLOCENE, "1000-11-12".toInstant(), "11000"),
        Arguments.of(Calendar.GREGORIAN, "1000-11-12".toInstant(), "1000"),
        Arguments.of(Calendar.GREGORIAN, "1994-12-13".toInstant(), "1994"),
        Arguments.of(Calendar.GREGORIAN, "1994-12-13".toInstant(), "1994")
    )
}