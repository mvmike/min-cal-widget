// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.application.system.DisableWidgetUseCase
import cat.mvmike.minimalcalendarwidget.application.system.EnableWidgetUseCase
import cat.mvmike.minimalcalendarwidget.application.user.ProcessIntentUseCase
import cat.mvmike.minimalcalendarwidget.domain.Variant
import cat.mvmike.minimalcalendarwidget.domain.Variant.MONTH
import cat.mvmike.minimalcalendarwidget.domain.Variant.WEEK

class MonthWidget : Widget(MONTH)

class WeekWidget : Widget(WEEK)

sealed class Widget(
    internal val variant: Variant
) : AppWidgetProvider() {

    companion object {
        fun getReceiverClass(variant: Variant): Class<out Widget> = when (variant) {
            MONTH -> MonthWidget::class.java
            WEEK -> WeekWidget::class.java
        }
    }

    override fun onEnabled(
        context: Context
    ) {
        super.onEnabled(context)
        EnableWidgetUseCase.execute(context, variant)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetId, variant)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetIds, variant)
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        super.onReceive(context, intent)
        ProcessIntentUseCase.execute(context, variant, intent)
    }

    override fun onDisabled(
        context: Context
    ) {
        super.onDisabled(context)
        DisableWidgetUseCase.execute(context, variant)
    }
}