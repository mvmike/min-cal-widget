// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.content.Context
import android.os.Build
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver

enum class Colour(
    val displayString: Int
) {
    SYSTEM_ACCENT(
        displayString = R.string.system_accent
    ) {
        override fun isAvailable() = SystemResolver.getRuntimeSDK() >= Build.VERSION_CODES.S

        override fun getHexValue(widgetTheme: Theme) = when (widgetTheme) {
            Theme.DARK -> R.color.instances_system_accent_light
            Theme.LIGHT -> R.color.instances_system_accent_dark
        }
    },
    CYAN(
        displayString = R.string.cyan
    ) {
        override fun getHexValue(widgetTheme: Theme) = R.color.instances_cyan
    },
    MINT(
        displayString = R.string.mint
    ) {
        override fun getHexValue(widgetTheme: Theme) = R.color.instances_mint
    },
    BLUE(
        displayString = R.string.blue
    ) {
        override fun getHexValue(widgetTheme: Theme) = R.color.instances_blue
    },
    GREEN(
        displayString = R.string.green
    ) {
        override fun getHexValue(widgetTheme: Theme) = R.color.instances_green
    },
    YELLOW(
        displayString = R.string.yellow
    ) {
        override fun getHexValue(widgetTheme: Theme) = R.color.instances_yellow
    },
    BLACK(
        displayString = R.string.black
    ) {
        override fun getHexValue(widgetTheme: Theme) = R.color.instances_black
    },
    WHITE(
        displayString = R.string.white
    ) {
        override fun getHexValue(widgetTheme: Theme) = R.color.instances_white
    };

    open fun isAvailable() = true

    abstract fun getHexValue(widgetTheme: Theme): Int

    fun getInstancesColour(isToday: Boolean, widgetTheme: Theme) = when (isToday) {
        true -> R.color.instances_today
        false -> getHexValue(widgetTheme)
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
