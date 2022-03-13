// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.application.action.system.StartAlarmUseCase
import cat.mvmike.minimalcalendarwidget.application.action.system.StopAlarmUseCase
import cat.mvmike.minimalcalendarwidget.application.action.user.AddListenersUseCase
import cat.mvmike.minimalcalendarwidget.application.action.user.ProcessIntentUseCase
import cat.mvmike.minimalcalendarwidget.application.visual.draw.DrawDaysUseCase
import cat.mvmike.minimalcalendarwidget.application.visual.draw.DrawDaysHeaderUseCase
import cat.mvmike.minimalcalendarwidget.application.visual.draw.DrawMonthAndYearHeaderUseCase
import cat.mvmike.minimalcalendarwidget.application.visual.draw.DrawWidgetLayout
import cat.mvmike.minimalcalendarwidget.application.visual.get.GetWidgetFormatUseCase
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.clearAllConfiguration
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver

class MonthWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        StartAlarmUseCase.execute(context)
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        redrawWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        redrawWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        ProcessIntentUseCase.execute(context, intent.action)
        if (SystemResolver.isReadCalendarPermitted(context)) {
            redraw(context)
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        clearAllConfiguration(context)
        StopAlarmUseCase.execute(context)
    }

    companion object {

        fun redraw(context: Context) {
            val name = ComponentName(context, MonthWidget::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            redrawWidgets(
                context = context,
                appWidgetManager = appWidgetManager,
                appWidgetIds = appWidgetManager.getAppWidgetIds(name),
            )
        }

        private fun redrawWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) =
            appWidgetIds.forEach { appWidgetId -> redrawWidget(context, appWidgetManager, appWidgetId)}

        private fun redrawWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val widgetRemoteView = RemoteViews(context.packageName, EnumConfiguration.WidgetTheme.get(context).mainLayout)
            widgetRemoteView.removeAllViews(R.id.calendar_days_layout)

            AddListenersUseCase.execute(
                context = context,
                widgetRemoteView = widgetRemoteView
            )

            val format = GetWidgetFormatUseCase.execute(appWidgetManager, appWidgetId)
            DrawWidgetLayout.execute(
                context = context,
                widgetRemoteView = widgetRemoteView
            )
            DrawMonthAndYearHeaderUseCase.execute(
                context = context,
                widgetRemoteView = widgetRemoteView,
                format = format
            )
            DrawDaysHeaderUseCase.execute(
                context = context,
                widgetRemoteView = widgetRemoteView,
                format = format
            )
            DrawDaysUseCase.execute(
                context = context,
                widgetRemoteView = widgetRemoteView,
                format = format
            )

            appWidgetManager.updateAppWidget(appWidgetId, widgetRemoteView)
        }
    }

}
