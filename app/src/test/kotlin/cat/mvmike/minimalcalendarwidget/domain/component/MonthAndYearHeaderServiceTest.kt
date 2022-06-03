// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.darkThemeMainTextColour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.lightThemeMainTextColour
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

internal class MonthAndYearHeaderServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getSpreadInstantsAndFormatsWithExpectedTextAndColour")
    fun execute(
        instant: Instant,
        format: Format,
        theme: Theme,
        expectedText: String,
        expectedTextColour: Int,
    ) {
        val expectedHeaderRelativeYearSize = 0.7f
        mockGetSystemInstant(instant)
        mockGetSystemZoneId()
        mockGetSystemLocale()
        mockSharedPreferences()
        mockWidgetTheme(theme)
        val month = instant.atZone(zoneId).month
        every { context.getString(month.getExpectedResourceId()) } returns month.getExpectedAbbreviatedString()
        justRun { GraphicResolver.createMonthAndYearHeader(context, widgetRv, expectedText, expectedTextColour, expectedHeaderRelativeYearSize) }

        MonthAndYearHeaderService.draw(context, widgetRv, format)

        verify { LocaleConfig.getLocale(context) }
        verify { ClockConfig.getInstant() }
        verify { ClockConfig.getSystemZoneId() }
        verify { context.getString(month.getExpectedResourceId()) }
        verifyWidgetTheme()
        verify { GraphicResolver.createMonthAndYearHeader(context, widgetRv, expectedText, expectedTextColour, expectedHeaderRelativeYearSize) }
        confirmVerified(widgetRv)
    }

    @Suppress("unused")
    private fun getSpreadInstantsAndFormatsWithExpectedTextAndColour() = Stream.of(
        Arguments.of("2018-01-26".toInstant(), Format.STANDARD, Theme.DARK, "January 2018", darkThemeMainTextColour),
        Arguments.of("2018-01-26".toInstant(), Format.REDUCED, Theme.DARK, "Jan 2018", darkThemeMainTextColour),
        Arguments.of("2005-02-19".toInstant(), Format.STANDARD, Theme.DARK, "February 2005", darkThemeMainTextColour),
        Arguments.of("2005-02-19".toInstant(), Format.REDUCED, Theme.DARK, "Feb 2005", darkThemeMainTextColour),
        Arguments.of("2027-03-05".toInstant(), Format.STANDARD, Theme.DARK, "March 2027", darkThemeMainTextColour),
        Arguments.of("2027-03-05".toInstant(), Format.REDUCED, Theme.DARK, "Mar 2027", darkThemeMainTextColour),
        Arguments.of("2099-04-30".toInstant(), Format.STANDARD, Theme.DARK, "April 2099", darkThemeMainTextColour),
        Arguments.of("2099-04-30".toInstant(), Format.REDUCED, Theme.DARK, "Apr 2099", darkThemeMainTextColour),
        Arguments.of("2000-05-01".toInstant(), Format.STANDARD, Theme.DARK, "May 2000", darkThemeMainTextColour),
        Arguments.of("2000-05-01".toInstant(), Format.REDUCED, Theme.DARK, "May 2000", darkThemeMainTextColour),
        Arguments.of("1998-06-02".toInstant(), Format.STANDARD, Theme.DARK, "June 1998", darkThemeMainTextColour),
        Arguments.of("1998-06-02".toInstant(), Format.REDUCED, Theme.DARK, "Jun 1998", darkThemeMainTextColour),
        Arguments.of("1992-07-07".toInstant(), Format.STANDARD, Theme.LIGHT, "July 1992", lightThemeMainTextColour),
        Arguments.of("1992-07-07".toInstant(), Format.REDUCED, Theme.LIGHT, "Jul 1992", lightThemeMainTextColour),
        Arguments.of("2018-08-01".toInstant(), Format.STANDARD, Theme.LIGHT, "August 2018", lightThemeMainTextColour),
        Arguments.of("2018-08-01".toInstant(), Format.REDUCED, Theme.LIGHT, "Aug 2018", lightThemeMainTextColour),
        Arguments.of("1987-09-12".toInstant(), Format.STANDARD, Theme.LIGHT, "September 1987", lightThemeMainTextColour),
        Arguments.of("1987-09-12".toInstant(), Format.REDUCED, Theme.LIGHT, "Sep 1987", lightThemeMainTextColour),
        Arguments.of("2017-10-01".toInstant(), Format.STANDARD, Theme.LIGHT, "October 2017", lightThemeMainTextColour),
        Arguments.of("2017-10-01".toInstant(), Format.REDUCED, Theme.LIGHT, "Oct 2017", lightThemeMainTextColour),
        Arguments.of("1000-11-12".toInstant(), Format.STANDARD, Theme.LIGHT, "November 1000", lightThemeMainTextColour),
        Arguments.of("1000-11-12".toInstant(), Format.REDUCED, Theme.LIGHT, "Nov 1000", lightThemeMainTextColour),
        Arguments.of("1994-12-13".toInstant(), Format.STANDARD, Theme.LIGHT, "December 1994", lightThemeMainTextColour),
        Arguments.of("1994-12-13".toInstant(), Format.REDUCED, Theme.LIGHT, "Dec 1994", lightThemeMainTextColour)
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
