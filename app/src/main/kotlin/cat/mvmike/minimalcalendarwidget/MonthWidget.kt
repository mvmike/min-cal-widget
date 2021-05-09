// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.application.action.system.RegisterReceiversUseCase
import cat.mvmike.minimalcalendarwidget.application.action.system.UnregisterReceiversUseCase
import cat.mvmike.minimalcalendarwidget.application.action.user.AddListenersUseCase
import cat.mvmike.minimalcalendarwidget.application.action.user.ProcessIntentUseCase
import cat.mvmike.minimalcalendarwidget.application.visual.DrawDaysHeaderUseCase
import cat.mvmike.minimalcalendarwidget.application.visual.DrawDaysUseCase
import cat.mvmike.minimalcalendarwidget.application.visual.DrawMonthAndYearHeaderUseCase
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.domain.configuration.clearAllConfiguration
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver

class MonthWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        RegisterReceiversUseCase.execute(context)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        drawWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        ProcessIntentUseCase.execute(context, intent)
        forceRedraw(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        clearAllConfiguration(context)
        UnregisterReceiversUseCase.execute(context)
    }

    companion object {

        private fun drawWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            val mainLayoutRemoteView = RemoteViews(context.packageName, Configuration.CalendarTheme.get(context).mainLayout)
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
                remoteViews = widgetRemoteView
            )

            DrawMonthAndYearHeaderUseCase.execute(
                context = context,
                widgetRemoteView = widgetRemoteView
            )
            DrawDaysHeaderUseCase.execute(
                context = context,
                widgetRv = widgetRemoteView
            )
            DrawDaysUseCase.execute(
                context = context,
                remoteViews = widgetRemoteView
            )

            appWidgetManager.updateAppWidget(appWidgetId, widgetRemoteView)
        }

        fun forceRedraw(context: Context) {
            when (SystemResolver.get().isReadCalendarPermitted(context)) {
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
