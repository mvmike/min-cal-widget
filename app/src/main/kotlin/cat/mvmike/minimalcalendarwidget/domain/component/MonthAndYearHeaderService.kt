// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.Variant
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.IntConfigurationItem.Page
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.NextIcon
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.PreviousIcon
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import java.time.LocalDate
import java.time.Month

object MonthAndYearHeaderService {

    private const val HEADER_RELATIVE_YEAR_SIZE = 0.6f

    fun draw(
        context: Context,
        widgetRemoteView: RemoteViews,
        appWidgetId: Int,
        variant: Variant,
        textSize: TextSize,
        widgetTheme: Theme
    ) {
        val systemLocalDate = SystemResolver.getSystemLocalDate()
        val allowPagination = BooleanConfigurationItem.AllowPagination.get(context, appWidgetId)
        val widgetLocalDate = when {
            allowPagination -> systemLocalDate.plus(Page.get(context, appWidgetId).toLong(), variant.paginationTemporalUnit)
            else -> systemLocalDate
        }
        val displayMonth = widgetLocalDate
            .toMonthDisplayValue(context)
            .take(textSize.monthHeaderLabelLength)
        val displayYear = EnumConfigurationItem.WidgetCalendar
            .get(context, appWidgetId)
            .getYear(widgetLocalDate)

        GraphicResolver.createMonthAndYearHeader(
            context = context,
            widgetRemoteView = widgetRemoteView,
            month = displayMonth,
            year = displayYear,
            textColour = widgetTheme.mainTextColour,
            headerYearRelativeSize = HEADER_RELATIVE_YEAR_SIZE,
            textRelativeSize = textSize.relativeValue,
            allowPagination = allowPagination
        )
        if (allowPagination) {
            PreviousIcon.addListener(context, widgetRemoteView, appWidgetId, variant)
            NextIcon.addListener(context, widgetRemoteView, appWidgetId, variant)
        }
    }

    private fun LocalDate.toMonthDisplayValue(
        context: Context
    ) = when (month) {
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
    }.let { month ->
        context.getString(month).replaceFirstChar { it.uppercase() }
    }
}