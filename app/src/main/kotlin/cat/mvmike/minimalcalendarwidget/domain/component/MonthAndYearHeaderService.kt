// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Format
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import java.time.Instant
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object MonthAndYearHeaderService {

    private const val YEAR_FORMAT = "yyyy"

    private const val HEADER_RELATIVE_YEAR_SIZE = 0.7f

    fun draw(context: Context, widgetRemoteView: RemoteViews, format: Format) {
        val systemInstant = SystemResolver.getSystemInstant()
        val systemZoneId = SystemResolver.getSystemZoneId()
        val displayMonth = format.getMonthHeaderLabel(systemInstant.toMonthDisplayValue(systemZoneId, context))
        val displayYear = systemInstant.toYearDisplayValue(systemZoneId)
        val widgetTheme = EnumConfigurationItem.WidgetTheme.get(context)

        GraphicResolver.createMonthAndYearHeader(
            context = context,
            widgetRemoteView = widgetRemoteView,
            text = "$displayMonth $displayYear",
            textColour = widgetTheme.mainTextColour,
            headerRelativeYearSize = HEADER_RELATIVE_YEAR_SIZE,
            textRelativeSize = format.headerTextRelativeSize
        )
    }

    private fun Instant.toMonthDisplayValue(zoneId: ZoneId, context: Context) =
        when (this.atZone(zoneId).month!!) {
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

    private fun Instant.toYearDisplayValue(zoneId: ZoneId) =
        DateTimeFormatter
            .ofPattern(YEAR_FORMAT)
            .withLocale(Locale.ENGLISH)
            .withZone(zoneId)
            .format(this)
}
