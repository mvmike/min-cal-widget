// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.activity

import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver

private const val NO_CALENDAR_APPLICATION_FOUND = "No calendar application found"

private const val TIME_APPEND_PATH = "time"

object CalendarActivity {

    fun start(context: Context) {
        val startMillis = SystemResolver.get().getInstant().toEpochMilli()
        val builder = CalendarContract.CONTENT_URI.buildUpon()
            .appendPath(TIME_APPEND_PATH)
        ContentUris.appendId(builder, startMillis)

        val calendarIntent = Intent(Intent.ACTION_VIEW)
            .setData(builder.build())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(calendarIntent)
        } catch (ignored: ActivityNotFoundException) {
            Toast.makeText(context, NO_CALENDAR_APPLICATION_FOUND, Toast.LENGTH_SHORT).show()
        }
    }
}
