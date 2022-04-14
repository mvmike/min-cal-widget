// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.MonthWidget
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate
import java.time.Instant

object SystemResolver {

    // INTENT

    fun setOnClickPendingIntent(
        context: Context,
        widgetRemoteView: RemoteViews,
        viewId: Int,
        code: Int,
        action: String
    ) = widgetRemoteView.setOnClickPendingIntent(
        viewId,
        PendingIntent.getBroadcast(
            context,
            code,
            Intent(context, MonthWidget::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )

    fun setRepeatingAlarm(
        context: Context,
        alarmId: Int,
        firstTriggerMillis: Long,
        intervalMillis: Long
    ) = context.getAlarmManager().setRepeating(
        AlarmManager.RTC, // RTC does not wake the device up
        firstTriggerMillis,
        intervalMillis,
        PendingIntent.getBroadcast(
            context,
            alarmId,
            Intent(context, MonthWidget::class.java).setAction(AutoUpdate.ACTION_AUTO_UPDATE),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )

    fun cancelRepeatingAlarm(context: Context, alarmId: Int) = context.getAlarmManager().cancel(
        PendingIntent.getBroadcast(
            context,
            alarmId,
            Intent(context, MonthWidget::class.java).setAction(AutoUpdate.ACTION_AUTO_UPDATE),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )

    private fun Context.getAlarmManager() = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // ACTIVITY

    fun <E> startActivity(context: Context, clazz: Class<E>) = context.startActivity(
        Intent(context, clazz)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )


    fun startCalendarActivity(context: Context, startInstant: Instant) {
        val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
        ContentUris.appendId(builder, startInstant.toEpochMilli())

        context.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setData(builder.build())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
