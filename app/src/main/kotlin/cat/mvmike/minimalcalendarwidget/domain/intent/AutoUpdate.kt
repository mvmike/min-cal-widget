// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.content.Context
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver

object AutoUpdate {

    const val ACTION_AUTO_UPDATE = "cat.mvmike.minimalcalendarwidget.AUTO_UPDATE"

    const val ALARM_ID = 859345

    const val INTERVAL_MILLIS = 900000L // 1000*60*15 === 15'

    fun setAlarm(context: Context) {
        val currentMillis = SystemResolver.get().getInstant().toEpochMilli()
        val firstTriggerMillis = currentMillis + INTERVAL_MILLIS

        SystemResolver.get().setRepeatingAlarm(
            context = context,
            alarmId = ALARM_ID,
            firstTriggerMillis = firstTriggerMillis,
            intervalMillis = INTERVAL_MILLIS
        )
    }

    fun cancelAlarm(context: Context) {
        SystemResolver.get().cancelRepeatingAlarm(context, ALARM_ID)
    }
}
