// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.Variant
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Calendar
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver.createMonthAndYearHeader
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate
import java.time.Month

internal class MonthAndYearHeaderServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @CsvSource(
        "2018-01-26,MONTH,false,0,40,DARK,GREGORIAN,JANUARY,January,2018",
        "2018-01-26,WEEK,false,0,15,DARK,GREGORIAN,JANUARY,Jan,2018",
        "2005-02-19,MONTH,true,0,40,DARK,HOLOCENE,FEBRUARY,February,12005",
        "2005-02-19,WEEK,true,0,15,DARK,GREGORIAN,FEBRUARY,Feb,2005",
        "2027-03-05,MONTH,false,0,40,DARK,GREGORIAN,MARCH,March,2027",
        "2027-03-05,WEEK,true,1,15,DARK,HOLOCENE,MARCH,Mar,12027",
        "2099-04-30,MONTH,true,1,40,DARK,HOLOCENE,MAY,May,12099",
        "2099-04-30,WEEK,true,1,15,DARK,GREGORIAN,MAY,May,2099",
        "2000-05-01,MONTH,true,-1,40,DARK,GREGORIAN,APRIL,April,2000",
        "2000-05-01,WEEK,true,-1,15,DARK,HOLOCENE,APRIL,Apr,12000",
        "1998-06-02,MONTH,false,0,40,DARK,GREGORIAN,JUNE,June,1998",
        "1998-06-02,WEEK,false,0,15,DARK,HOLOCENE,JUNE,Jun,11998",
        "1992-07-07,MONTH,false,0,40,LIGHT,GREGORIAN,JULY,July,1992",
        "1992-07-07,WEEK,false,0,15,LIGHT,GREGORIAN,JULY,Jul,1992",
        "2018-08-01,MONTH,false,0,40,LIGHT,GREGORIAN,AUGUST,August,2018",
        "2018-08-01,WEEK,false,0,15,LIGHT,HOLOCENE,AUGUST,Aug,12018",
        "1987-09-12,MONTH,false,0,40,LIGHT,HOLOCENE,SEPTEMBER,September,11987",
        "1987-09-12,WEEK,false,0,15,LIGHT,GREGORIAN,SEPTEMBER,Sep,1987",
        "2017-10-01,MONTH,false,0,40,LIGHT,GREGORIAN,OCTOBER,October,2017",
        "2017-10-01,WEEK,false,0,15,LIGHT,GREGORIAN,OCTOBER,Oct,2017",
        "1000-11-12,MONTH,false,0,40,LIGHT,HOLOCENE,NOVEMBER,November,11000",
        "1000-11-12,WEEK,false,0,15,LIGHT,GREGORIAN,NOVEMBER,Nov,1000",
        "1994-12-13,MONTH,false,0,40,LIGHT,GREGORIAN,DECEMBER,December,1994",
        "1994-12-13,WEEK,true,3,15,LIGHT,GREGORIAN,JANUARY,Jan,1995"
    )
    fun draw_shouldAddMonthAndYearWithColourAndRelativeMonthAndYearSize(
        localDate: LocalDate,
        variant: Variant,
        allowPagination: Boolean,
        page: Int,
        textSizePercentage: Int,
        widgetTheme: Theme,
        calendar: Calendar,
        expectedMonthEnum: Month,
        expectedMonth: String,
        expectedYear: String
    ) {
        val expectedHeaderRelativeYearSize = 0.6f
        val textSize = TextSize(textSizePercentage)
        mockkObject(
            ActionableView.PreviousIcon,
            ActionableView.NextIcon
        )
        mockGetSystemLocalDate(localDate)
        mockAllowPagination(allowPagination)
        if (allowPagination) {
            mockPage(page)
            justRun { ActionableView.PreviousIcon.addListener(context, widgetRv, appWidgetId, variant) }
            justRun { ActionableView.NextIcon.addListener(context, widgetRv, appWidgetId, variant) }
        }
        mockWidgetCalendar(calendar)
        every {
            context.getString(expectedMonthEnum.getExpectedResourceIdAndTranslation().first)
        } returns expectedMonthEnum.getExpectedResourceIdAndTranslation().second
        justRun {
            createMonthAndYearHeader(
                context = context,
                widgetRemoteView = widgetRv,
                month = expectedMonth,
                year = expectedYear,
                textColour = widgetTheme.mainTextColour,
                headerYearRelativeSize = expectedHeaderRelativeYearSize,
                textRelativeSize = textSize.relativeValue,
                allowPagination = allowPagination
            )
        }

        MonthAndYearHeaderService.draw(context, widgetRv, appWidgetId, variant, textSize, widgetTheme)

        verifyGetSystemLocalDate()
        verifyAllowPagination()
        if (allowPagination) {
            verifyPage()
            verify { ActionableView.PreviousIcon.addListener(context, widgetRv, appWidgetId, variant) }
            verify { ActionableView.NextIcon.addListener(context, widgetRv, appWidgetId, variant) }
        }
        verify { context.getString(expectedMonthEnum.getExpectedResourceIdAndTranslation().first) }
        verifyWidgetCalendar()
        verify {
            createMonthAndYearHeader(
                context = context,
                widgetRemoteView = widgetRv,
                month = expectedMonth,
                year = expectedYear,
                textColour = widgetTheme.mainTextColour,
                headerYearRelativeSize = expectedHeaderRelativeYearSize,
                textRelativeSize = textSize.relativeValue,
                allowPagination = allowPagination
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