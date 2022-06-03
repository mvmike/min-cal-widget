// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.MonthWidget
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.component.DaysHeaderService
import cat.mvmike.minimalcalendarwidget.domain.component.DaysService
import cat.mvmike.minimalcalendarwidget.domain.component.LayoutService
import cat.mvmike.minimalcalendarwidget.domain.component.MonthAndYearHeaderService
import cat.mvmike.minimalcalendarwidget.domain.getFormat
import cat.mvmike.minimalcalendarwidget.domain.intent.addAllListeners

object RedrawWidgetUseCase {

    fun execute(context: Context) {
        val name = ComponentName(context, MonthWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        execute(
            context = context,
            appWidgetManager = appWidgetManager,
            appWidgetIds = appWidgetManager.getAppWidgetIds(name)
        )
    }

    fun execute(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) =
        appWidgetIds.forEach { appWidgetId -> execute(context, appWidgetManager, appWidgetId) }

    fun execute(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val widgetRemoteView = RemoteViews(context.packageName, R.layout.widget)
        widgetRemoteView.removeAllViews(R.id.calendar_days_layout)

        addAllListeners(context, widgetRemoteView)

        val format = getFormat(appWidgetManager, appWidgetId)
        LayoutService.draw(
            context = context,
            widgetRemoteView = widgetRemoteView
        )
        MonthAndYearHeaderService.draw(
            context = context,
            widgetRemoteView = widgetRemoteView,
            format = format
        )
        DaysHeaderService.draw(
            context = context,
            widgetRemoteView = widgetRemoteView,
            format = format
        )
        DaysService.draw(
            context = context,
            widgetRemoteView = widgetRemoteView,
            format = format
        )

        appWidgetManager.updateAppWidget(appWidgetId, widgetRemoteView)
    }
}
