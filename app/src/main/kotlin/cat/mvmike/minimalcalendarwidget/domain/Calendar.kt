// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import android.content.Context
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver

data class Calendar(
    val id: Int,
    val accountName: String,
    val displayName: String,
    val isPrimary: Boolean,
    val isVisible: Boolean
)

fun getCalendars(
    context: Context
): List<Calendar> = when (CalendarResolver.isReadCalendarPermitted(context)) {
    true -> {
        CalendarResolver.getCalendars(
            context = context
        )
    }
    else -> emptyList()
}