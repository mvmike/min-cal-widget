// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle

data class Format(
    val width: Int
) {
    private val monthHeaderLabelLength: Int = when {
        width >= 180 -> Int.MAX_VALUE
        else -> 3
    }

    private val dayHeaderLabelLength: Int = when {
        width >= 180 -> 3
        else -> 1
    }

    val headerTextRelativeSize: Float = when {
        width >= 220 -> 1.0f
        width >= 200 -> 0.9f
        else -> 0.8f
    }

    val dayCellTextRelativeSize: Float = when {
        width >= 260 -> 1.2f
        width >= 240 -> 1.1f
        width >= 220 -> 1.0f
        width >= 200 -> 0.9f
        else -> 0.8f
    }

    fun getMonthHeaderLabel(value: String) = value.take(monthHeaderLabelLength)

    fun getDayHeaderLabel(value: String) = value.take(dayHeaderLabelLength)
}

fun getFormat(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) = runCatching {
    appWidgetManager.getAppWidgetOptions(appWidgetId)
        .getWidth(context)
        ?.let { Format(it) }
}.getOrNull()

private fun Bundle.getWidth(context: Context) = when (context.resources.configuration.orientation) {
    ORIENTATION_LANDSCAPE -> getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
    else -> getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
}.takeIf { it > 0 }
