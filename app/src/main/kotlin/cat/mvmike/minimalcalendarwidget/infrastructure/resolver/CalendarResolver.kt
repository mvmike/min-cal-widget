package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import cat.mvmike.minimalcalendarwidget.domain.entry.Instance
import java.time.Instant
import java.time.ZoneId

object CalendarResolver {

    // CALENDAR CONTRACT

    private val instanceQueryFields = arrayOf(
        CalendarContract.Instances.EVENT_ID,
        CalendarContract.Instances.BEGIN,
        CalendarContract.Instances.END,
        CalendarContract.Instances.EVENT_TIMEZONE,
        CalendarContract.Instances.SELF_ATTENDEE_STATUS
    )

    fun getInstances(context: Context, begin: Long, end: Long): Set<Instance> {
        val instances: MutableSet<Instance> = HashSet()
        CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end).use { instanceCursor ->
            while (instanceCursor.moveToNext()) {
                instances.add(
                    Instance(
                        eventId = instanceCursor.getInt(0),
                        start = Instant.ofEpochMilli(instanceCursor.getLong(1)),
                        end = Instant.ofEpochMilli(instanceCursor.getLong(2)),
                        zoneId = ZoneId.of(instanceCursor.getString(3)),
                        isDeclined = instanceCursor.getInt(4) == CalendarContract.Instances.STATUS_CANCELED
                    )
                )
            }
        }
        return instances.toSet()
    }

    fun isReadCalendarPermitted(context: Context) =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
}