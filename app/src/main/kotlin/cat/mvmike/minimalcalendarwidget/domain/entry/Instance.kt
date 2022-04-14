// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.entry

import android.content.Context
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.config.ClockConfig
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

data class Instance(
    val eventId: Int,
    val start: Instant,
    val end: Instant,
    val zoneId: ZoneId,
    val isDeclined: Boolean
) {
    // take out 5 milliseconds to avoid erratic behaviour events that end at midnight
    fun isInDay(day: LocalDate): Boolean {
        val instanceStartLocalDate = LocalDateTime.ofInstant(start, zoneId).toLocalDate()
        val instanceEndLocalDate = LocalDateTime.ofInstant(end.minusMillis(5), zoneId).toLocalDate()
        return !instanceStartLocalDate.isAfter(day)
                && !instanceEndLocalDate.isBefore(day)
    }
}

fun getInstances(context: Context, from: LocalDate, to: LocalDate): Set<Instance> {
    return when (SystemResolver.isReadCalendarPermitted(context)) {
        false -> HashSet()
        true -> {
            val systemZoneId = ClockConfig.getSystemZoneId()
            SystemResolver.getInstances(
                context = context,
                begin = from.toStartOfDayInEpochMilli(systemZoneId),
                end = to.toStartOfDayInEpochMilli(systemZoneId)
            )
        }
    }
}

private fun LocalDate.toStartOfDayInEpochMilli(zoneId: ZoneId) =
    this.atStartOfDay(zoneId).toInstant().toEpochMilli()
