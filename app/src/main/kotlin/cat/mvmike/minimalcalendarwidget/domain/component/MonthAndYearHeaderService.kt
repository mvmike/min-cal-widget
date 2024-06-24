// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import java.time.LocalDate
import java.time.Month

object MonthAndYearHeaderService {

    private const val HEADER_RELATIVE_YEAR_SIZE = 0.6f

    fun draw(
        context: Context,
        widgetRemoteView: RemoteViews,
        textSize: TextSize,
        widgetTheme: Theme
    ) {
        val systemLocalDate = SystemResolver.getSystemLocalDate()
        val displayMonth = systemLocalDate
            .toMonthDisplayValue(context)
            .take(textSize.monthHeaderLabelLength)
        val displayYear = EnumConfigurationItem.WidgetCalendar
            .get(context)
            .getYear(systemLocalDate)

        GraphicResolver.createMonthAndYearHeader(
            context = context,
            widgetRemoteView = widgetRemoteView,
            month = displayMonth,
            year = displayYear,
            textColour = widgetTheme.mainTextColour,
            headerYearRelativeSize = HEADER_RELATIVE_YEAR_SIZE,
            textRelativeSize = textSize.relativeValue
        )
    }

    private fun LocalDate.toMonthDisplayValue(
        context: Context
    ) = when (month!!) {
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
    }.let { dayOfWeek ->
        context.getString(dayOfWeek).replaceFirstChar { it.uppercase() }
    }
}