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
    val dayHighlightDrawable: CellHighlightDrawableThemePack,
    private val header: CellThemePack,
    private val outOfMonth: CellThemePack,
    private val thisMonth: CellThemePack
) {
    DARK(
        displayString = R.string.dark,
        mainBackground = R.color.background_full_dark,
        mainTextColour = R.color.text_colour_dark,
        dayHighlightDrawable = CellHighlightDrawableThemePack(
            rightSingle = R.drawable.day_highlight_right_single_dark,
            rightDouble = R.drawable.day_highlight_right_double_dark,
            centeredSingle = R.drawable.day_highlight_centered_single_dark,
            centeredDouble = R.drawable.day_highlight_centered_double_dark
        ),
        header = CellThemePack(
            textColour = R.color.text_colour_dark,
            saturdayBackground = R.color.background_saturday_this_month_dark,
            sundayBackground = R.color.background_sunday_this_month_dark
        ),
        outOfMonth = CellThemePack(
            textColour = R.color.text_colour_semi_dark,
            saturdayBackground = R.color.background_saturday_dark,
            sundayBackground = R.color.background_sunday_dark
        ),
        thisMonth = CellThemePack(
            textColour = R.color.text_colour_dark,
            weekdayBackground = R.color.background_this_month_dark,
            saturdayBackground = R.color.background_saturday_this_month_dark,
            sundayBackground = R.color.background_sunday_this_month_dark
        )
    ),
    LIGHT(
        displayString = R.string.light,
        mainBackground = R.color.background_full_light,
        mainTextColour = R.color.text_colour_light,
        dayHighlightDrawable = CellHighlightDrawableThemePack(
            rightSingle = R.drawable.day_highlight_right_single_light,
            rightDouble = R.drawable.day_highlight_right_double_light,
            centeredSingle = R.drawable.day_highlight_centered_single_light,
            centeredDouble = R.drawable.day_highlight_centered_double_light
        ),
        header = CellThemePack(
            textColour = R.color.text_colour_light,
            saturdayBackground = R.color.background_saturday_this_month_light,
            sundayBackground = R.color.background_sunday_this_month_light
        ),
        outOfMonth = CellThemePack(
            textColour = R.color.text_colour_semi_light,
            saturdayBackground = R.color.background_saturday_light,
            sundayBackground = R.color.background_sunday_light
        ),
        thisMonth = CellThemePack(
            textColour = R.color.text_colour_light,
            weekdayBackground = R.color.background_this_month_light,
            saturdayBackground = R.color.background_saturday_this_month_light,
            sundayBackground = R.color.background_sunday_this_month_light
        )
    );

    fun getCellHeader(dayOfWeek: DayOfWeek) = header.get(dayOfWeek)

    fun getCellDay(
        inMonth: Boolean,
        dayOfWeek: DayOfWeek
    ) = when {
        inMonth -> thisMonth
        else -> outOfMonth
    }.get(dayOfWeek)
}

fun getThemeDisplayValues(context: Context) =
    Theme.entries.map { it.getDisplayValue(context) }

fun Theme.getDisplayValue(context: Context) =
    context.getString(displayString).replaceFirstChar { it.uppercase() }