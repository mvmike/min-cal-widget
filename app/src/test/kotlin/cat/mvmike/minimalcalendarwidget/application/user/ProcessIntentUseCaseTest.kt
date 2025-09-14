// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.user

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate.ACTION_AUTO_UPDATE
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import io.mockk.justRun
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.Instant.ofEpochSecond

private const val MONTH_AND_YEAR_HEADER_CLICK_ACTION = "action.mincal.month_and_year_header_click"
private const val ROW_HEADER_CLICK_ACTION = "action.mincal.row_header_click"
private const val CELL_DAY_CLICK_ACTION = "action.mincal.cell_day_click"

internal class ProcessIntentUseCaseTest : BaseTest() {

    @ParameterizedTest
    @NullSource
    @ValueSource(
        strings = [
            ACTION_AUTO_UPDATE,
            ACTION_APPWIDGET_UPDATE,
            "some_random_intent_action",
            ""
        ]
    )
    fun shouldDoNothing_whenNoActionableViewIntent(intentAction: String?) {
        mockIntent(intentAction)

        ProcessIntentUseCase.execute(context, intent)

        verifyIntentAction()
    }

    @Test
    fun shouldLaunchConfigurationActivity() {
        mockIntent("action.mincal.configuration_icon_click")
        justRun { ConfigurationActivity.Companion.start(context) }

        ProcessIntentUseCase.execute(context, intent)

        verifyIntentAction()
        verify { ConfigurationActivity.Companion.start(context) }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            MONTH_AND_YEAR_HEADER_CLICK_ACTION,
            ROW_HEADER_CLICK_ACTION,
            "$CELL_DAY_CLICK_ACTION.1675886154",
            "$CELL_DAY_CLICK_ACTION.1671249586",
            "$CELL_DAY_CLICK_ACTION.1624398458",
            "$CELL_DAY_CLICK_ACTION.1434987405"
        ]
    )
    fun shouldLaunchPermissionsActivity_whenNoPermissionsGivenAndNoneSymbolSet(action: String) {
        mockIntent(action)
        mockIsReadCalendarPermitted(false)
        mockInstancesSymbolSet(SymbolSet.MINIMAL)
        mockkObject(AutoUpdate)

        justRun { PermissionsActivity.Companion.start(context) }
        justRun { AutoUpdate.set(context) }

        ProcessIntentUseCase.execute(context, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verifyInstancesSymbolSet()
        verify { PermissionsActivity.Companion.start(context) }
        verify { AutoUpdate.set(context) }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            MONTH_AND_YEAR_HEADER_CLICK_ACTION,
            ROW_HEADER_CLICK_ACTION
        ]
    )
    fun shouldLaunchCalendarActivityAndRedrawWidget_whenPermissionsGiven(action: String) {
        mockIntent(action)
        mockIsReadCalendarPermitted(true)
        mockGetSystemInstant()
        mockOpenCalendarOnClickedDay(false)
        mockkObject(RedrawWidgetUseCase)
        mockkObject(AutoUpdate)

        justRun { CalendarActivity.start(context, systemInstant) }
        justRun { RedrawWidgetUseCase.execute(context) }
        justRun { AutoUpdate.set(context) }

        ProcessIntentUseCase.execute(context, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verifyGetSystemInstant()
        verify { CalendarActivity.start(context, systemInstant) }
        verify { RedrawWidgetUseCase.execute(context) }
        verify { AutoUpdate.set(context) }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            MONTH_AND_YEAR_HEADER_CLICK_ACTION,
            ROW_HEADER_CLICK_ACTION
        ]
    )
    fun shouldLaunchCalendarActivityAndRedrawWidget_whenNoPermissionsGivenAndNoneSymbolSet(action: String) {
        mockIntent(action)
        mockIsReadCalendarPermitted(false)
        mockInstancesSymbolSet(SymbolSet.NONE)
        mockGetSystemInstant()
        mockOpenCalendarOnClickedDay(false)
        mockkObject(RedrawWidgetUseCase)
        mockkObject(AutoUpdate)

        justRun { CalendarActivity.start(context, systemInstant) }
        justRun { RedrawWidgetUseCase.execute(context) }
        justRun { AutoUpdate.set(context) }

        ProcessIntentUseCase.execute(context, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verifyInstancesSymbolSet()
        verifyGetSystemInstant()
        verify { CalendarActivity.start(context, systemInstant) }
        verify { RedrawWidgetUseCase.execute(context) }
        verify { AutoUpdate.set(context) }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "$CELL_DAY_CLICK_ACTION.1675786154",
            "$CELL_DAY_CLICK_ACTION.1671349586",
            "$CELL_DAY_CLICK_ACTION.1624298458",
            "$CELL_DAY_CLICK_ACTION.1434887405"
        ]
    )
    fun shouldLaunchCalendarActivityOnTodayAndRedrawWidget(action: String) {
        mockIntent(action)
        mockIsReadCalendarPermitted(true)
        mockGetSystemInstant()
        mockOpenCalendarOnClickedDay(false)
        mockkObject(RedrawWidgetUseCase)
        mockkObject(AutoUpdate)

        justRun { CalendarActivity.start(context, systemInstant) }
        justRun { RedrawWidgetUseCase.execute(context) }
        justRun { AutoUpdate.set(context) }

        ProcessIntentUseCase.execute(context, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verifyGetSystemInstant()
        verifyOpenCalendarOnClickedDay()
        verify { CalendarActivity.start(context, systemInstant) }
        verify { RedrawWidgetUseCase.execute(context) }
        verify { AutoUpdate.set(context) }
    }

    @ParameterizedTest
    @CsvSource(
        "$CELL_DAY_CLICK_ACTION.1675886154,1675863134",
        "$CELL_DAY_CLICK_ACTION.1671249586,1671283934",
        "$CELL_DAY_CLICK_ACTION.1624398458,1624455134",
        "$CELL_DAY_CLICK_ACTION.1434987405,1434979934"
    )
    fun shouldLaunchCalendarActivityOnIntentExtraAndRedrawWidget_whenIntentAndPermissionsGiven(
        action: String,
        startTimeInstantEpochSeconds: Long
    ) {
        mockIntent(action)
        val startTimeInstant = ofEpochSecond(startTimeInstantEpochSeconds)
        mockIsReadCalendarPermitted(true)
        mockGetSystemInstant()
        mockGetSystemZoneId()
        mockOpenCalendarOnClickedDay(true)
        mockkObject(RedrawWidgetUseCase)
        mockkObject(AutoUpdate)

        justRun { CalendarActivity.start(context, startTimeInstant) }
        justRun { RedrawWidgetUseCase.execute(context) }
        justRun { AutoUpdate.set(context) }

        ProcessIntentUseCase.execute(context, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verifyGetSystemInstant()
        verifyGetSystemZoneId()
        verifyOpenCalendarOnClickedDay()
        verify { CalendarActivity.start(context, startTimeInstant) }
        verify { RedrawWidgetUseCase.execute(context) }
        verify { AutoUpdate.set(context) }
    }
}