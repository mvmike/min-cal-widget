// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.content.Context
import cat.mvmike.minimalcalendarwidget.R
import java.time.DayOfWeek

enum class Theme(
    val displayString: Int,
    val mainLayout: Int,
    val mainBackground: Int,
    private val cellHeader: Cell,
    private val cellHeaderSaturday: Cell,
    private val cellHeaderSunday: Cell,
    private val cellDay: Cell,
    val cellDayThisMonth: Cell,
    private val cellDaySaturday: Cell,
    private val cellDaySunday: Cell,
    private val cellDayToday: Cell,
    private val cellDaySaturdayToday: Cell,
    private val cellDaySundayToday: Cell
) {
    BLACK(
        displayString = R.string.black,
        mainLayout = R.layout.widget_black,
        mainBackground = R.color.background_full_black,
        cellHeader = Cell(layout = R.layout.black_cell_header),
        cellHeaderSaturday = Cell(
            layout = R.layout.black_cell_header_saturday,
            background = R.color.background_saturday_black
        ),
        cellHeaderSunday = Cell(
            layout = R.layout.black_cell_header_sunday,
            background = R.color.background_sunday_black
        ),
        cellDay = Cell(layout = R.layout.black_cell_day),
        cellDayThisMonth = Cell(
            layout = R.layout.black_cell_day_this_month,
            background = R.color.background_this_month_black
        ),
        cellDaySaturday = Cell(
            layout = R.layout.black_cell_day_saturday,
            background = R.color.background_saturday_black
        ),
        cellDaySunday = Cell(
            layout = R.layout.black_cell_day_sunday,
            background = R.color.background_sunday_black
        ),
        cellDayToday = Cell(
            layout = R.layout.black_cell_day_today,
            background = R.color.background_today_black
        ),
        cellDaySaturdayToday = Cell(
            layout = R.layout.black_cell_day_saturday_today,
            background = R.color.background_saturday_today_black
        ),
        cellDaySundayToday = Cell(
            layout = R.layout.black_cell_day_sunday_today,
            background = R.color.background_sunday_today_black
        )
    ),
    GREY(
        displayString = R.string.grey,
        mainLayout = R.layout.widget_grey,
        mainBackground = R.color.background_full_grey,
        cellHeader = Cell(layout = R.layout.grey_cell_header),
        cellHeaderSaturday = Cell(
            layout = R.layout.grey_cell_header_saturday,
            background = R.color.background_saturday_grey
        ),
        cellHeaderSunday = Cell(
            layout = R.layout.grey_cell_header_sunday,
            background = R.color.background_sunday_grey
        ),
        cellDay = Cell(layout = R.layout.grey_cell_day),
        cellDayThisMonth = Cell(
            layout = R.layout.grey_cell_day_this_month,
            background = R.color.background_this_month_grey
        ),
        cellDaySaturday = Cell(
            layout = R.layout.grey_cell_day_saturday,
            background = R.color.background_saturday_grey
        ),
        cellDaySunday = Cell(
            layout = R.layout.grey_cell_day_sunday,
            background = R.color.background_sunday_grey
        ),
        cellDayToday = Cell(
            layout = R.layout.grey_cell_day_today,
            background = R.color.background_today_grey
        ),
        cellDaySaturdayToday = Cell(
            layout = R.layout.grey_cell_day_saturday_today,
            background = R.color.background_saturday_today_grey
        ),
        cellDaySundayToday = Cell(
            layout = R.layout.grey_cell_day_sunday_today,
            background = R.color.background_sunday_today_grey
        )
    ),
    WHITE(
        displayString = R.string.white,
        mainLayout = R.layout.widget_white,
        mainBackground = R.color.background_full_white,
        cellHeader = Cell(layout = R.layout.white_cell_header),
        cellHeaderSaturday = Cell(
            layout = R.layout.white_cell_header_saturday,
            background = R.color.background_saturday_white
        ),
        cellHeaderSunday = Cell(
            layout = R.layout.white_cell_header_sunday,
            background = R.color.background_sunday_white
        ),
        cellDay = Cell(layout = R.layout.white_cell_day),
        cellDayThisMonth = Cell(
            layout = R.layout.white_cell_day_this_month,
            background = R.color.background_this_month_white
        ),
        cellDaySaturday = Cell(
            layout = R.layout.white_cell_day_saturday,
            background = R.color.background_saturday_white
        ),
        cellDaySunday = Cell(
            layout = R.layout.white_cell_day_sunday,
            background = R.color.background_sunday_white
        ),
        cellDayToday = Cell(
            layout = R.layout.white_cell_day_today,
            background = R.color.background_today_white
        ),
        cellDaySaturdayToday = Cell(
            layout = R.layout.white_cell_day_saturday_today,
            background = R.color.background_saturday_today_white
        ),
        cellDaySundayToday = Cell(
            layout = R.layout.white_cell_day_sunday_today,
            background = R.color.background_sunday_today_white
        )
    );

    fun getCellHeader(dayOfWeek: DayOfWeek): Cell {
        return when (dayOfWeek) {
            DayOfWeek.SATURDAY -> cellHeaderSaturday
            DayOfWeek.SUNDAY -> cellHeaderSunday
            else -> cellHeader
        }
    }

    fun getCellDay(isToday: Boolean, inMonth: Boolean, dayOfWeek: DayOfWeek): Cell {
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

data class Cell(
    val id: Int = android.R.id.text1,
    val layout: Int,
    val background: Int? = null
)
