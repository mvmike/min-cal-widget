// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.user

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate.ACTION_AUTO_UPDATE
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import io.mockk.justRun
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.Instant.ofEpochSecond

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

        verifyIntentAction()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "action.mincal.configuration_icon_click",
            "action.mincal.month_and_year_header_click",
            "action.mincal.row_header_click",
            "action.mincal.cell_day_click.1675886154",
            "action.mincal.cell_day_click.1671249586",
            "action.mincal.cell_day_click.1624398458",
            "action.mincal.cell_day_click.1434987405"
        ]
    )
    fun shouldLaunchPermissionsActivity_whenActionableViewIntentAndNoPermissionsGiven(action: String) {
        mockIntent(action)
        mockIsReadCalendarPermitted(false)
        justRun { PermissionsActivity.Companion.start(context) }

        ProcessIntentUseCase.execute(context, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verify { PermissionsActivity.Companion.start(context) }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "action.mincal.configuration_icon_click"
        ]
    )
    fun shouldLaunchConfigurationActivityAndRedrawWidget(action: String) {
        mockIntent(action)
        mockIsReadCalendarPermitted(true)
        mockkObject(RedrawWidgetUseCase)

        justRun { ConfigurationActivity.Companion.start(context) }
        justRun { RedrawWidgetUseCase.execute(context) }

        ProcessIntentUseCase.execute(context, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verify { ConfigurationActivity.Companion.start(context) }
        verify { RedrawWidgetUseCase.execute(context) }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "action.mincal.month_and_year_header_click",
            "action.mincal.row_header_click"
        ]
    )
    fun shouldLaunchCalendarActivityAndRedrawWidget_whenIntentAndPermissionsGiven(action: String) {
        mockIntent(action)
        mockIsReadCalendarPermitted(true)
        mockGetSystemInstant()
        mockOpenCalendarOnClickedDay(false)
        mockkObject(RedrawWidgetUseCase)

        justRun { CalendarActivity.start(context, systemInstant) }
        justRun { RedrawWidgetUseCase.execute(context) }

        ProcessIntentUseCase.execute(context, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verifyGetSystemInstant()
        verify { CalendarActivity.start(context, systemInstant) }
        verify { RedrawWidgetUseCase.execute(context) }
    }

    @ParameterizedTest
    @MethodSource("getMincalCalendarIntentActionAndExpectedExtraInstantAndStartTimeInstant")
    fun shouldLaunchCalendarActivityOnTodayAndRedrawWidget(action: String) {
        mockIntent(action)
        mockIsReadCalendarPermitted(true)
        mockGetSystemInstant()
        mockSharedPreferences()
        mockOpenCalendarOnClickedDay(false)
        mockkObject(RedrawWidgetUseCase)

        justRun { CalendarActivity.start(context, systemInstant) }
        justRun { RedrawWidgetUseCase.execute(context) }

        ProcessIntentUseCase.execute(context, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verifyGetSystemInstant()
        verifySharedPreferencesAccess()
        verifyOpenCalendarOnClickedDay()
        verify { CalendarActivity.start(context, systemInstant) }
        verify { RedrawWidgetUseCase.execute(context) }
    }

    @ParameterizedTest
    @MethodSource("getMincalCalendarIntentActionAndExpectedExtraInstantAndStartTimeInstant")
    fun shouldLaunchCalendarActivityOnIntentExtraAndRedrawWidget_whenIntentAndPermissionsGiven(
        action: String,
        extraInstantEpochSeconds: Long,
        startTimeInstantEpochSeconds: Long
    ) {
        val extraInstant = ofEpochSecond(extraInstantEpochSeconds)
        val startTimeInstant = ofEpochSecond(startTimeInstantEpochSeconds)
        mockIntentWithLongExtra(action, systemInstant, extraInstant)
        mockIsReadCalendarPermitted(true)
        mockGetSystemInstant()
        mockGetSystemZoneId()
        mockSharedPreferences()
        mockOpenCalendarOnClickedDay(true)
        mockkObject(RedrawWidgetUseCase)

        justRun { CalendarActivity.start(context, startTimeInstant) }
        justRun { RedrawWidgetUseCase.execute(context) }

        ProcessIntentUseCase.execute(context, intent)

        verifyIntentAction()
        verify { intent.getLongExtra(any(), systemInstant.epochSecond) }
        verifyIsReadCalendarPermitted()
        verifyGetSystemInstant()
        verifyGetSystemZoneId()
        verifySharedPreferencesAccess()
        verifyOpenCalendarOnClickedDay()
        verify { CalendarActivity.start(context, startTimeInstant) }
        verify { RedrawWidgetUseCase.execute(context) }
    }

    private fun getMincalCalendarIntentActionAndExpectedExtraInstantAndStartTimeInstant() = listOf(
        Arguments.of("action.mincal.cell_day_click.1675886154", 1675886154, 1675863134),
        Arguments.of("action.mincal.cell_day_click.1671249586", 1671249586, 1671283934),
        Arguments.of("action.mincal.cell_day_click.1624398458", 1624398458, 1624455134),
        Arguments.of("action.mincal.cell_day_click.1434987405", 1434987405, 1434979934)
    )
}