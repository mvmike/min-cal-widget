// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.user

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.Variant
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate.ACTION_AUTO_UPDATE
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import java.time.Instant.ofEpochSecond

private const val MONTH_AND_YEAR_HEADER_CLICK_ACTION = "action.mincal.month_and_year_header_click"
private const val ROW_HEADER_CLICK_ACTION = "action.mincal.row_header_click"
private const val CELL_DAY_CLICK_ACTION = "action.mincal.cell_day_click"

internal class ProcessIntentUseCaseTest : BaseTest() {

    @ParameterizedTest
    @CsvSource(
        "MONTH,",
        "WEEK,",
        "MONTH,$ACTION_AUTO_UPDATE",
        "WEEK,$ACTION_AUTO_UPDATE",
        "MONTH,$ACTION_APPWIDGET_UPDATE",
        "WEEK,$ACTION_APPWIDGET_UPDATE",
        "MONTH,some_random_intent_action",
        "WEEK,some_random_intent_action",
        "MONTH,''",
        "WEEK,''"
    )
    fun execute_shouldDoNothing_whenNoActionableViewIntent(
        variant: Variant,
        intentAction: String?
    ) {
        mockIntent(intentAction)

        ProcessIntentUseCase.execute(context, variant, intent)

        verifyIntentAction()
    }

    @ParameterizedTest
    @EnumSource(value = Variant::class)
    fun execute_shouldLaunchConfigurationActivity(variant: Variant) {
        mockIntent("action.mincal.configuration_icon_click")
        justRun { ConfigurationActivity.start(context) }

        ProcessIntentUseCase.execute(context, variant, intent)

        verifyIntentAction()
        verify { ConfigurationActivity.start(context) }
    }

    @ParameterizedTest
    @CsvSource(
        "MONTH,$MONTH_AND_YEAR_HEADER_CLICK_ACTION",
        "WEEK,$ROW_HEADER_CLICK_ACTION",
        "MONTH,$CELL_DAY_CLICK_ACTION.1675886154",
        "WEEK,$CELL_DAY_CLICK_ACTION.1671249586",
        "MONTH,$CELL_DAY_CLICK_ACTION.1624398458",
        "WEEK,$CELL_DAY_CLICK_ACTION.1434987405"
    )
    fun execute_shouldLaunchPermissionsActivity_whenNoPermissionsGiven(
        variant: Variant,
        action: String
    ) {
        mockIntent(action)
        mockIsReadCalendarPermitted(false)
        mockInstancesSymbolSet(SymbolSet.MINIMAL)
        mockkObject(AutoUpdate)

        justRun { PermissionsActivity.start(context) }
        justRun { AutoUpdate.set(context, variant) }

        ProcessIntentUseCase.execute(context, variant, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verifyInstancesSymbolSet()
        verify { PermissionsActivity.start(context) }
        verify { AutoUpdate.set(context, variant) }
        confirmVerified(AutoUpdate)
    }

    @ParameterizedTest
    @CsvSource(
        "MONTH,$MONTH_AND_YEAR_HEADER_CLICK_ACTION",
        "WEEK,$MONTH_AND_YEAR_HEADER_CLICK_ACTION",
        "MONTH,$ROW_HEADER_CLICK_ACTION",
        "WEEK,$ROW_HEADER_CLICK_ACTION"
    )
    fun execute_shouldLaunchCalendarActivityAndRedrawWidget_whenPermissionsGiven(
        variant: Variant,
        action: String
    ) {
        mockIntent(action)
        mockIsReadCalendarPermitted(true)
        mockGetSystemInstant()
        mockOpenCalendarOnClickedDay(false)
        mockkObject(RedrawWidgetUseCase)
        mockkObject(AutoUpdate)

        justRun { CalendarActivity.start(context, systemInstant) }
        justRun { RedrawWidgetUseCase.execute(context) }
        justRun { AutoUpdate.set(context, variant) }

        ProcessIntentUseCase.execute(context, variant, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verifyGetSystemInstant()
        verify { CalendarActivity.start(context, systemInstant) }
        verify { RedrawWidgetUseCase.execute(context) }
        verify { AutoUpdate.set(context, variant) }
        confirmVerified(RedrawWidgetUseCase, AutoUpdate)
    }

    @ParameterizedTest
    @CsvSource(
        "MONTH,$MONTH_AND_YEAR_HEADER_CLICK_ACTION",
        "WEEK,$MONTH_AND_YEAR_HEADER_CLICK_ACTION",
        "MONTH,$ROW_HEADER_CLICK_ACTION",
        "WEEK,$ROW_HEADER_CLICK_ACTION"
    )
    fun execute_shouldLaunchCalendarActivityAndRedrawWidget_whenNoPermissionsGivenAndNoneSymbolSet(
        variant: Variant,
        action: String
    ) {
        mockIntent(action)
        mockIsReadCalendarPermitted(false)
        mockInstancesSymbolSet(SymbolSet.NONE)
        mockGetSystemInstant()
        mockOpenCalendarOnClickedDay(false)
        mockkObject(RedrawWidgetUseCase)
        mockkObject(AutoUpdate)

        justRun { CalendarActivity.start(context, systemInstant) }
        justRun { RedrawWidgetUseCase.execute(context) }
        justRun { AutoUpdate.set(context, variant) }

        ProcessIntentUseCase.execute(context, variant, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verifyInstancesSymbolSet()
        verifyGetSystemInstant()
        verify { CalendarActivity.start(context, systemInstant) }
        verify { RedrawWidgetUseCase.execute(context) }
        verify { AutoUpdate.set(context, variant) }
        confirmVerified(RedrawWidgetUseCase, AutoUpdate)
    }

    @ParameterizedTest
    @CsvSource(
        "MONTH,$CELL_DAY_CLICK_ACTION.1675786154",
        "WEEK,$CELL_DAY_CLICK_ACTION.1671349586",
        "MONTH,$CELL_DAY_CLICK_ACTION.1624298458",
        "WEEK,$CELL_DAY_CLICK_ACTION.1434887405"
    )
    fun execute_shouldLaunchCalendarActivityOnTodayAndRedrawWidget(
        variant: Variant,
        action: String
    ) {
        mockIntent(action)
        mockIsReadCalendarPermitted(true)
        mockGetSystemInstant()
        mockOpenCalendarOnClickedDay(false)
        mockkObject(RedrawWidgetUseCase)
        mockkObject(AutoUpdate)

        justRun { CalendarActivity.start(context, systemInstant) }
        justRun { RedrawWidgetUseCase.execute(context) }
        justRun { AutoUpdate.set(context, variant) }

        ProcessIntentUseCase.execute(context, variant, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verifyGetSystemInstant()
        verifyOpenCalendarOnClickedDay()
        verify { CalendarActivity.start(context, systemInstant) }
        verify { RedrawWidgetUseCase.execute(context) }
        verify { AutoUpdate.set(context, variant) }
        confirmVerified(RedrawWidgetUseCase, AutoUpdate)
    }

    @ParameterizedTest
    @CsvSource(
        "MONTH,$CELL_DAY_CLICK_ACTION.1675886154,1675863134",
        "WEEK,$CELL_DAY_CLICK_ACTION.1671249586,1671283934",
        "MONTH,$CELL_DAY_CLICK_ACTION.1624398458,1624455134",
        "WEEK,$CELL_DAY_CLICK_ACTION.1434987405,1434979934"
    )
    fun execute_shouldLaunchCalendarActivityOnIntentExtraAndRedrawWidget_whenIntentAndPermissionsGiven(
        variant: Variant,
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
        justRun { AutoUpdate.set(context, variant) }

        ProcessIntentUseCase.execute(context, variant, intent)

        verifyIntentAction()
        verifyIsReadCalendarPermitted()
        verifyGetSystemInstant()
        verifyGetSystemZoneId()
        verifyOpenCalendarOnClickedDay()
        verify { CalendarActivity.start(context, startTimeInstant) }
        verify { RedrawWidgetUseCase.execute(context) }
        verify { AutoUpdate.set(context, variant) }
        confirmVerified(RedrawWidgetUseCase, AutoUpdate)
    }
}