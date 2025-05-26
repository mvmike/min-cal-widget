// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.Manifest.permission.READ_CALENDAR
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.provider.CalendarContract.Calendars
import android.provider.CalendarContract.Calendars.DEFAULT_SORT_ORDER
import android.provider.CalendarContract.Events
import android.provider.CalendarContract.Instances
import androidx.core.content.ContextCompat.checkSelfPermission
import cat.mvmike.minimalcalendarwidget.domain.Calendar
import cat.mvmike.minimalcalendarwidget.domain.Instance
import cat.mvmike.minimalcalendarwidget.domain.Instance.AllDayInstance
import cat.mvmike.minimalcalendarwidget.domain.Instance.TimedInstance
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal val instanceQueryFields = arrayOf(
    Instances._ID,
    Events.CALENDAR_ID,
    Instances.BEGIN,
    Instances.END,
    Instances.EVENT_TIMEZONE,
    Instances.SELF_ATTENDEE_STATUS,
    Events.ALL_DAY
)

internal val calendarQueryFields = arrayOf(
    Calendars._ID,
    Calendars.ACCOUNT_NAME,
    Calendars.CALENDAR_DISPLAY_NAME,
    Calendars.IS_PRIMARY,
    Calendars.VISIBLE
)

private const val CALENDAR_CONTRACT_TRUE = 1

private const val SORT_ORDER_DESC = "DESC"

object CalendarResolver {

    fun isReadCalendarPermitted(context: Context) =
        checkSelfPermission(context, READ_CALENDAR) == PERMISSION_GRANTED

    fun getInstances(
        context: Context,
        begin: Long,
        end: Long
    ): Set<Instance> {
        val instances: MutableSet<Instance> = HashSet()
        var instanceCursor: Cursor? = null
        runCatching {
            instanceCursor = Instances.query(context.contentResolver, instanceQueryFields, begin, end)
            while (instanceCursor?.moveToNext() == true) {
                instanceCursor?.toInstance()?.let { instances.add(it) }
            }
        }
        instanceCursor?.close()
        return instances.toSet()
    }

    fun getCalendars(
        context: Context
    ): List<Calendar> {
        val calendars: MutableList<Calendar> = mutableListOf()
        var calendarCursor: Cursor? = null
        runCatching {
            calendarCursor = context.contentResolver
                .query(Calendars.CONTENT_URI, calendarQueryFields, null, null, "$DEFAULT_SORT_ORDER $SORT_ORDER_DESC")
            while (calendarCursor?.moveToNext() == true) {
                calendarCursor?.toCalendar()?.let { calendars.add(it) }
            }
        }
        calendarCursor?.close()
        return calendars.toList()
    }

    private fun Cursor.toInstance(): Instance? = runCatching {
        val id = getInt(0)
        val calendarId = getInt(1)
        val start = Instant.ofEpochMilli(getLong(2))
        // end of instances are exclusive (e.g. an hour event is from 10 to 11 and not from 10 to 10:59:59.999)
        val end = Instant.ofEpochMilli(getLong(3) - 1)
        val zoneId = toZoneIdOrDefault(getString(4))
        val isDeclined = getInt(5) == Instances.STATUS_CANCELED
        val isAllDay = getInt(6) == CALENDAR_CONTRACT_TRUE

        when {
            isAllDay -> AllDayInstance(
                id = id,
                calendarId = calendarId,
                isDeclined = isDeclined,
                start = LocalDateTime.ofInstant(start, zoneId).toLocalDate(),
                end = LocalDateTime.ofInstant(end, zoneId).toLocalDate()
            )
            else -> TimedInstance(
                id = id,
                calendarId = calendarId,
                isDeclined = isDeclined,
                start = ZonedDateTime.ofInstant(start, zoneId),
                end = ZonedDateTime.ofInstant(end, zoneId)
            )
        }
    }.getOrNull()

    private fun Cursor.toCalendar(): Calendar? = runCatching {
        Calendar(
            id = getInt(0),
            accountName = getString(1),
            displayName = getString(2),
            isPrimary = getInt(3) == CALENDAR_CONTRACT_TRUE,
            isVisible = getInt(4) == CALENDAR_CONTRACT_TRUE
        )
    }.getOrNull()

    private fun toZoneIdOrDefault(zoneId: String?): ZoneId = zoneId?.let {
        runCatching {
            ZoneId.of(it)
        }.getOrNull()
    } ?: ZoneId.systemDefault()
}