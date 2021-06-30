// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.action.user

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate.ACTION_AUTO_UPDATE
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.verify
import java.time.Instant.now
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

internal class ProcessIntentUseCaseTest : BaseTest() {

    @ParameterizedTest
    @ValueSource(strings = [
        ACTION_AUTO_UPDATE,
        ACTION_APPWIDGET_UPDATE
    ])
    fun shouldDoNothing_whenUpdateIntent(action: String) {
        ProcessIntentUseCase.execute(context, action)

        confirmVerified(context)
    }

    @ParameterizedTest
    @EnumSource(value = ActionableView::class)
    fun shouldLaunchPermissionsActivity_whenNoUpdateIntentAndNoPermissionsGiven(actionableView: ActionableView) {
        val action = actionableView.action

        mockIsReadCalendarPermitted(false)
        justRun { systemResolver.startActivity(context, PermissionsActivity::class.java) }

        ProcessIntentUseCase.execute(context, action)

        verify { systemResolver.isReadCalendarPermitted(context) }
        verify { systemResolver.startActivity(context, PermissionsActivity::class.java) }
        confirmVerified(context, systemResolver)
    }

    @Test
    fun shouldLaunchConfigurationActivity_whenOpenConfigurationIntentAndPermissionsGiven() {
        mockIsReadCalendarPermitted(true)
        justRun { systemResolver.startActivity(context, ConfigurationActivity::class.java) }

        ProcessIntentUseCase.execute(context, ActionableView.OPEN_CONFIGURATION.action)

        verify { systemResolver.isReadCalendarPermitted(context) }
        verify { systemResolver.startActivity(context, ConfigurationActivity::class.java) }
        confirmVerified(context, systemResolver)
    }

    @Test
    fun shouldLaunchCalendarActivity_whenOpenCalendarIntentAndPermissionsGiven() {
        val instant = now()
        mockIsReadCalendarPermitted(true)
        mockGetSystemInstant(instant)

        justRun { systemResolver.startCalendarActivity(context, instant) }

        ProcessIntentUseCase.execute(context, ActionableView.OPEN_CALENDAR.action)

        verify { systemResolver.isReadCalendarPermitted(context) }
        verify { systemResolver.getInstant() }
        verify { systemResolver.startCalendarActivity(context, instant) }
        confirmVerified(context, systemResolver)
    }
}
