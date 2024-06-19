// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import android.content.Context
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

sealed class Instance(
    open val id: Int,
    open val eventId: Int,
    open val isDeclined: Boolean
) {
    abstract fun isInDay(
        day: LocalDate,
        dayZoneId: ZoneId
    ): Boolean

    data class TimedInstance(
        override val id: Int,
        override val eventId: Int,
        override val isDeclined: Boolean,
        val start: ZonedDateTime,
        val end: ZonedDateTime
    ) : Instance(
            id = id,
            eventId = eventId,
            isDeclined = isDeclined
        ) {
        override fun isInDay(day: LocalDate, dayZoneId: ZoneId): Boolean {
            val startOfDay = day.atStartOfDay(dayZoneId)
            val endOfDay = startOfDay
                .plus(1, ChronoUnit.DAYS)
                .minus(1, ChronoUnit.MILLIS)

            return !start.isAfter(endOfDay) && !end.isBefore(startOfDay)
        }
    }

    data class AllDayInstance(
        override val id: Int,
        override val eventId: Int,
        override val isDeclined: Boolean,
        val start: LocalDate,
        val end: LocalDate
    ) : Instance(
            id = id,
            eventId = eventId,
            isDeclined = isDeclined
        ) {
        override fun isInDay(day: LocalDate, dayZoneId: ZoneId): Boolean {
            return !start.isAfter(day) && !end.isBefore(day)
        }
    }
}

fun getInstances(
    context: Context,
    from: LocalDate,
    to: LocalDate
): Set<Instance> =
    when (CalendarResolver.isReadCalendarPermitted(context)) {
        true -> {
            val systemZoneId = SystemResolver.getSystemZoneId()
            CalendarResolver.getInstances(
                context = context,
                begin = from.atStartOfDayInMillis(systemZoneId),
                end = to.atStartOfDayInMillis(systemZoneId)
            )
        }
        else -> HashSet()
    }

private fun LocalDate.atStartOfDayInMillis(zoneId: ZoneId) =
    atStartOfDay(zoneId).toInstant().toEpochMilli()