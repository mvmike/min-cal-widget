// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.widget.Toast
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.config.ClockConfig

object CalendarActivity {

    fun start(context: Context) {
        val systemInstant = ClockConfig.getInstant()

        try {
            SystemResolver.startCalendarActivity(
                context = context,
                startInstant = systemInstant
            )
        } catch (ignored: ActivityNotFoundException) {
            Toast.makeText(context, R.string.no_calendar_application, Toast.LENGTH_SHORT).show()
        }
    }
}
