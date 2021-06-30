// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver

enum class ActionableView(
    val viewId: Int,
    val code: Int,
    val action: String
) {
    OPEN_CONFIGURATION(
        viewId = R.id.configuration_icon,
        code = 98,
        action = "action.WIDGET_CONFIGURATION"
    ),
    OPEN_CALENDAR(
        viewId = R.id.calendar_widget,
        code = 99,
        action = "action.WIDGET_PRESS"
    );

    fun addListener(context: Context, widgetRemoteView: RemoteViews) =
        SystemResolver.get().setOnClickPendingIntent(
            context = context,
            widgetRemoteView = widgetRemoteView,
            viewId = viewId,
            code = code,
            action = action
        )
}
