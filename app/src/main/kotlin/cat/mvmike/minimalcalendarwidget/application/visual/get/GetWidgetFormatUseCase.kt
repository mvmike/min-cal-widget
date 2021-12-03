// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual.get

import android.appwidget.AppWidgetManager
import cat.mvmike.minimalcalendarwidget.domain.Format

object GetWidgetFormatUseCase {

    fun execute(appWidgetManager: AppWidgetManager, appWidgetId: Int): Format =
        try {
            with(appWidgetManager.getAppWidgetOptions(appWidgetId)) {
                val width = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
                val height = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
                return Format.values()
                    .firstOrNull { it.fitsSize(width, height) }
                    ?: Format.STANDARD
            }
        } catch (ignored: Exception) {
            Format.STANDARD
        }
}
