// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.MonthWidget
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver

object AutoUpdate {

    internal const val ACTION_AUTO_UPDATE = "cat.mvmike.minimalcalendarwidget.AUTO_UPDATE"

    private const val ALARM_ID = 859345

    fun set(context: Context, intervalInMillis: Long) {
        val currentMillis = SystemResolver.getSystemInstant().toEpochMilli()
        val firstTriggerMillis = currentMillis + intervalInMillis

        context.getAlarmManager().setRepeating(
            AlarmManager.RTC, // RTC does not wake the device up
            firstTriggerMillis,
            intervalInMillis,
            PendingIntent.getBroadcast(
                context,
                ALARM_ID,
                Intent(context, MonthWidget::class.java).setAction(ACTION_AUTO_UPDATE),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    fun cancel(context: Context) = context.getAlarmManager().cancel(
        PendingIntent.getBroadcast(
            context,
            ALARM_ID,
            Intent(context, MonthWidget::class.java).setAction(ACTION_AUTO_UPDATE),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )

    private fun Context.getAlarmManager() = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}
