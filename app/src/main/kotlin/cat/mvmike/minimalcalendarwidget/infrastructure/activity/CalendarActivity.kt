// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.widget.Toast
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver

private const val NO_CALENDAR_APPLICATION_FOUND = "No calendar application found"

object CalendarActivity {

    fun start(context: Context) {
        val systemInstant = SystemResolver.getInstant()

        try {
            SystemResolver.startCalendarActivity(
                context = context,
                startInstant = systemInstant
            )
        } catch (ignored: ActivityNotFoundException) {
            Toast.makeText(context, NO_CALENDAR_APPLICATION_FOUND, Toast.LENGTH_SHORT).show()
        }
    }
}
