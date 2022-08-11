// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle

private const val DEFAULT_DAY_HEADER_LABEL_LENGTH = 3
private const val DEFAULT_HEADER_TEXT_RELATIVE_SIZE = 1f
private const val DEFAULT_DAY_CELL_TEXT_RELATIVE_SIZE = 1f

data class Format(
    private val monthHeaderLabelLength: Int = Int.MAX_VALUE,
    private val dayHeaderLabelLength: Int = DEFAULT_DAY_HEADER_LABEL_LENGTH,
    val headerTextRelativeSize: Float = DEFAULT_HEADER_TEXT_RELATIVE_SIZE,
    val dayCellTextRelativeSize: Float = DEFAULT_DAY_CELL_TEXT_RELATIVE_SIZE
) {
    fun getMonthHeaderLabel(value: String) = value.take(monthHeaderLabelLength)

    fun getDayHeaderLabel(value: String) = value.take(dayHeaderLabelLength)
}

fun getFormat(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) = try {
    with(appWidgetManager.getAppWidgetOptions(appWidgetId)) {
        val width = getWidth(context)
        Format(
            monthHeaderLabelLength = when {
                width >= 180 -> Int.MAX_VALUE
                else -> 3
            },
            dayHeaderLabelLength = when {
                width >= 180 -> DEFAULT_DAY_HEADER_LABEL_LENGTH
                else -> 1
            },
            headerTextRelativeSize = when {
                width >= 220 -> DEFAULT_DAY_CELL_TEXT_RELATIVE_SIZE
                width >= 200 -> 0.9f
                else -> 0.8f
            },
            dayCellTextRelativeSize = when {
                width >= 260 -> 1.2f
                width >= 240 -> 1.1f
                width >= 220 -> DEFAULT_DAY_CELL_TEXT_RELATIVE_SIZE
                width >= 200 -> 0.9f
                else -> 0.8f
            }
        )
    }
} catch (ignored: Exception) {
    Format()
}

private fun Bundle.getWidth(context: Context) = when (
    context.resources.configuration.orientation == ORIENTATION_LANDSCAPE
) {
    true -> getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
    else -> getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
}
