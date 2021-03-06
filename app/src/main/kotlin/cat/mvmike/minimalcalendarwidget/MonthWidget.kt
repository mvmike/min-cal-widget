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
        drawWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        ProcessIntentUseCase.execute(context, intent.action)
        forceRedraw(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        clearAllConfiguration(context)
        StopAlarmUseCase.execute(context)
    }

    companion object {

        private fun drawWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            val mainLayoutRemoteView = RemoteViews(context.packageName, EnumConfiguration.WidgetTheme.get(context).mainLayout)
            appWidgetIds.forEach { appWidgetId ->
                drawWidget(
                    context = context,
                    appWidgetManager = appWidgetManager,
                    appWidgetId = appWidgetId,
                    widgetRemoteView = mainLayoutRemoteView
                )
            }
        }

        private fun drawWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, widgetRemoteView: RemoteViews) {
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

        fun forceRedraw(context: Context) {
            when (SystemResolver.isReadCalendarPermitted(context)) {
                false -> return
                true -> {
                    val name = ComponentName(context, MonthWidget::class.java)
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    drawWidgets(
                        context = context,
                        appWidgetManager = appWidgetManager,
                        appWidgetIds = appWidgetManager.getAppWidgetIds(name),
                    )
                }
            }
        }
    }

}
