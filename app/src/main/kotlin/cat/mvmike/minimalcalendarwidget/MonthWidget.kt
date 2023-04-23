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

class MonthWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        EnableWidgetUseCase.execute(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetId)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        super.onReceive(context, intent)
        ProcessIntentUseCase.execute(context, intent)
    }

    override fun onDisabled(
        context: Context
    ) {
        super.onDisabled(context)
        DisableWidgetUseCase.execute(context)
    }
}
