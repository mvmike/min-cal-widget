// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual.draw

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.Format
import cat.mvmike.minimalcalendarwidget.infrastructure.config.ClockConfig
import cat.mvmike.minimalcalendarwidget.infrastructure.config.LocaleConfig
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import java.time.Instant
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DrawMonthAndYearHeaderUseCase {

    private const val YEAR_FORMAT = "yyyy"

    private const val HEADER_RELATIVE_YEAR_SIZE = 0.7f

    fun execute(context: Context, widgetRemoteView: RemoteViews, format: Format) {
        val systemInstant = ClockConfig.getInstant()
        val systemZoneId = ClockConfig.getSystemZoneId()
        val locale = LocaleConfig.getLocale(context)
        val displayMonth = format.getMonthHeaderLabel(systemInstant.toMonthDisplayValue(systemZoneId, context))
        val displayYear = systemInstant.toYearDisplayValue(locale, systemZoneId)

        GraphicResolver.createMonthAndYearHeader(
            widgetRemoteView = widgetRemoteView,
            monthAndYear = "$displayMonth $displayYear",
            headerRelativeYearSize = HEADER_RELATIVE_YEAR_SIZE
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

    private fun Instant.toYearDisplayValue(locale: Locale, zoneId: ZoneId) =
        DateTimeFormatter
            .ofPattern(YEAR_FORMAT)
            .withLocale(locale)
            .withZone(zoneId)
            .format(this)
}
