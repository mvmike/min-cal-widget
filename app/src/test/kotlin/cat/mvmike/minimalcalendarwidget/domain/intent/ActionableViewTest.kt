// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.app.PendingIntent
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.CellDay.getExtraInstant
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource

internal class ActionableViewTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getAndSetActionableViewWithItsProperties")
    fun addListener_shouldSetOnClickPendingIntentToRemoteViews(actionableView: ActionableView) {
        val remoteViews = mockk<RemoteViews>()

        every { PendingIntent.getBroadcast(context, actionableView.code, any(), any()) } returns pendingIntent
        justRun { remoteViews.setOnClickPendingIntent(actionableView.viewId, pendingIntent) }

        actionableView.addListener(context, remoteViews)

        verify { remoteViews.setOnClickPendingIntent(actionableView.viewId, pendingIntent) }
    }

    @Test
    fun addListener_shouldThrowExceptionWhenCalledOnCellDay() {
        assertThrows<UnsupportedOperationException> {
            ActionableView.CellDay.addListener(context, mockk())
        }
    }

    @Test
    fun addListenerWithStartOfDay_shouldSetOnClickPendingIntentToAllRemoteViews() {
        val remoteViews1 = mockk<RemoteViews>()
        val remoteViews2 = mockk<RemoteViews>()
        val remoteViews = arrayOf(remoteViews1, null, remoteViews2)

        every { PendingIntent.getBroadcast(context, ActionableView.CellDay.code, any(), any()) } returns pendingIntent
        justRun { remoteViews1.setOnClickPendingIntent(ActionableView.CellDay.viewId, pendingIntent) }
        justRun { remoteViews2.setOnClickPendingIntent(ActionableView.CellDay.viewId, pendingIntent) }

        ActionableView.CellDay.addListener(context, remoteViews, systemInstant)

        verify { remoteViews1.setOnClickPendingIntent(ActionableView.CellDay.viewId, pendingIntent) }
        verify { remoteViews2.setOnClickPendingIntent(ActionableView.CellDay.viewId, pendingIntent) }
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

    private fun getAndSetActionableViewWithItsProperties() = listOf(
        of(ActionableView.ConfigurationIcon, 1),
        of(ActionableView.MonthAndYearHeader, 2),
        of(ActionableView.RowHeader, 3)
    )
}