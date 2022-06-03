// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.content.Context
import cat.mvmike.minimalcalendarwidget.R
import java.time.DayOfWeek

enum class Theme(
    val displayString: Int,
    val mainBackground: Int,
    val mainTextColour: Int,
    private val header: CellPack,
    private val day: CellPack,
    private val thisMonth: CellPack,
    private val today: CellPack
) {
    DARK(
        displayString = R.string.dark,
        mainBackground = R.color.background_full_dark,
        mainTextColour = R.color.text_colour_dark,
        header = CellPack(
            mainLayout = R.layout.cell_header,
            textColour = R.color.text_colour_dark,
            saturdayBackground = R.color.background_saturday_this_month_dark,
            sundayBackground = R.color.background_sunday_this_month_dark
        ),
        day = CellPack(
            mainLayout = R.layout.cell_day,
            textColour = R.color.text_colour_semi_dark,
            saturdayBackground = R.color.background_saturday_dark,
            sundayBackground = R.color.background_sunday_dark
        ),
        thisMonth = CellPack(
            mainLayout = R.layout.cell_day,
            textColour = R.color.text_colour_dark,
            weekdayBackground = R.color.background_this_month_dark,
            saturdayBackground = R.color.background_saturday_this_month_dark,
            sundayBackground = R.color.background_sunday_this_month_dark
        ),
        today = CellPack(
            mainLayout = R.layout.cell_day,
            textColour = R.color.text_colour_dark,
            weekdayBackground = R.color.background_today_dark,
            saturdayBackground = R.color.background_saturday_today_dark,
            sundayBackground = R.color.background_sunday_today_dark
        )
    ),
    LIGHT(
        displayString = R.string.light,
        mainBackground = R.color.background_full_light,
        mainTextColour = R.color.text_colour_light,
        header = CellPack(
            mainLayout = R.layout.cell_header,
            textColour = R.color.text_colour_light,
            saturdayBackground = R.color.background_saturday_this_month_light,
            sundayBackground = R.color.background_sunday_this_month_light
        ),
        day = CellPack(
            mainLayout = R.layout.cell_day,
            textColour = R.color.text_colour_semi_light,
            saturdayBackground = R.color.background_saturday_light,
            sundayBackground = R.color.background_sunday_light
        ),
        thisMonth = CellPack(
            mainLayout = R.layout.cell_day,
            textColour = R.color.text_colour_light,
            weekdayBackground = R.color.background_this_month_light,
            saturdayBackground = R.color.background_saturday_this_month_light,
            sundayBackground = R.color.background_sunday_this_month_light
        ),
        today = CellPack(
            mainLayout = R.layout.cell_day,
            textColour = R.color.text_colour_light,
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
        theme.getDisplayValue(context)
    }.toTypedArray()

fun Theme.getDisplayValue(context: Context) =
    context.getString(this.displayString).replaceFirstChar { it.uppercase() }

data class CellPack(
    val mainLayout: Int,
    val textColour: Int,
    val weekdayBackground: Int? = null,
    val saturdayBackground: Int? = null,
    val sundayBackground: Int? = null
) {
    fun get(dayOfWeek: DayOfWeek) = Cell(
        layout = mainLayout,
        textColour = textColour,
        background = when (dayOfWeek) {
            DayOfWeek.SATURDAY -> saturdayBackground
            DayOfWeek.SUNDAY -> sundayBackground
            else -> weekdayBackground
        }
    )
}

data class Cell(
    val id: Int = android.R.id.text1,
    val layout: Int,
    val textColour: Int,
    val background: Int? = null
)
