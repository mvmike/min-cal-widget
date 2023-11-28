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
            viewId = R.id.cell_header,
            layout = R.layout.cell_header,
            textColour = R.color.text_colour_dark,
            saturdayBackground = R.color.background_saturday_this_month_dark,
            sundayBackground = R.color.background_sunday_this_month_dark
        ),
        day = CellPack(
            viewId = R.id.cell_day,
            layout = R.layout.cell_day,
            textColour = R.color.text_colour_semi_dark,
            saturdayBackground = R.color.background_saturday_dark,
            sundayBackground = R.color.background_sunday_dark
        ),
        thisMonth = CellPack(
            viewId = R.id.cell_day,
            layout = R.layout.cell_day,
            textColour = R.color.text_colour_dark,
            weekdayBackground = R.color.background_this_month_dark,
            saturdayBackground = R.color.background_saturday_this_month_dark,
            sundayBackground = R.color.background_sunday_this_month_dark
        ),
        today = CellPack(
            viewId = R.id.cell_day,
            layout = R.layout.cell_day,
            textColour = R.color.text_colour_today,
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
            viewId = R.id.cell_header,
            layout = R.layout.cell_header,
            textColour = R.color.text_colour_light,
            saturdayBackground = R.color.background_saturday_this_month_light,
            sundayBackground = R.color.background_sunday_this_month_light
        ),
        day = CellPack(
            viewId = R.id.cell_day,
            layout = R.layout.cell_day,
            textColour = R.color.text_colour_semi_light,
            saturdayBackground = R.color.background_saturday_light,
            sundayBackground = R.color.background_sunday_light
        ),
        thisMonth = CellPack(
            viewId = R.id.cell_day,
            layout = R.layout.cell_day,
            textColour = R.color.text_colour_light,
            weekdayBackground = R.color.background_this_month_light,
            saturdayBackground = R.color.background_saturday_this_month_light,
            sundayBackground = R.color.background_sunday_this_month_light
        ),
        today = CellPack(
            viewId = R.id.cell_day,
            layout = R.layout.cell_day,
            textColour = R.color.text_colour_today,
            weekdayBackground = R.color.background_today_light,
            saturdayBackground = R.color.background_saturday_today_light,
            sundayBackground = R.color.background_sunday_today_light
        )
    );

    fun getCellHeader(dayOfWeek: DayOfWeek) = header.get(dayOfWeek)

    fun getCellDay(
        isToday: Boolean,
        inMonth: Boolean,
        dayOfWeek: DayOfWeek
    ) = when {
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
    context.getString(displayString).replaceFirstChar { it.uppercase() }