// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.user

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate.ACTION_AUTO_UPDATE
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.Instant
import java.time.Instant.now
import java.util.stream.Stream

internal class ProcessIntentUseCaseTest : BaseTest() {

    private val intent = mockk<Intent>()

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

        verify { intent.action }
    }

    @ParameterizedTest
    @MethodSource("getMincalIntentActionAndExpectedExtraInstant")
    fun shouldLaunchPermissionsActivity_whenActionableViewIntentAndNoPermissionsGiven(action: String) {
        mockIntent(action)
        mockIsReadCalendarPermitted(false)
        justRun { PermissionsActivity.Companion.start(context) }

        ProcessIntentUseCase.execute(context, intent)

        verify { intent.action }
        verify { CalendarResolver.isReadCalendarPermitted(context) }
        verify { PermissionsActivity.Companion.start(context) }
    }

    @ParameterizedTest
    @MethodSource("getMincalConfigurationIntentActionAndExpectedExtraInstant")
    fun shouldLaunchConfigurationActivityAndRedrawWidgetWithUpsertFormat_whenConfigurationIconIntentAndPermissionsGiven(action: String) {
        mockIntent(action)
        mockIsReadCalendarPermitted(true)
        mockkObject(RedrawWidgetUseCase)

        justRun { ConfigurationActivity.Companion.start(context) }
        justRun { RedrawWidgetUseCase.execute(context, true) }

        ProcessIntentUseCase.execute(context, intent)

        verify { intent.action }
        verify { CalendarResolver.isReadCalendarPermitted(context) }
        verify { ConfigurationActivity.Companion.start(context) }
        verify { RedrawWidgetUseCase.execute(context, true) }
    }

    @ParameterizedTest
    @MethodSource("getMincalCalendarIntentActionAndExpectedExtraInstant")
    fun shouldLaunchCalendarActivityAndRedrawWidgetWithUpsertFormat_whenIntentAndPermissionsGiven(action: String, extraInstant: Instant?) {
        val instant = now()
        extraInstant
            ?.let { mockIntentWithLongExtra(action, instant, it) }
            ?: mockIntent(action)
        mockIsReadCalendarPermitted(true)
        mockGetSystemInstant(instant)
        mockkObject(RedrawWidgetUseCase)

        justRun { CalendarActivity.start(context, extraInstant ?: instant) }
        justRun { RedrawWidgetUseCase.execute(context, true) }

        ProcessIntentUseCase.execute(context, intent)

        verify { intent.action }
        extraInstant?.let { verify { intent.getLongExtra(any(), instant.epochSecond) } }
        verify { CalendarResolver.isReadCalendarPermitted(context) }
        verify { SystemResolver.getSystemInstant() }
        verify { CalendarActivity.start(context, extraInstant ?: instant) }
        verify { RedrawWidgetUseCase.execute(context, true) }
    }

    @Suppress("UnusedPrivateMember")
    private fun getMincalIntentActionAndExpectedExtraInstant() = Stream.concat(
        getMincalConfigurationIntentActionAndExpectedExtraInstant(),
        getMincalCalendarIntentActionAndExpectedExtraInstant()
    )

    private fun getMincalConfigurationIntentActionAndExpectedExtraInstant() = Stream.of(
        Arguments.of("action.mincal.configuration_icon_click", null)
    )

    private fun getMincalCalendarIntentActionAndExpectedExtraInstant() = Stream.of(
        Arguments.of("action.mincal.month_and_year_header_click", null),
        Arguments.of("action.mincal.row_header_click", null),
        Arguments.of("action.mincal.cell_day_click.1675886154", Instant.ofEpochSecond(1675886154)),
        Arguments.of("action.mincal.cell_day_click.1671249586", Instant.ofEpochSecond(1671249586)),
        Arguments.of("action.mincal.cell_day_click.1624398458", Instant.ofEpochSecond(1624398458)),
        Arguments.of("action.mincal.cell_day_click.1434987405", Instant.ofEpochSecond(1434987405))
    )

    private fun mockIntent(action: String) {
        every { intent.action } returns action
        every { intent.addFlags(any()) } returns intent

    }

    private fun mockIntentWithLongExtra(action: String, systemInstant: Instant, extraInstant: Instant) {
        mockIntent(action)
        every { intent.getLongExtra("startOfDayInEpochSeconds", systemInstant.epochSecond) } returns extraInstant.epochSecond
    }
}
