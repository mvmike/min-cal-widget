// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.MonthWidget

object SystemResolver {

    // INTENT

    fun setOnClickPendingIntent(
        context: Context,
        widgetRemoteView: RemoteViews,
        viewId: Int,
        code: Int,
        action: String
    ) = widgetRemoteView.setOnClickPendingIntent(
        viewId,
        PendingIntent.getBroadcast(
            context,
            code,
            Intent(context, MonthWidget::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )

    // ACTIVITY

    fun <E> startActivity(context: Context, clazz: Class<E>) = context.startActivity(
        Intent(context, clazz)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}
