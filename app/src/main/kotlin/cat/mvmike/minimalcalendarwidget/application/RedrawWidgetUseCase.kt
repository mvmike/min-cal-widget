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
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.PercentageConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.isFirstDayOfWeekLocalePreferenceEnabled
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.ConfigurationIcon
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.MonthAndYearHeader
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver.getSystemFirstDayOfWeek
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val emptyCoroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> }

object RedrawWidgetUseCase {

    fun execute(
        context: Context
    ) {
        val name = ComponentName(context, MonthWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = runCatching { appWidgetManager.getAppWidgetIds(name) }.getOrNull()
        appWidgetIds?.let {
            execute(
                context = context,
                appWidgetManager = appWidgetManager,
                appWidgetIds = appWidgetIds
            )
        }
    }

    fun execute(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) = appWidgetIds.forEach { appWidgetId ->
        execute(
            context = context,
            appWidgetManager = appWidgetManager,
            appWidgetId = appWidgetId
        )
    }

    fun execute(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) = runCatching {
        val widgetRemoteView = RemoteViews(context.packageName, R.layout.widget)
        widgetRemoteView.removeAllViews(R.id.calendar_days_layout)

        ConfigurationIcon.addListener(context, widgetRemoteView)
        MonthAndYearHeader.addListener(context, widgetRemoteView)

        val textSize = PercentageConfigurationItem.WidgetTextSize.get(context)
        val widgetTheme = EnumConfigurationItem.WidgetTheme.get(context)
        val transparency = PercentageConfigurationItem.WidgetTransparency.get(context)
        val firstDayOfWeek = when {
            isFirstDayOfWeekLocalePreferenceEnabled() -> getSystemFirstDayOfWeek()
            else -> EnumConfigurationItem.FirstDayOfWeek.get(context)
        }

        LayoutService.draw(
            context = context,
            widgetRemoteView = widgetRemoteView,
            widgetTheme = widgetTheme,
            transparency = transparency
        )
        MonthAndYearHeaderService.draw(
            context = context,
            widgetRemoteView = widgetRemoteView,
            textSize = textSize,
            widgetTheme = widgetTheme
        )
        DaysHeaderService.draw(
            context = context,
            widgetRemoteView = widgetRemoteView,
            firstDayOfWeek = firstDayOfWeek,
            widgetTheme = widgetTheme,
            transparency = transparency,
            textSize = textSize
        )
        DaysService.draw(
            context = context,
            widgetRemoteView = widgetRemoteView,
            firstDayOfWeek = firstDayOfWeek,
            widgetTheme = widgetTheme,
            transparency = transparency,
            textSize = textSize
        )

        CoroutineScope(Dispatchers.Default).launch(emptyCoroutineExceptionHandler) {
            appWidgetManager.updateAppWidget(appWidgetId, widgetRemoteView)
        }
    }
}