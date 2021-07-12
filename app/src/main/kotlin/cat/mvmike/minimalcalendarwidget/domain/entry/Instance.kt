// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.entry

import android.content.Context
import android.provider.CalendarContract
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

val FIELDS: Array<String> = arrayOf(
    CalendarContract.Instances.EVENT_ID,
    CalendarContract.Instances.BEGIN,
    CalendarContract.Instances.END,
    CalendarContract.Instances.EVENT_TIMEZONE
)

data class Instance(
    val eventId: Int,
    val start: Instant,
    val end: Instant,
    val zoneId: ZoneId
) {
    // take out 5 milliseconds to avoid erratic behaviour events that end at midnight
    fun isInDay(day: LocalDate): Boolean {
        val localDateTimeStart = LocalDateTime.ofInstant(start, zoneId)
        val localDateTimeEnd = LocalDateTime.ofInstant(end.minusMillis(5), zoneId)
        return localDateTimeStart.monthValue <= day.monthValue
                && localDateTimeStart.dayOfMonth <= day.dayOfMonth
                && localDateTimeEnd.monthValue >= day.monthValue
                && localDateTimeEnd.dayOfMonth >= day.dayOfMonth
    }
}

fun getInstances(context: Context, from: LocalDate, to: LocalDate): Set<Instance> {
    return when (SystemResolver.isReadCalendarPermitted(context)) {
        false -> HashSet()
        true -> {
            val systemZoneId = SystemResolver.getSystemZoneId()
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
