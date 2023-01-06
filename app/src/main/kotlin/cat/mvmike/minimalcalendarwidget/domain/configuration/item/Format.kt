// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle

private const val DEFAULT_MONTH_HEADER_LABEL_LENGTH = Int.MAX_VALUE
private const val DEFAULT_DAY_HEADER_LABEL_LENGTH = 3
private const val DEFAULT_HEADER_TEXT_RELATIVE_SIZE = 1f
private const val DEFAULT_DAY_CELL_TEXT_RELATIVE_SIZE = 1f

data class Format(
    val width: Int
) {
    private val monthHeaderLabelLength: Int = when {
        width >= 180 -> DEFAULT_MONTH_HEADER_LABEL_LENGTH
        else -> 3
    }

    private val dayHeaderLabelLength: Int = when {
        width >= 180 -> DEFAULT_DAY_HEADER_LABEL_LENGTH
        else -> 1
    }

    val headerTextRelativeSize: Float = when {
        width >= 220 -> DEFAULT_HEADER_TEXT_RELATIVE_SIZE
        width >= 200 -> 0.9f
        else -> 0.8f
    }

    val dayCellTextRelativeSize: Float = when {
        width >= 260 -> 1.2f
        width >= 240 -> 1.1f
        width >= 220 -> DEFAULT_DAY_CELL_TEXT_RELATIVE_SIZE
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
