// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.Widget
import cat.mvmike.minimalcalendarwidget.domain.Variant
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver

object AutoUpdate {

    internal const val ACTION_AUTO_UPDATE = "cat.mvmike.minimalcalendarwidget.AUTO_UPDATE"

    private const val ALARM_ID = 859345

    private const val INTERVAL_MILLIS = 600000L // 1000*60*10 === 10'

    fun set(context: Context, variant: Variant) {
        val currentMillis = SystemResolver.getSystemInstant().toEpochMilli()
        val firstTriggerMillis = currentMillis + INTERVAL_MILLIS

        context.getAlarmManager().setRepeating(
            AlarmManager.RTC,
            firstTriggerMillis,
            INTERVAL_MILLIS,
            PendingIntent.getBroadcast(
                context,
                ALARM_ID,
                Intent(context, Widget.getReceiverClass(variant)).setAction(ACTION_AUTO_UPDATE),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    fun cancel(context: Context, variant: Variant) = context.getAlarmManager().cancel(
        PendingIntent.getBroadcast(
            context,
            ALARM_ID,
            Intent(context, Widget.getReceiverClass(variant)).setAction(ACTION_AUTO_UPDATE),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )

    private fun Context.getAlarmManager() = getSystemService(Context.ALARM_SERVICE) as AlarmManager
}