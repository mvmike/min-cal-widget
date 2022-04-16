// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate.ALARM_ID
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate.INTERVAL_MILLIS
import cat.mvmike.minimalcalendarwidget.infrastructure.config.ClockConfig
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.AlarmManagerResolver
import io.mockk.justRun
import io.mockk.verify
import java.time.Instant
import org.junit.jupiter.api.Test

internal class AutoUpdateTest : BaseTest() {

    @Test
    fun setAlarm_shouldSetRepeatingAlarm() {
        val instant = Instant.now()
        mockGetSystemInstant(instant)
        justRun {
            AlarmManagerResolver.setRepeatingAlarm(
                context = context,
                alarmId = ALARM_ID,
                firstTriggerMillis = instant.toEpochMilli() + INTERVAL_MILLIS,
                intervalMillis = INTERVAL_MILLIS
            )
        }

        AutoUpdate.setAlarm(context)

        verify { ClockConfig.getInstant() }
        verify {
            AlarmManagerResolver.setRepeatingAlarm(
                context = context,
                alarmId = ALARM_ID,
                firstTriggerMillis = instant.toEpochMilli() + INTERVAL_MILLIS,
                intervalMillis = INTERVAL_MILLIS
            )
        }
    }

    @Test
    fun cancelAlarm_shouldCancelAlarm() {
        justRun {
            AlarmManagerResolver.cancelRepeatingAlarm(
                context = context,
                alarmId = ALARM_ID
            )
        }

        AutoUpdate.cancelAlarm(context)

        verify {
            AlarmManagerResolver.cancelRepeatingAlarm(
                context = context,
                alarmId = ALARM_ID
            )
        }
    }
}
