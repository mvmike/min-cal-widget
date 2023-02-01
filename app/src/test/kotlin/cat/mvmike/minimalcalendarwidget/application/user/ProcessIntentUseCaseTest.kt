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
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
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
        mockIntent(action)
        ProcessIntentUseCase.execute(context, intent)
    }

    @ParameterizedTest
    @EnumSource(value = ActionableView::class)
    fun shouldLaunchPermissionsActivity_whenNoUpdateIntentAndNoPermissionsGiven(actionableView: ActionableView) {
        val action = actionableView.action
        mockIntent(action)

        mockIsReadCalendarPermitted(false)
        justRun { PermissionsActivity.Companion.start(context) }

        ProcessIntentUseCase.execute(context, intent)

        verify { CalendarResolver.isReadCalendarPermitted(context) }
        verify { PermissionsActivity.Companion.start(context) }
    }

    @Test
    fun shouldLaunchConfigurationActivityAndRedrawWidgetWithUpsertFormat_whenOpenConfigurationIntentAndPermissionsGiven() {
        mockIsReadCalendarPermitted(true)
        mockkObject(RedrawWidgetUseCase)
        mockIntent(ActionableView.OPEN_CONFIGURATION.action)

        justRun { ConfigurationActivity.Companion.start(context) }
        justRun { RedrawWidgetUseCase.execute(context, true) }

        ProcessIntentUseCase.execute(context, intent)

        verify { CalendarResolver.isReadCalendarPermitted(context) }
        verify { ConfigurationActivity.Companion.start(context) }
        verify { RedrawWidgetUseCase.execute(context, true) }
    }

    @Test
    fun shouldLaunchCalendarActivityAndRedrawWidgetWithUpsertFormat_whenOpenCalendarIntentAndPermissionsGiven() {
        val instant = now()
        mockIsReadCalendarPermitted(true)
        mockGetSystemInstant(instant)
        mockkObject(RedrawWidgetUseCase)
        mockIntent(ActionableView.OPEN_CALENDAR.action, instant)

        justRun { CalendarActivity.start(context, instant) }
        justRun { RedrawWidgetUseCase.execute(context, true) }

        ProcessIntentUseCase.execute(context, intent)

        verify { CalendarResolver.isReadCalendarPermitted(context) }
        verify { CalendarActivity.start(context, instant) }
        verify { SystemResolver.getRuntimeSDK() }
        verify { RedrawWidgetUseCase.execute(context, true) }
    }
}
