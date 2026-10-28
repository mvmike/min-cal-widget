// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.Widget
import cat.mvmike.minimalcalendarwidget.domain.Variant
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
    ) = Variant.entries.forEach { variant ->
        execute(
            context = context,
            variant = variant
        )
    }

    fun execute(
        context: Context,
        variant: Variant
    ) {
        val name = ComponentName(context, Widget.getReceiverClass(variant))
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = runCatching { appWidgetManager.getAppWidgetIds(name) }.getOrNull()
        appWidgetIds?.let {
            execute(
                context = context,
                appWidgetManager = appWidgetManager,
                appWidgetIds = appWidgetIds,
                variant = variant
            )
        }
    }

    fun execute(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        variant: Variant
    ) = appWidgetIds.forEach { appWidgetId ->
        execute(
            context = context,
            appWidgetManager = appWidgetManager,
            appWidgetId = appWidgetId,
            variant = variant
        )
    }

    fun execute(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        variant: Variant
    ) = runCatching {
        val widgetRemoteView = RemoteViews(
            context.packageName,
            when (variant) {
                Variant.MONTH -> R.layout.widget_month
                Variant.WEEK -> R.layout.widget_week
            }
        )
        widgetRemoteView.removeAllViews(R.id.calendar_days_layout)

        ConfigurationIcon.addListener(context, widgetRemoteView, variant)
        MonthAndYearHeader.addListener(context, widgetRemoteView, variant)

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
            variant = variant,
            firstDayOfWeek = firstDayOfWeek,
            widgetTheme = widgetTheme,
            transparency = transparency,
            textSize = textSize
        )
        DaysService.draw(
            context = context,
            widgetRemoteView = widgetRemoteView,
            variant = variant,
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