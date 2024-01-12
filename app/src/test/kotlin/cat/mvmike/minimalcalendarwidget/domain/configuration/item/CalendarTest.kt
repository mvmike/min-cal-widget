// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate

internal class CalendarTest : BaseTest() {

    @ParameterizedTest
    @CsvSource(
        "GREGORIAN,2018-01-26,2018",
        "HOLOCENE,2018-01-26,12018",
        "HOLOCENE,2005-02-19,12005",
        "GREGORIAN,2005-02-19,2005",
        "GREGORIAN,2027-03-05,2027",
        "HOLOCENE,2027-03-05,12027",
        "HOLOCENE,2099-04-30,12099",
        "GREGORIAN,2099-04-30,2099",
        "GREGORIAN,2000-05-01,2000",
        "HOLOCENE,2000-05-01,12000",
        "GREGORIAN,1998-06-02,1998",
        "HOLOCENE,1998-06-02,11998",
        "HOLOCENE,1992-07-07,11992",
        "GREGORIAN,1992-07-07,1992",
        "GREGORIAN,2018-08-01,2018",
        "HOLOCENE,2018-08-01,12018",
        "HOLOCENE,1987-09-12,11987",
        "GREGORIAN,1987-09-12,1987",
        "GREGORIAN,2017-10-01,2017",
        "HOLOCENE,2017-10-01,12017",
        "HOLOCENE,1000-11-12,11000",
        "GREGORIAN,1000-11-12,1000",
        "GREGORIAN,1994-12-13,1994",
        "GREGORIAN,1994-12-13,1994"
    )
    fun getYear_shouldReturnExpectedString(
        calendar: Calendar,
        localDate: LocalDate,
        expectedYear: String
    ) {
        assertThat(calendar.getYear(localDate)).isEqualTo(expectedYear)
    }
}