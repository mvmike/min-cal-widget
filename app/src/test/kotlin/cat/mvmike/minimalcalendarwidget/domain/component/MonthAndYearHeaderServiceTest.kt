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
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate
import java.time.Month

internal class MonthAndYearHeaderServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @CsvSource(
        "2018-01-26,40,DARK,GREGORIAN,January,2018",
        "2018-01-26,15,DARK,GREGORIAN,Jan,2018",
        "2005-02-19,40,DARK,HOLOCENE,February,12005",
        "2005-02-19,15,DARK,GREGORIAN,Feb,2005",
        "2027-03-05,40,DARK,GREGORIAN,March,2027",
        "2027-03-05,15,DARK,HOLOCENE,Mar,12027",
        "2099-04-30,40,DARK,HOLOCENE,April,12099",
        "2099-04-30,15,DARK,GREGORIAN,Apr,2099",
        "2000-05-01,40,DARK,GREGORIAN,May,2000",
        "2000-05-01,15,DARK,HOLOCENE,May,12000",
        "1998-06-02,40,DARK,GREGORIAN,June,1998",
        "1998-06-02,15,DARK,HOLOCENE,Jun,11998",
        "1992-07-07,40,LIGHT,GREGORIAN,July,1992",
        "1992-07-07,15,LIGHT,GREGORIAN,Jul,1992",
        "2018-08-01,40,LIGHT,GREGORIAN,August,2018",
        "2018-08-01,15,LIGHT,HOLOCENE,Aug,12018",
        "1987-09-12,40,LIGHT,HOLOCENE,September,11987",
        "1987-09-12,15,LIGHT,GREGORIAN,Sep,1987",
        "2017-10-01,40,LIGHT,GREGORIAN,October,2017",
        "2017-10-01,15,LIGHT,GREGORIAN,Oct,2017",
        "1000-11-12,40,LIGHT,HOLOCENE,November,11000",
        "1000-11-12,15,LIGHT,GREGORIAN,Nov,1000",
        "1994-12-13,40,LIGHT,GREGORIAN,December,1994",
        "1994-12-13,15,LIGHT,GREGORIAN,Dec,1994"
    )
    fun draw_shouldAddMonthAndYearWithColourAndRelativeMonthAndYearSize(
        localDate: LocalDate,
        textSizePercentage: Int,
        widgetTheme: Theme,
        calendar: Calendar,
        expectedMonth: String,
        expectedYear: String
    ) {
        val expectedHeaderRelativeYearSize = 0.6f
        val textSize = TextSize(textSizePercentage)
        mockGetSystemLocalDate(localDate)
        mockWidgetCalendar(calendar)
        every {
            context.getString(localDate.month.getExpectedResourceIdAndTranslation().first)
        } returns localDate.month.getExpectedResourceIdAndTranslation().second
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
        verify { context.getString(localDate.month.getExpectedResourceIdAndTranslation().first) }
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

    private fun Month.getExpectedResourceIdAndTranslation() = when (this) {
        Month.JANUARY -> Pair(R.string.january, "January")
        Month.FEBRUARY -> Pair(R.string.february, "February")
        Month.MARCH -> Pair(R.string.march, "March")
        Month.APRIL -> Pair(R.string.april, "April")
        Month.MAY -> Pair(R.string.may, "May")
        Month.JUNE -> Pair(R.string.june, "June")
        Month.JULY -> Pair(R.string.july, "July")
        Month.AUGUST -> Pair(R.string.august, "August")
        Month.SEPTEMBER -> Pair(R.string.september, "September")
        Month.OCTOBER -> Pair(R.string.october, "October")
        Month.NOVEMBER -> Pair(R.string.november, "November")
        Month.DECEMBER -> Pair(R.string.december, "December")
    }
}