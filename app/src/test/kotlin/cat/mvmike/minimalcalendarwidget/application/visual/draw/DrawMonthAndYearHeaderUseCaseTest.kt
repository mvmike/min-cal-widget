// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual.draw

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.Format
import cat.mvmike.minimalcalendarwidget.infrastructure.config.ClockConfig
import cat.mvmike.minimalcalendarwidget.infrastructure.config.LocaleConfig
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
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

internal class DrawMonthAndYearHeaderUseCaseTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getSpreadInstantsAndFormatsWithExpectedMonthAndYearTranslation")
    fun execute(
        instant: Instant,
        format: Format,
        expectedMonthAndYear: String
    ) {
        val expectedHeaderRelativeYearSize = 0.7f
        mockGetSystemInstant(instant)
        mockGetSystemZoneId()
        mockGetSystemLocale()
        val month = instant.atZone(zoneId).month
        every { context.getString(month.getExpectedResourceId()) } returns month.getExpectedAbbreviatedString()
        justRun { GraphicResolver.createMonthAndYearHeader(widgetRv, expectedMonthAndYear, expectedHeaderRelativeYearSize) }

        DrawMonthAndYearHeaderUseCase.execute(context, widgetRv, format)

        verify { LocaleConfig.getLocale(context) }
        verify { ClockConfig.getInstant() }
        verify { ClockConfig.getSystemZoneId() }
        verify { context.getString(month.getExpectedResourceId()) }
        verify { GraphicResolver.createMonthAndYearHeader(widgetRv, expectedMonthAndYear, expectedHeaderRelativeYearSize) }
        confirmVerified(widgetRv)
    }

    @Suppress("unused")
    private fun getSpreadInstantsAndFormatsWithExpectedMonthAndYearTranslation() = Stream.of(
        Arguments.of("2018-01-26".toInstant(), Format.STANDARD, "January 2018"),
        Arguments.of("2018-01-26".toInstant(), Format.REDUCED, "Jan 2018"),
        Arguments.of("2005-02-19".toInstant(), Format.STANDARD, "February 2005"),
        Arguments.of("2005-02-19".toInstant(), Format.REDUCED, "Feb 2005"),
        Arguments.of("2027-03-05".toInstant(), Format.STANDARD, "March 2027"),
        Arguments.of("2027-03-05".toInstant(), Format.REDUCED, "Mar 2027"),
        Arguments.of("2099-04-30".toInstant(), Format.STANDARD, "April 2099"),
        Arguments.of("2099-04-30".toInstant(), Format.REDUCED, "Apr 2099"),
        Arguments.of("2000-05-01".toInstant(), Format.STANDARD, "May 2000"),
        Arguments.of("2000-05-01".toInstant(), Format.REDUCED, "May 2000"),
        Arguments.of("1998-06-02".toInstant(), Format.STANDARD, "June 1998"),
        Arguments.of("1998-06-02".toInstant(), Format.REDUCED, "Jun 1998"),
        Arguments.of("1992-07-07".toInstant(), Format.STANDARD, "July 1992"),
        Arguments.of("1992-07-07".toInstant(), Format.REDUCED, "Jul 1992"),
        Arguments.of("2018-08-01".toInstant(), Format.STANDARD, "August 2018"),
        Arguments.of("2018-08-01".toInstant(), Format.REDUCED, "Aug 2018"),
        Arguments.of("1987-09-12".toInstant(), Format.STANDARD, "September 1987"),
        Arguments.of("1987-09-12".toInstant(), Format.REDUCED, "Sep 1987"),
        Arguments.of("2017-10-01".toInstant(), Format.STANDARD, "October 2017"),
        Arguments.of("2017-10-01".toInstant(), Format.REDUCED, "Oct 2017"),
        Arguments.of("1000-11-12".toInstant(), Format.STANDARD, "November 1000"),
        Arguments.of("1000-11-12".toInstant(), Format.REDUCED, "Nov 1000"),
        Arguments.of("1994-12-13".toInstant(), Format.STANDARD, "December 1994"),
        Arguments.of("1994-12-13".toInstant(), Format.REDUCED, "Dec 1994")
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
