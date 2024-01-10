// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.CalendarContract
import androidx.core.content.ContextCompat.checkSelfPermission
import cat.mvmike.minimalcalendarwidget.domain.Instance
import cat.mvmike.minimalcalendarwidget.domain.Instance.AllDayInstance
import cat.mvmike.minimalcalendarwidget.domain.Instance.TimedInstance
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal val instanceQueryFields = arrayOf(
    CalendarContract.Instances.EVENT_ID,
    CalendarContract.Instances.BEGIN,
    CalendarContract.Instances.END,
    CalendarContract.Instances.EVENT_TIMEZONE,
    CalendarContract.Instances.SELF_ATTENDEE_STATUS,
    CalendarContract.Events.ALL_DAY
)

object CalendarResolver {

    fun getInstances(
        context: Context,
        begin: Long,
        end: Long
    ): Set<Instance> {
        val instances: MutableSet<Instance> = HashSet()
        var instanceCursor: Cursor? = null
        runCatching {
            instanceCursor = queryInstances(context, begin, end)
            while (instanceCursor!!.moveToNext()) {
                instanceCursor!!.toInstance()?.let { instances.add(it) }
            }
        }
        instanceCursor?.close()
        return instances.toSet()
    }

    fun isReadCalendarPermitted(context: Context) =
        checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED

    private fun queryInstances(
        context: Context,
        begin: Long,
        end: Long
    ): Cursor = CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end)

    private fun Cursor.toInstance(): Instance? = runCatching {
        val eventId = getInt(0)
        val start = Instant.ofEpochMilli(getLong(1))
        // end of instances are exclusive (e.g. an hour event is from 10 to 11 and not from 10 to 10:59:59.999)
        val end = Instant.ofEpochMilli(getLong(2) - 1)
        val zoneId = toZoneIdOrDefault(getString(3))
        val isDeclined = getInt(4) == CalendarContract.Instances.STATUS_CANCELED
        val isAllDay = getInt(5) == 1

        when {
            isAllDay -> AllDayInstance(
                eventId = eventId,
                isDeclined = isDeclined,
                start = LocalDateTime.ofInstant(start, zoneId).toLocalDate(),
                end = LocalDateTime.ofInstant(end, zoneId).toLocalDate()
            )
            else -> TimedInstance(
                eventId = eventId,
                isDeclined = isDeclined,
                start = ZonedDateTime.ofInstant(start, zoneId),
                end = ZonedDateTime.ofInstant(end, zoneId)
            )
        }
    }.getOrNull()

    private fun toZoneIdOrDefault(zoneId: String?): ZoneId = zoneId?.let {
        runCatching {
            ZoneId.of(it)
        }.getOrNull()
    } ?: ZoneId.systemDefault()
}