// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class ActionableViewTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getActionableViewsWithTheirProperties")
    fun addListener_shouldSetOnClickPendingIntent(actionableViewTestProperties: ActionableViewTestProperties) {
        justRun {
            systemResolver.setOnClickPendingIntent(
                context = context,
                remoteViews = widgetRv,
                viewId = actionableViewTestProperties.viewId,
                code = actionableViewTestProperties.code,
                action = actionableViewTestProperties.action
            )
        }

        actionableViewTestProperties.actionableView.addListener(context, widgetRv)

        verify {
            systemResolver.setOnClickPendingIntent(
                context = context,
                remoteViews = widgetRv,
                viewId = actionableViewTestProperties.viewId,
                code = actionableViewTestProperties.code,
                action = actionableViewTestProperties.action
            )
        }
        confirmVerified(systemResolver, context, widgetRv)
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
