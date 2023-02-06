// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.MonthWidget
import cat.mvmike.minimalcalendarwidget.R
import java.time.Instant
import java.util.UUID

enum class ActionableView(
    val viewId: Int,
    val code: Int,
    val action: String,
    val mustBeUnique: Boolean,
) {
    OPEN_CONFIGURATION(
        viewId = R.id.configuration_icon,
        code = 98,
        action = "action.WIDGET_CONFIGURATION",
        mustBeUnique = false,
    ),
    OPEN_CALENDAR(
        viewId = android.R.id.text1,
        code = 99,
        action = "action.WIDGET_PRESS",
        mustBeUnique = true,
    );

    fun addListener(context: Context, widgetRemoteView: RemoteViews, extra: Instant? = null) {
        val intent = Intent(context, MonthWidget::class.java).setAction(if (mustBeUnique) action + UUID.randomUUID().toString() else action)
        extra?.let { intent.putExtra("extra", extra) }

        widgetRemoteView.setOnClickPendingIntent(
            viewId,
            PendingIntent.getBroadcast(
                context,
                code,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
