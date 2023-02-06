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
    CONFIGURATION_ICON(
        viewId = R.id.configuration_icon,
        code = 90,
        action = "action.mincal.configuration_icon_click"
    ),
    MONTH_AND_YEAR_HEADER(
        viewId = R.id.month_and_year_header,
        code = 91,
        action = "action.mincal.month_and_year_header_click"
    ),
    CALENDAR_DAYS(
        viewId = R.id.calendar_days_layout,
        code = 92,
        action = "action.mincal.calendar_days_click"
    );

    internal fun addListener(context: Context, widgetRemoteView: RemoteViews) =
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

fun addAllListeners(context: Context, widgetRemoteView: RemoteViews) =
    ActionableView.values().forEach {
        it.addListener(context, widgetRemoteView)
    }
