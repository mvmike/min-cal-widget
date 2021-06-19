// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.app.PendingIntent
import android.content.Intent
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.MonthWidget
import cat.mvmike.minimalcalendarwidget.R
import io.mockk.EqMatcher
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class ActionableViewTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getActionableViewsWithTheirProperties")
    fun addListener_shouldSetOnClickPendingIntent(actionableViewTestProperties: ActionableViewTestProperties) {
        val intent = mockk<Intent>()
        val pendingIntent = mockk<PendingIntent>()

        mockkConstructor(Intent::class)
        every {
            constructedWith<Intent>(
                EqMatcher(context),
                EqMatcher(MonthWidget::class.java)
            ).setAction(
                actionableViewTestProperties.action
            )
        } returns intent

        mockkStatic(PendingIntent::class)
        every {
            PendingIntent.getBroadcast(
                context,
                actionableViewTestProperties.code,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        } returns pendingIntent

        justRun { widgetRv.setOnClickPendingIntent(actionableViewTestProperties.viewId, any()) }

        actionableViewTestProperties.actionableView.addListener(context, widgetRv)

        verify {
            widgetRv.setOnClickPendingIntent(
                actionableViewTestProperties.viewId,
                pendingIntent
            )
        }
        confirmVerified(intent, pendingIntent, widgetRv)
    }

    companion object {

        @JvmStatic
        @Suppress("unused")
        fun getActionableViewsWithTheirProperties(): Stream<ActionableViewTestProperties> = Stream.of(
            ActionableViewTestProperties(
                actionableView = ActionableView.OPEN_CONFIGURATION,
                viewId = R.id.configuration_icon,
                code = 98,
                action = "action.WIDGET_CONFIGURATION"
            ),
            ActionableViewTestProperties(
                actionableView = ActionableView.OPEN_CALENDAR,
                viewId = R.id.calendar_widget,
                code = 99,
                action = "action.WIDGET_PRESS",
            )
        )
    }

    internal data class ActionableViewTestProperties(
        val actionableView: ActionableView,
        val viewId: Int,
        val code: Int,
        val action: String
    )
}
