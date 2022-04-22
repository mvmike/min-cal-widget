// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.user

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate.ACTION_AUTO_UPDATE
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.config.ClockConfig
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import io.mockk.justRun
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.Instant.now

internal class ProcessIntentUseCaseTest : BaseTest() {

    @ParameterizedTest
    @ValueSource(
        strings = [
            ACTION_AUTO_UPDATE,
            ACTION_APPWIDGET_UPDATE,
            "some_random_intent_action"
        ]
    )
    fun shouldDoNothing_whenNoActionableViewIntent(action: String) {
        ProcessIntentUseCase.execute(context, action)
    }

    @ParameterizedTest
    @EnumSource(value = ActionableView::class)
    fun shouldLaunchPermissionsActivity_whenNoUpdateIntentAndNoPermissionsGiven(actionableView: ActionableView) {
        val action = actionableView.action

        mockIsReadCalendarPermitted(false)
        justRun { PermissionsActivity.Companion.start(context) }

        ProcessIntentUseCase.execute(context, action)

        verify { CalendarResolver.isReadCalendarPermitted(context) }
        verify { PermissionsActivity.Companion.start(context) }
    }

    @Test
    fun shouldLaunchConfigurationActivity_whenOpenConfigurationIntentAndPermissionsGiven() {
        mockIsReadCalendarPermitted(true)
        mockkObject(RedrawWidgetUseCase)

        justRun { ConfigurationActivity.Companion.start(context) }
        justRun { RedrawWidgetUseCase.execute(context) }

        ProcessIntentUseCase.execute(context, ActionableView.OPEN_CONFIGURATION.action)

        verify { CalendarResolver.isReadCalendarPermitted(context) }
        verify { ConfigurationActivity.Companion.start(context) }
        verify { RedrawWidgetUseCase.execute(context) }
    }

    @Test
    fun shouldLaunchCalendarActivity_whenOpenCalendarIntentAndPermissionsGiven() {
        val instant = now()
        mockIsReadCalendarPermitted(true)
        mockGetSystemInstant(instant)
        mockkObject(RedrawWidgetUseCase)

        justRun { CalendarActivity.start(context, instant) }
        justRun { RedrawWidgetUseCase.execute(context) }

        ProcessIntentUseCase.execute(context, ActionableView.OPEN_CALENDAR.action)

        verify { CalendarResolver.isReadCalendarPermitted(context) }
        verify { ClockConfig.getInstant() }
        verify { CalendarActivity.start(context, instant) }
        verify { RedrawWidgetUseCase.execute(context) }
    }
}
