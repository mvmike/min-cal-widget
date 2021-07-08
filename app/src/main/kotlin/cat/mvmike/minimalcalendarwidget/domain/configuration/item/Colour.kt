// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.content.Context
import cat.mvmike.minimalcalendarwidget.R

enum class Colour(
    val hexValue: Int,
    val displayString: Int,
) {
    CYAN(
        hexValue = R.color.instances_cyan,
        displayString = R.string.cyan
    ),
    MINT(
        hexValue = R.color.instances_mint,
        displayString = R.string.mint
    ),
    BLUE(
        hexValue = R.color.instances_blue,
        displayString = R.string.blue
    ),
    GREEN(
        hexValue = R.color.instances_green,
        displayString = R.string.green
    ),
    YELLOW(
        hexValue = R.color.instances_yellow,
        displayString = R.string.yellow
    ),
    WHITE(
        hexValue = R.color.instances_white,
        displayString = R.string.white
    );

    fun getInstancesColour(isToday: Boolean) = when(isToday){
        true -> R.color.instances_today
        false -> this.hexValue
    }
}

fun getColourDisplayValues(context: Context) =
    Colour.values().map { colour ->
        context.getString(colour.displayString).replaceFirstChar { it.uppercase() }
    }.toTypedArray()
