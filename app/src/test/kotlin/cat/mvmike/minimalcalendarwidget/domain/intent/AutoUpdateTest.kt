// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.Widget
import cat.mvmike.minimalcalendarwidget.domain.Variant
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class AutoUpdateTest : BaseTest() {

    private val alarmManager = mockk<AlarmManager>()

    @ParameterizedTest
    @EnumSource(value = Variant::class)
    fun set_shouldSetRepeatingAlarm(variant: Variant) {
        mockGetSystemInstant()
        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
        every {
            PendingIntent.getBroadcast(
                context,
                859345,
                intent,
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

        AutoUpdate.set(context, variant)

        verifyGetSystemInstant()
        verify { context.getSystemService(Context.ALARM_SERVICE) }
        verify { Widget.getReceiverClass(variant) }
        verify { anyConstructed<Intent>().action = AutoUpdate.ACTION_AUTO_UPDATE }
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
        confirmVerified(alarmManager)
    }

    @ParameterizedTest
    @EnumSource(value = Variant::class)
    fun cancel_shouldCancelAlarm(variant: Variant) {
        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
        every {
            PendingIntent.getBroadcast(
                context,
                859345,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } returns pendingIntent
        justRun { alarmManager.cancel(pendingIntent) }

        AutoUpdate.cancel(context, variant)

        verify { context.getSystemService(Context.ALARM_SERVICE) }
        verify { Widget.getReceiverClass(variant) }
        verify { anyConstructed<Intent>().action = AutoUpdate.ACTION_AUTO_UPDATE }
        verify {
            PendingIntent.getBroadcast(
                context,
                859345,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        verify { alarmManager.cancel(pendingIntent) }
        confirmVerified(alarmManager)
    }
}