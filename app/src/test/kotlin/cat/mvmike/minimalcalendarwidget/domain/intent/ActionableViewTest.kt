// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.app.PendingIntent
import android.content.Intent
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.Widget
import cat.mvmike.minimalcalendarwidget.domain.Variant
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.CellDay.getExtraInstant
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

internal class ActionableViewTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getActionableViewWithVariants")
    fun addListener_shouldSetOnClickPendingIntentToRemoteViews(
        actionableView: ActionableView,
        variant: Variant
    ) {
        val remoteViews = mockk<RemoteViews>()

        mockIntentPutAppWidgetId()
        every {
            PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } returns pendingIntent
        justRun { remoteViews.setOnClickPendingIntent(actionableView.viewId, pendingIntent) }

        actionableView.addListener(context, remoteViews, appWidgetId, variant)

        verify { Widget.getReceiverClass(variant) }
        verify { anyConstructed<Intent>().action = actionableView.action }
        verifyIntentPutAppWidgetId()
        verify {
            PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        verify { remoteViews.setOnClickPendingIntent(actionableView.viewId, pendingIntent) }
        confirmVerified(remoteViews)
    }

    @ParameterizedTest
    @EnumSource(value = Variant::class)
    fun addListener_shouldThrowExceptionWhenCalledOnCellDay(variant: Variant) {
        assertThrows<UnsupportedOperationException> {
            ActionableView.CellDay.addListener(context, mockk(), appWidgetId, variant)
        }
    }

    @ParameterizedTest
    @EnumSource(value = Variant::class)
    fun addListenerWithStartOfDay_shouldSetOnClickPendingIntentToAllRemoteViews(variant: Variant) {
        val remoteViews1 = mockk<RemoteViews>()
        val remoteViews2 = mockk<RemoteViews>()
        val remoteViews = arrayOf(remoteViews1, null, remoteViews2)

        mockIntentPutAppWidgetId()
        every {
            PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } returns pendingIntent
        justRun { remoteViews1.setOnClickPendingIntent(ActionableView.CellDay.viewId, pendingIntent) }
        justRun { remoteViews2.setOnClickPendingIntent(ActionableView.CellDay.viewId, pendingIntent) }

        ActionableView.CellDay.addListener(context, remoteViews, appWidgetId, variant, systemInstant)

        verify(exactly = 2) { Widget.getReceiverClass(variant) }
        verify(exactly = 2) { anyConstructed<Intent>().action = "${ActionableView.CellDay.action}.${systemInstant.epochSecond}" }
        verifyIntentPutAppWidgetId(times = 2)
        verify(exactly = 2) {
            PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        verify { remoteViews1.setOnClickPendingIntent(ActionableView.CellDay.viewId, pendingIntent) }
        verify { remoteViews2.setOnClickPendingIntent(ActionableView.CellDay.viewId, pendingIntent) }
        confirmVerified(remoteViews1, remoteViews2)
    }

    @Test
    fun getExtraInstant_shouldReturnInstantFromIntentAction() {
        mockGetSystemInstant()
        mockGetSystemZoneId()
        mockIntent("${ActionableView.CellDay.action}.${systemInstant.epochSecond}")

        val result = intent.getExtraInstant()

        assertThat(result).isEqualTo(systemInstant)
        verify { intent.action }
        verifyGetSystemInstant()
        verifyGetSystemZoneId()
    }

    @Test
    fun getExtraInstant_shouldReturnCurrentInstantWhenActionIsMissing() {
        mockIntent(null)
        mockGetSystemInstant()
        mockGetSystemZoneId()

        val result = intent.getExtraInstant()

        assertThat(result).isEqualTo(systemInstant)
        verify { intent.action }
        verifyGetSystemInstant()
        verifyGetSystemZoneId()
    }

    @ParameterizedTest
    @MethodSource("getAndSetActionableViewWithItsProperties")
    fun toActionableView_shouldReturnActionableView(actionableView: ActionableView, intentActionCalls: Int) {
        mockIntent(actionableView.action)

        assertThat(intent.toActionableView()).isEqualTo(actionableView)

        verify(exactly = intentActionCalls) { intent.action }
    }

    @Test
    fun toActionableView_shouldReturnCellDayWhenActionStartsWithCellDayAction() {
        mockIntent("${ActionableView.CellDay.action}.12345")

        assertThat(intent.toActionableView()).isEqualTo(ActionableView.CellDay)

        verify(exactly = 5) { intent.action }
    }

    @Test
    fun toActionableView_shouldReturnNullWhenActionIsUnknown() {
        mockIntent("unknown.action")

        assertThat(intent.toActionableView()).isNull()

        verify(exactly = 5) { intent.action }
    }

    @Test
    fun toActionableView_shouldReturnNullWhenActionIsNull() {
        mockIntent(null)

        assertThat(intent.toActionableView()).isNull()

        verify(exactly = 4) { intent.action }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2611])
    fun getAppWidgetId_shouldReturnIntentExtraAppWidgetId(expectedAppWidgetId: Int) {
        mockIntentAppWidgetId(expectedAppWidgetId)

        assertThat(intent.getAppWidgetId()).isEqualTo(expectedAppWidgetId)

        verifyIntentAppWidgetId()
    }

    private fun getActionableViewWithVariants() = Variant.entries.flatMap { variant ->
        listOf(
            of(ActionableView.ConfigurationIcon, variant),
            of(ActionableView.MonthAndYearHeader, variant),
            of(ActionableView.RowHeader, variant)
        )
    }

    private fun getAndSetActionableViewWithItsProperties() = listOf(
        of(ActionableView.ConfigurationIcon, 1),
        of(ActionableView.MonthAndYearHeader, 2),
        of(ActionableView.RowHeader, 3)
    )
}