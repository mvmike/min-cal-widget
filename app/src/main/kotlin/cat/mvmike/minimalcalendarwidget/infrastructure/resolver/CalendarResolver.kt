// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import cat.mvmike.minimalcalendarwidget.domain.Instance
import java.time.Instant
import java.time.ZoneId

internal val instanceQueryFields = arrayOf(
    CalendarContract.Instances.EVENT_ID,
    CalendarContract.Instances.BEGIN,
    CalendarContract.Instances.END,
    CalendarContract.Instances.EVENT_TIMEZONE,
    CalendarContract.Instances.SELF_ATTENDEE_STATUS
)

object CalendarResolver {

    fun getInstances(context: Context, begin: Long, end: Long): Set<Instance> {
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
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED

    private fun queryInstances(context: Context, begin: Long, end: Long): Cursor =
        CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end)

    private fun Cursor.toInstance(): Instance? = runCatching {
        Instance(
            eventId = this.getInt(0),
            start = Instant.ofEpochMilli(this.getLong(1)),
            end = Instant.ofEpochMilli(this.getLong(2)),
            zoneId = toZoneIdOrDefault(this.getString(3)),
            isDeclined = this.getInt(4) == CalendarContract.Instances.STATUS_CANCELED
        )
    }.getOrNull()

    private fun toZoneIdOrDefault(zoneId: String?): ZoneId = zoneId?.let {
        runCatching {
            ZoneId.of(it)
        }.getOrNull()
    } ?: ZoneId.systemDefault()
}
