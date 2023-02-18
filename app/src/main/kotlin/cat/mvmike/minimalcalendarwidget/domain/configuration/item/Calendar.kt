// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.content.Context
import cat.mvmike.minimalcalendarwidget.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val YEAR_FORMAT = "yyyy"

enum class Calendar(
    val displayString: Int
) {

    GREGORIAN(
        displayString = R.string.gregorian
    ),

    HOLOCENE(
        displayString = R.string.holocene
    ) {
        override fun getYear(instant: Instant, zoneId: ZoneId) =
            "1${super.getYear(instant, zoneId)}"
    };

    open fun getYear(instant: Instant, zoneId: ZoneId): String = DateTimeFormatter
        .ofPattern(YEAR_FORMAT)
        .withLocale(Locale.ENGLISH)
        .withZone(zoneId)
        .format(instant)
}

fun getAvailableCalendars() =
    Calendar.values()

fun Calendar.getDisplayValue(context: Context) =
    context.getString(this.displayString).replaceFirstChar { it.uppercase() }

fun getCalendarDisplayValues(context: Context) =
    getAvailableCalendars()
        .map { it.getDisplayValue(context) }
        .toTypedArray()
