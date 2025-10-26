// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.activity

import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import cat.mvmike.minimalcalendarwidget.R
import java.time.Instant

object CalendarActivity {

    fun start(
        context: Context,
        startTime: Instant
    ) = try {
        val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
        ContentUris.appendId(builder, startTime.toEpochMilli())

        context.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setData(builder.build())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, R.string.no_calendar_application, Toast.LENGTH_SHORT).show()
    }
}