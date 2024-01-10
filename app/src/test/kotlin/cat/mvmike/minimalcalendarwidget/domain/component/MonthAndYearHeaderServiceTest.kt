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
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.LocalDate.parse
import java.time.Month

internal class MonthAndYearHeaderServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getInstancesWithTextSizeAndThemeAndCalendarWithExpectedMonthAndYear")
    fun draw_shouldAddMonthAndYearWithColourAndRelativeMonthAndYearSize(
        localDate: LocalDate,
        textSize: TextSize,
        widgetTheme: Theme,
        calendar: Calendar,
        expectedMonth: String,
        expectedYear: String
    ) {
        val expectedHeaderRelativeYearSize = 0.6f
        mockGetSystemLocalDate(localDate)
        mockWidgetCalendar(calendar)
        every {
            context.getString(localDate.month.getExpectedResourceId())
        } returns localDate.month.getExpectedAbbreviatedString()
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

        verifyGetSystemLocalDate()
        verify { context.getString(localDate.month.getExpectedResourceId()) }
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

    private fun getInstancesWithTextSizeAndThemeAndCalendarWithExpectedMonthAndYear() = listOf(
        of(parse("2018-01-26"), TextSize(40), Theme.DARK, Calendar.GREGORIAN, "January", "2018"),
        of(parse("2018-01-26"), TextSize(15), Theme.DARK, Calendar.GREGORIAN, "Jan", "2018"),
        of(parse("2005-02-19"), TextSize(40), Theme.DARK, Calendar.HOLOCENE, "February", "12005"),
        of(parse("2005-02-19"), TextSize(15), Theme.DARK, Calendar.GREGORIAN, "Feb", "2005"),
        of(parse("2027-03-05"), TextSize(40), Theme.DARK, Calendar.GREGORIAN, "March", "2027"),
        of(parse("2027-03-05"), TextSize(15), Theme.DARK, Calendar.HOLOCENE, "Mar", "12027"),
        of(parse("2099-04-30"), TextSize(40), Theme.DARK, Calendar.HOLOCENE, "April", "12099"),
        of(parse("2099-04-30"), TextSize(15), Theme.DARK, Calendar.GREGORIAN, "Apr", "2099"),
        of(parse("2000-05-01"), TextSize(40), Theme.DARK, Calendar.GREGORIAN, "May", "2000"),
        of(parse("2000-05-01"), TextSize(15), Theme.DARK, Calendar.HOLOCENE, "May", "12000"),
        of(parse("1998-06-02"), TextSize(40), Theme.DARK, Calendar.GREGORIAN, "June", "1998"),
        of(parse("1998-06-02"), TextSize(15), Theme.DARK, Calendar.HOLOCENE, "Jun", "11998"),
        of(parse("1992-07-07"), TextSize(40), Theme.LIGHT, Calendar.GREGORIAN, "July", "1992"),
        of(parse("1992-07-07"), TextSize(15), Theme.LIGHT, Calendar.GREGORIAN, "Jul", "1992"),
        of(parse("2018-08-01"), TextSize(40), Theme.LIGHT, Calendar.GREGORIAN, "August", "2018"),
        of(parse("2018-08-01"), TextSize(15), Theme.LIGHT, Calendar.HOLOCENE, "Aug", "12018"),
        of(parse("1987-09-12"), TextSize(40), Theme.LIGHT, Calendar.HOLOCENE, "September", "11987"),
        of(parse("1987-09-12"), TextSize(15), Theme.LIGHT, Calendar.GREGORIAN, "Sep", "1987"),
        of(parse("2017-10-01"), TextSize(40), Theme.LIGHT, Calendar.GREGORIAN, "October", "2017"),
        of(parse("2017-10-01"), TextSize(15), Theme.LIGHT, Calendar.GREGORIAN, "Oct", "2017"),
        of(parse("1000-11-12"), TextSize(40), Theme.LIGHT, Calendar.HOLOCENE, "November", "11000"),
        of(parse("1000-11-12"), TextSize(15), Theme.LIGHT, Calendar.GREGORIAN, "Nov", "1000"),
        of(parse("1994-12-13"), TextSize(40), Theme.LIGHT, Calendar.GREGORIAN, "December", "1994"),
        of(parse("1994-12-13"), TextSize(15), Theme.LIGHT, Calendar.GREGORIAN, "Dec", "1994")
    )

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