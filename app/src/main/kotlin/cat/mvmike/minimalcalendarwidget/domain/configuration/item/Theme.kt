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
    private val header: CellPack,
    private val day: CellPack,
    private val thisMonth: CellPack,
    private val today: CellPack
) {
    DARK(
        displayString = R.string.dark,
        mainLayout = R.layout.widget_dark,
        mainBackground = R.color.background_full_dark,
        header = CellPack(
            mainLayout = R.layout.dark_cell_header,
            saturdayBackground = R.color.background_saturday_this_month_dark,
            sundayBackground = R.color.background_sunday_this_month_dark
        ),
        day = CellPack(
            mainLayout = R.layout.dark_cell_day,
            saturdayBackground = R.color.background_saturday_dark,
            sundayBackground = R.color.background_sunday_dark
        ),
        thisMonth = CellPack(
            mainLayout = R.layout.dark_cell_day_this_month,
            weekdayBackground = R.color.background_this_month_dark,
            saturdayBackground = R.color.background_saturday_this_month_dark,
            sundayBackground = R.color.background_sunday_this_month_dark
        ),
        today = CellPack(
            mainLayout = R.layout.dark_cell_day_today,
            weekdayBackground = R.color.background_today_dark,
            saturdayBackground = R.color.background_saturday_today_dark,
            sundayBackground = R.color.background_sunday_today_dark
        )
    ),
    LIGHT(
        displayString = R.string.light,
        mainLayout = R.layout.widget_light,
        mainBackground = R.color.background_full_light,
        header = CellPack(
            mainLayout = R.layout.light_cell_header,
            saturdayBackground = R.color.background_saturday_this_month_light,
            sundayBackground = R.color.background_sunday_this_month_light
        ),
        day = CellPack(
            mainLayout = R.layout.light_cell_day,
            saturdayBackground = R.color.background_saturday_light,
            sundayBackground = R.color.background_sunday_light
        ),
        thisMonth = CellPack(
            mainLayout = R.layout.light_cell_day_this_month,
            weekdayBackground = R.color.background_this_month_light,
            saturdayBackground = R.color.background_saturday_this_month_light,
            sundayBackground = R.color.background_sunday_this_month_light
        ),
        today = CellPack(
            mainLayout = R.layout.light_cell_day_today,
            weekdayBackground = R.color.background_today_light,
            saturdayBackground = R.color.background_saturday_today_light,
            sundayBackground = R.color.background_sunday_today_light
        )
    );

    fun getCellHeader(dayOfWeek: DayOfWeek) = header.get(dayOfWeek)

    fun getCellDay(isToday: Boolean, inMonth: Boolean, dayOfWeek: DayOfWeek) = when {
        isToday -> today
        inMonth -> thisMonth
        else -> day
    }.get(dayOfWeek)
}

fun getThemeDisplayValues(context: Context) =
    Theme.values().map { theme ->
        context.getString(theme.displayString).replaceFirstChar { it.uppercase() }
    }.toTypedArray()

data class CellPack(
    val mainLayout: Int,
    val weekdayBackground: Int? = null,
    val saturdayBackground: Int? = null,
    val sundayBackground: Int? = null
) {
    fun get(dayOfWeek: DayOfWeek): Cell = when (dayOfWeek) {
        DayOfWeek.SATURDAY -> Cell(layout = mainLayout, background = saturdayBackground)
        DayOfWeek.SUNDAY -> Cell(layout = mainLayout, background = sundayBackground)
        else -> Cell(layout = mainLayout, background = weekdayBackground)
    }
}

data class Cell(
    val id: Int = android.R.id.text1,
    val layout: Int,
    val background: Int? = null
)
