// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver.createMonthAndYearHeader
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.stream.Stream

internal class MonthAndYearHeaderServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getSpreadInstantsAndFormatsWithExpectedTextAndColour")
    fun draw_shouldAddMonthAndYearWithColourAndRelativeMonthAndYearSize(
        instant: Instant,
        format: Format,
        theme: Theme,
        expectedText: String
    ) {
        val expectedHeaderRelativeYearSize = 0.7f
        mockGetSystemInstant(instant)
        mockGetSystemZoneId()
        mockSharedPreferences()
        mockWidgetTheme(theme)
        val month = instant.atZone(zoneId).month
        every { context.getString(month.getExpectedResourceId()) } returns month.getExpectedAbbreviatedString()
        justRun {
            createMonthAndYearHeader(context, widgetRv, expectedText, theme.mainTextColour, expectedHeaderRelativeYearSize, format.headerTextRelativeSize)
        }

        MonthAndYearHeaderService.draw(context, widgetRv, format)

        verify { SystemResolver.getSystemInstant() }
        verify { SystemResolver.getSystemZoneId() }
        verify { context.getString(month.getExpectedResourceId()) }
        verifyWidgetTheme()
        verify {
            createMonthAndYearHeader(context, widgetRv, expectedText, theme.mainTextColour, expectedHeaderRelativeYearSize, format.headerTextRelativeSize)
        }
        confirmVerified(widgetRv)
    }

    @Suppress("UnusedPrivateMember")
    private fun getSpreadInstantsAndFormatsWithExpectedTextAndColour() = Stream.of(
        Arguments.of("2018-01-26".toInstant(), Format(220), Theme.DARK, "January 2018"),
        Arguments.of("2018-01-26".toInstant(), Format(150), Theme.DARK, "Jan 2018"),
        Arguments.of("2005-02-19".toInstant(), Format(220), Theme.DARK, "February 2005"),
        Arguments.of("2005-02-19".toInstant(), Format(150), Theme.DARK, "Feb 2005"),
        Arguments.of("2027-03-05".toInstant(), Format(220), Theme.DARK, "March 2027"),
        Arguments.of("2027-03-05".toInstant(), Format(150), Theme.DARK, "Mar 2027"),
        Arguments.of("2099-04-30".toInstant(), Format(220), Theme.DARK, "April 2099"),
        Arguments.of("2099-04-30".toInstant(), Format(150), Theme.DARK, "Apr 2099"),
        Arguments.of("2000-05-01".toInstant(), Format(220), Theme.DARK, "May 2000"),
        Arguments.of("2000-05-01".toInstant(), Format(150), Theme.DARK, "May 2000"),
        Arguments.of("1998-06-02".toInstant(), Format(220), Theme.DARK, "June 1998"),
        Arguments.of("1998-06-02".toInstant(), Format(150), Theme.DARK, "Jun 1998"),
        Arguments.of("1992-07-07".toInstant(), Format(220), Theme.LIGHT, "July 1992"),
        Arguments.of("1992-07-07".toInstant(), Format(150), Theme.LIGHT, "Jul 1992"),
        Arguments.of("2018-08-01".toInstant(), Format(220), Theme.LIGHT, "August 2018"),
        Arguments.of("2018-08-01".toInstant(), Format(150), Theme.LIGHT, "Aug 2018"),
        Arguments.of("1987-09-12".toInstant(), Format(220), Theme.LIGHT, "September 1987"),
        Arguments.of("1987-09-12".toInstant(), Format(150), Theme.LIGHT, "Sep 1987"),
        Arguments.of("2017-10-01".toInstant(), Format(220), Theme.LIGHT, "October 2017"),
        Arguments.of("2017-10-01".toInstant(), Format(150), Theme.LIGHT, "Oct 2017"),
        Arguments.of("1000-11-12".toInstant(), Format(220), Theme.LIGHT, "November 1000"),
        Arguments.of("1000-11-12".toInstant(), Format(150), Theme.LIGHT, "Nov 1000"),
        Arguments.of("1994-12-13".toInstant(), Format(220), Theme.LIGHT, "December 1994"),
        Arguments.of("1994-12-13".toInstant(), Format(150), Theme.LIGHT, "Dec 1994")
    )!!

    private fun String.toInstant() = LocalDateTime
        .parse(this.plus("T00:00:00Z"), DateTimeFormatter.ISO_ZONED_DATE_TIME)
        .toInstant(ZoneOffset.UTC)

    private fun Month.getExpectedResourceId() =
        when (this) {
            Month.JANUARY -> R.string.january
            Month.FEBRUARY -> R.string.february
            Month.MARCH -> R.string.march
            Month.APRIL -> R.string.april
            Month.MAY -> R.string.may
            Month.JUNE -> R.string.june
            Month.JULY -> R.string.july
            Month.AUGUST -> R.string.august
            Month.SEPTEMBER -> R.string.september
            Month.OCTOBER -> R.string.october
            Month.NOVEMBER -> R.string.november
            Month.DECEMBER -> R.string.december
        }

    private fun Month.getExpectedAbbreviatedString() =
        when (this) {
            Month.JANUARY -> "January"
            Month.FEBRUARY -> "February"
            Month.MARCH -> "March"
            Month.APRIL -> "April"
            Month.MAY -> "May"
            Month.JUNE -> "June"
            Month.JULY -> "July"
            Month.AUGUST -> "August"
            Month.SEPTEMBER -> "September"
            Month.OCTOBER -> "October"
            Month.NOVEMBER -> "November"
            Month.DECEMBER -> "December"
        }
}
