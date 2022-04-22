// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import android.appwidget.AppWidgetManager

enum class Format(
    private val minWidth: Int,
    private val minHeight: Int,
    private val monthHeaderLabelLength: Int,
    private val dayHeaderLabelLength: Int,
    val dayCellValueRelativeSize: Float
) {
    STANDARD(
        minWidth = 180,
        minHeight = 70,
        monthHeaderLabelLength = Int.MAX_VALUE,
        dayHeaderLabelLength = 3,
        dayCellValueRelativeSize = 1f
    ),
    REDUCED(
        minWidth = 0,
        minHeight = 0,
        monthHeaderLabelLength = 3,
        dayHeaderLabelLength = 1,
        dayCellValueRelativeSize = 0.6f
    );

    fun fitsSize(width: Int, height: Int) = minWidth <= width && minHeight <= height

    fun getMonthHeaderLabel(value: String) = value.take(monthHeaderLabelLength)

    fun getDayHeaderLabel(value: String) = value.take(dayHeaderLabelLength)
}

fun getFormat(appWidgetManager: AppWidgetManager, appWidgetId: Int) = try {
    with(appWidgetManager.getAppWidgetOptions(appWidgetId)) {
        val width = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val height = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        Format.values()
            .firstOrNull { it.fitsSize(width, height) }
            ?: Format.STANDARD
    }
} catch (ignored: Exception) {
    Format.STANDARD
}
