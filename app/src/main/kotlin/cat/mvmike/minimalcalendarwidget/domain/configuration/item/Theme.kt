// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.content.Context
import cat.mvmike.minimalcalendarwidget.R
import java.time.DayOfWeek

enum class Theme(
    val displayString: Int,
    val mainLayout: Int,
    private val cellHeader: Int,
    private val cellHeaderSaturday: Int,
    private val cellHeaderSunday: Int,
    private val cellDay: Int,
    private val cellDayThisMonth: Int,
    private val cellDaySaturday: Int,
    private val cellDaySunday: Int,
    private val cellDayToday: Int,
    private val cellDaySaturdayToday: Int,
    private val cellDaySundayToday: Int
) {
    BLACK(
        displayString = R.string.black,
        mainLayout = R.layout.widget_black,
        cellHeader = R.layout.black_cell_header,
        cellHeaderSaturday = R.layout.black_cell_header_saturday,
        cellHeaderSunday = R.layout.black_cell_header_sunday,
        cellDay = R.layout.black_cell_day,
        cellDayThisMonth = R.layout.black_cell_day_this_month,
        cellDaySaturday = R.layout.black_cell_day_saturday,
        cellDaySunday = R.layout.black_cell_day_sunday,
        cellDayToday = R.layout.black_cell_day_today,
        cellDaySaturdayToday = R.layout.black_cell_day_saturday_today,
        cellDaySundayToday = R.layout.black_cell_day_sunday_today
    ),
    GREY(
        displayString = R.string.grey,
        mainLayout = R.layout.widget_grey,
        cellHeader = R.layout.grey_cell_header,
        cellHeaderSaturday = R.layout.grey_cell_header_saturday,
        cellHeaderSunday = R.layout.grey_cell_header_sunday,
        cellDay = R.layout.grey_cell_day,
        cellDayThisMonth = R.layout.grey_cell_day_this_month,
        cellDaySaturday = R.layout.grey_cell_day_saturday,
        cellDaySunday = R.layout.grey_cell_day_sunday,
        cellDayToday = R.layout.grey_cell_day_today,
        cellDaySaturdayToday = R.layout.grey_cell_day_saturday_today,
        cellDaySundayToday = R.layout.grey_cell_day_sunday_today
    ),
    WHITE(
        displayString = R.string.white,
        mainLayout = R.layout.widget_white,
        cellHeader = R.layout.white_cell_header,
        cellHeaderSaturday = R.layout.white_cell_header_saturday,
        cellHeaderSunday = R.layout.white_cell_header_sunday,
        cellDay = R.layout.white_cell_day,
        cellDayThisMonth = R.layout.white_cell_day_this_month,
        cellDaySaturday = R.layout.white_cell_day_saturday,
        cellDaySunday = R.layout.white_cell_day_sunday,
        cellDayToday = R.layout.white_cell_day_today,
        cellDaySaturdayToday = R.layout.white_cell_day_saturday_today,
        cellDaySundayToday = R.layout.white_cell_day_sunday_today
    );

    fun getCellHeader(dayOfWeek: DayOfWeek): Int {
        return when (dayOfWeek) {
            DayOfWeek.SATURDAY -> cellHeaderSaturday
            DayOfWeek.SUNDAY -> cellHeaderSunday
            else -> cellHeader
        }
    }

    fun getCellDay(isToday: Boolean, inMonth: Boolean, dayOfWeek: DayOfWeek): Int {
        if (isToday) {
            return when (dayOfWeek) {
                DayOfWeek.SATURDAY -> cellDaySaturdayToday
                DayOfWeek.SUNDAY -> cellDaySundayToday
                else -> cellDayToday
            }
        }

        if (inMonth) {
            return when (dayOfWeek) {
                DayOfWeek.SATURDAY -> cellDaySaturday
                DayOfWeek.SUNDAY -> cellDaySunday
                else -> cellDayThisMonth
            }
        }

        return cellDay
    }
}

fun getThemeDisplayValues(context: Context) =
    Theme.values().map { theme ->
        context.getString(theme.displayString).replaceFirstChar { it.uppercase() }
    }.toTypedArray()
