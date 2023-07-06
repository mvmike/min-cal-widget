// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Calendar
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
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
import java.time.Month
import java.util.stream.Stream

internal class MonthAndYearHeaderServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getInstancesWithTextSizeAndThemeAndCalendarWithExpectedMonthAndYear")
    fun draw_shouldAddMonthAndYearWithColourAndRelativeMonthAndYearSize(
        instant: Instant,
        textSize: TextSize,
        widgetTheme: Theme,
        calendar: Calendar,
        expectedMonth: String,
        expectedYear: String
    ) {
        val expectedHeaderRelativeYearSize = 0.6f
        mockGetSystemInstant(instant)
        mockGetSystemZoneId()
        mockSharedPreferences()
        mockWidgetCalendar(calendar)
        val month = instant.atZone(zoneId).month
        every { context.getString(month.getExpectedResourceId()) } returns month.getExpectedAbbreviatedString()
        justRun {
            createMonthAndYearHeader(
                context = context,
                widgetRemoteView = widgetRv,
                month = expectedMonth,
                year = expectedYear,
                textColour = widgetTheme.mainTextColour,
                headerYearRelativeSize = expectedHeaderRelativeYearSize,
                textRelativeSize = textSize.relativeValue
            )
        }

        MonthAndYearHeaderService.draw(context, widgetRv, textSize, widgetTheme)

        verify { SystemResolver.getSystemInstant() }
        verify { SystemResolver.getSystemZoneId() }
        verify { context.getString(month.getExpectedResourceId()) }
        verifyWidgetCalendar()
        verify {
            createMonthAndYearHeader(
                context = context,
                widgetRemoteView = widgetRv,
                month = expectedMonth,
                year = expectedYear,
                textColour = widgetTheme.mainTextColour,
                headerYearRelativeSize = expectedHeaderRelativeYearSize,
                textRelativeSize = textSize.relativeValue
            )
        }
        confirmVerified(widgetRv)
    }

    private fun getInstancesWithTextSizeAndThemeAndCalendarWithExpectedMonthAndYear() = Stream.of(
        Arguments.of("2018-01-26".toInstant(), TextSize(40), Theme.DARK, Calendar.GREGORIAN, "January", "2018"),
        Arguments.of("2018-01-26".toInstant(), TextSize(15), Theme.DARK, Calendar.GREGORIAN, "Jan", "2018"),
        Arguments.of("2005-02-19".toInstant(), TextSize(40), Theme.DARK, Calendar.HOLOCENE, "February", "12005"),
        Arguments.of("2005-02-19".toInstant(), TextSize(15), Theme.DARK, Calendar.GREGORIAN, "Feb", "2005"),
        Arguments.of("2027-03-05".toInstant(), TextSize(40), Theme.DARK, Calendar.GREGORIAN, "March", "2027"),
        Arguments.of("2027-03-05".toInstant(), TextSize(15), Theme.DARK, Calendar.HOLOCENE, "Mar", "12027"),
        Arguments.of("2099-04-30".toInstant(), TextSize(40), Theme.DARK, Calendar.HOLOCENE, "April", "12099"),
        Arguments.of("2099-04-30".toInstant(), TextSize(15), Theme.DARK, Calendar.GREGORIAN, "Apr", "2099"),
        Arguments.of("2000-05-01".toInstant(), TextSize(40), Theme.DARK, Calendar.GREGORIAN, "May", "2000"),
        Arguments.of("2000-05-01".toInstant(), TextSize(15), Theme.DARK, Calendar.HOLOCENE, "May", "12000"),
        Arguments.of("1998-06-02".toInstant(), TextSize(40), Theme.DARK, Calendar.GREGORIAN, "June", "1998"),
        Arguments.of("1998-06-02".toInstant(), TextSize(15), Theme.DARK, Calendar.HOLOCENE, "Jun", "11998"),
        Arguments.of("1992-07-07".toInstant(), TextSize(40), Theme.LIGHT, Calendar.GREGORIAN, "July", "1992"),
        Arguments.of("1992-07-07".toInstant(), TextSize(15), Theme.LIGHT, Calendar.GREGORIAN, "Jul", "1992"),
        Arguments.of("2018-08-01".toInstant(), TextSize(40), Theme.LIGHT, Calendar.GREGORIAN, "August", "2018"),
        Arguments.of("2018-08-01".toInstant(), TextSize(15), Theme.LIGHT, Calendar.HOLOCENE, "Aug", "12018"),
        Arguments.of("1987-09-12".toInstant(), TextSize(40), Theme.LIGHT, Calendar.HOLOCENE, "September", "11987"),
        Arguments.of("1987-09-12".toInstant(), TextSize(15), Theme.LIGHT, Calendar.GREGORIAN, "Sep", "1987"),
        Arguments.of("2017-10-01".toInstant(), TextSize(40), Theme.LIGHT, Calendar.GREGORIAN, "October", "2017"),
        Arguments.of("2017-10-01".toInstant(), TextSize(15), Theme.LIGHT, Calendar.GREGORIAN, "Oct", "2017"),
        Arguments.of("1000-11-12".toInstant(), TextSize(40), Theme.LIGHT, Calendar.HOLOCENE, "November", "11000"),
        Arguments.of("1000-11-12".toInstant(), TextSize(15), Theme.LIGHT, Calendar.GREGORIAN, "Nov", "1000"),
        Arguments.of("1994-12-13".toInstant(), TextSize(40), Theme.LIGHT, Calendar.GREGORIAN, "December", "1994"),
        Arguments.of("1994-12-13".toInstant(), TextSize(15), Theme.LIGHT, Calendar.GREGORIAN, "Dec", "1994")
    )!!

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