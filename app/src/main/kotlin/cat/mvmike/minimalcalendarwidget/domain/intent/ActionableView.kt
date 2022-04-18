// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.MonthWidget
import cat.mvmike.minimalcalendarwidget.R

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
        viewId = R.id.calendar_days_layout,
        code = 99,
        action = "action.WIDGET_PRESS"
    );

    fun addListener(context: Context, widgetRemoteView: RemoteViews) =
        widgetRemoteView.setOnClickPendingIntent(
            viewId,
            PendingIntent.getBroadcast(
                context,
                code,
                Intent(context, MonthWidget::class.java).setAction(action),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
}
