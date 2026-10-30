// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.system

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import cat.mvmike.minimalcalendarwidget.Widget
import cat.mvmike.minimalcalendarwidget.domain.Variant
import cat.mvmike.minimalcalendarwidget.domain.configuration.hasLegacyConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.migrateLegacyConfiguration

object MigrateConfigurationUseCase {

    fun execute(context: Context, appWidgetManager: AppWidgetManager) {
        if (!hasLegacyConfiguration(context)) {
            return
        }
        runCatching {
            Variant.entries.flatMap {
                appWidgetManager.getAppWidgetIds(ComponentName(context, Widget.getReceiverClass(it))).toList()
            }
        }.onSuccess {
            migrateLegacyConfiguration(context, it)
        }
    }
}