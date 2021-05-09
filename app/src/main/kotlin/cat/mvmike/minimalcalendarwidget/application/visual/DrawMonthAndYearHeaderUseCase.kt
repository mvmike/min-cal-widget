// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object DrawMonthAndYearHeaderUseCase {

    private const val MONTH_FORMAT_NON_STANDALONE = "MMMM"
    private const val YEAR_FORMAT = "yyyy"

    private const val HEADER_RELATIVE_YEAR_SIZE = 0.7f

    fun execute(context: Context, widgetRemoteView: RemoteViews) {
        val locale = SystemResolver.get().getLocale(context)
        val systemInstant = SystemResolver.get().getInstant()
        val systemZoneId = SystemResolver.get().getSystemZoneId()
        val displayMonth = systemInstant.toMonthDisplayValue(locale, systemZoneId)
        val displayYear = systemInstant.toYearDisplayValue(locale, systemZoneId)
        SystemResolver.get().createMonthAndYearHeader(
            widgetRemoteView = widgetRemoteView,
            monthAndYear = "$displayMonth $displayYear",
            headerRelativeYearSize = HEADER_RELATIVE_YEAR_SIZE
        )
    }

    private fun Instant.toMonthDisplayValue(locale: Locale, zoneId: ZoneId) =
        DateTimeFormatter
            .ofPattern(MONTH_FORMAT_NON_STANDALONE)
            .withLocale(locale)
            .withZone(zoneId)
            .format(this)
            .stripArticle(locale)
            .replaceFirstChar { it.uppercase() }

    private fun Instant.toYearDisplayValue(locale: Locale, zoneId: ZoneId) =
        DateTimeFormatter
            .ofPattern(YEAR_FORMAT)
            .withLocale(locale)
            .withZone(zoneId)
            .format(this)

    private fun String.stripArticle(locale: Locale) = when (locale.language) {
        "ca" -> this.split(" ", "’").last()
        else -> this
    }
}
