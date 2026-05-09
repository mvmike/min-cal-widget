// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.BaseTest
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class AutoUpdateTest : BaseTest() {

    private val alarmManager = mockk<AlarmManager>()

    @Test
    fun set_shouldSetRepeatingAlarm() {
        mockGetSystemInstant()
        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
        every {
            PendingIntent.getBroadcast(
                context,
                859345,
                any<Intent>(),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } returns pendingIntent
        justRun {
            alarmManager.setRepeating(
                AlarmManager.RTC,
                systemInstant.toEpochMilli() + 600000L,
                600000L,
                pendingIntent
            )
        }

        AutoUpdate.set(context)

        verifyGetSystemInstant()
        verify { context.getSystemService(Context.ALARM_SERVICE) }
        verify {
            PendingIntent.getBroadcast(
                context,
                859345,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        verify {
            alarmManager.setRepeating(
                AlarmManager.RTC,
                systemInstant.toEpochMilli() + 600000L,
                600000L,
                pendingIntent
            )
        }
    }

    @Test
    fun cancel_shouldCancelAlarm() {
        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
        every {
            PendingIntent.getBroadcast(
                context,
                859345,
                any<Intent>(),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } returns pendingIntent
        justRun { alarmManager.cancel(pendingIntent) }

        AutoUpdate.cancel(context)

        verify { context.getSystemService(Context.ALARM_SERVICE) }
        verify {
            PendingIntent.getBroadcast(
                context,
                859345,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        verify { alarmManager.cancel(pendingIntent) }
    }
}