// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.application.action.system.StartAlarmUseCase
import cat.mvmike.minimalcalendarwidget.application.action.system.StopAlarmUseCase
import cat.mvmike.minimalcalendarwidget.application.action.user.AddListenersUseCase
import cat.mvmike.minimalcalendarwidget.application.action.user.ProcessIntentUseCase
import cat.mvmike.minimalcalendarwidget.application.visual.DrawDaysHeaderUseCase
import cat.mvmike.minimalcalendarwidget.application.visual.DrawDaysUseCase
import cat.mvmike.minimalcalendarwidget.application.visual.DrawMonthAndYearHeaderUseCase
import cat.mvmike.minimalcalendarwidget.application.visual.DrawWidgetLayout
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.clearAllConfiguration
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver

class MonthWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        StartAlarmUseCase.execute(context)
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

        private fun redrawWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            val widgetRemoteView = RemoteViews(context.packageName, EnumConfiguration.WidgetTheme.get(context).mainLayout)
            appWidgetIds.forEach { appWidgetId ->
                widgetRemoteView.removeAllViews(R.id.calendar_widget)

                AddListenersUseCase.execute(
                    context = context,
                    widgetRemoteView = widgetRemoteView
                )

                DrawWidgetLayout.execute(
                    context = context,
                    widgetRemoteView = widgetRemoteView
                )
                DrawMonthAndYearHeaderUseCase.execute(
                    context = context,
                    widgetRemoteView = widgetRemoteView
                )
                DrawDaysHeaderUseCase.execute(
                    context = context,
                    widgetRemoteView = widgetRemoteView
                )
                DrawDaysUseCase.execute(
                    context = context,
                    widgetRemoteView = widgetRemoteView
                )

                appWidgetManager.updateAppWidget(appWidgetId, widgetRemoteView)
            }
        }
    }

}
