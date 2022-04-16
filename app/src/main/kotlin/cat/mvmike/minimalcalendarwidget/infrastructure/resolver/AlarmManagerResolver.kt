package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.MonthWidget
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate

object AlarmManagerResolver {

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

}
