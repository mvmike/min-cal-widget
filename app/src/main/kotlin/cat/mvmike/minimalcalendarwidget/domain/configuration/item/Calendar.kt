// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.content.Context
import cat.mvmike.minimalcalendarwidget.R
import java.time.LocalDate

enum class Calendar(
    val displayString: Int
) {

    GREGORIAN(
        displayString = R.string.gregorian
    ) {
        override fun getYear(localDate: LocalDate) = "${localDate.year}"
    },

    HOLOCENE(
        displayString = R.string.holocene
    ) {
        override fun getYear(localDate: LocalDate) = "1${localDate.year}"
    };

    abstract fun getYear(localDate: LocalDate): String
}

fun Calendar.getDisplayValue(context: Context) =
    context.getString(displayString).replaceFirstChar { it.uppercase() }

fun getCalendarDisplayValues(context: Context) =
    Calendar.entries
        .map { it.getDisplayValue(context) }
        .toTypedArray()