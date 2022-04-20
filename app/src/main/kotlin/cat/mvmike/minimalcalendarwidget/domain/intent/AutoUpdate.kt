// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.MonthWidget
import cat.mvmike.minimalcalendarwidget.infrastructure.config.ClockConfig

object AutoUpdate {

    const val ACTION_AUTO_UPDATE = "cat.mvmike.minimalcalendarwidget.AUTO_UPDATE"

    private const val ALARM_ID = 859345

    private const val INTERVAL_MILLIS = 900000L // 1000*60*15 === 15'

    fun setAlarm(context: Context) {
        val currentMillis = ClockConfig.getInstant().toEpochMilli()
        val firstTriggerMillis = currentMillis + INTERVAL_MILLIS

        context.getAlarmManager().setRepeating(
            AlarmManager.RTC, // RTC does not wake the device up
            firstTriggerMillis,
            INTERVAL_MILLIS,
            PendingIntent.getBroadcast(
                context,
                ALARM_ID,
                Intent(context, MonthWidget::class.java).setAction(ACTION_AUTO_UPDATE),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    fun cancelAlarm(context: Context) = context.getAlarmManager().cancel(
        PendingIntent.getBroadcast(
            context,
            ALARM_ID,
            Intent(context, MonthWidget::class.java).setAction(ACTION_AUTO_UPDATE),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )

    private fun Context.getAlarmManager() = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}
