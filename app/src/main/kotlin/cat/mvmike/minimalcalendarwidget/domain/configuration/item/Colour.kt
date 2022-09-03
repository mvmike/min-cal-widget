// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.content.Context
import android.os.Build
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver

enum class Colour(
    val hexValue: Int,
    val displayString: Int
) {
    SYSTEM_ACCENT(
        hexValue = R.color.instances_system_accent,
        displayString = R.string.system_accent
    ) {
        override fun isAvailable() = SystemResolver.getRuntimeSDK() >= Build.VERSION_CODES.S
    },
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
    BLACK(
        hexValue = R.color.instances_black,
        displayString = R.string.black
    ),
    WHITE(
        hexValue = R.color.instances_white,
        displayString = R.string.white
    );

    open fun isAvailable() = true

    fun getInstancesColour(isToday: Boolean) = when (isToday) {
        true -> R.color.instances_today
        false -> this.hexValue
    }
}

fun getAvailableColors() =
    Colour.values().filter { it.isAvailable() }

fun Colour.getDisplayValue(context: Context) =
    context.getString(this.displayString).replaceFirstChar { it.uppercase() }

fun getColourDisplayValues(context: Context) =
    getAvailableColors()
        .map { it.getDisplayValue(context) }
        .toTypedArray()
