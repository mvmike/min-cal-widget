// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.content.Context
import android.os.Build
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver

enum class Colour(
    val displayString: Int,
    private val darkThemeHexColour: Int,
    private val lightThemeHexColour: Int
) {
    SYSTEM_ACCENT(
        displayString = R.string.system_accent,
        darkThemeHexColour = R.color.instances_system_accent_light,
        lightThemeHexColour = R.color.instances_system_accent_dark
    ) {
        override fun isAvailable() = SystemResolver.getRuntimeSDK() >= Build.VERSION_CODES.S
    },
    CYAN(
        displayString = R.string.cyan,
        darkThemeHexColour = R.color.instances_cyan,
        lightThemeHexColour = R.color.instances_cyan
    ),
    MINT(
        displayString = R.string.mint,
        darkThemeHexColour = R.color.instances_mint,
        lightThemeHexColour = R.color.instances_mint
    ),
    BLUE(
        displayString = R.string.blue,
        darkThemeHexColour = R.color.instances_blue,
        lightThemeHexColour = R.color.instances_blue
    ),
    GREEN(
        displayString = R.string.green,
        darkThemeHexColour = R.color.instances_green,
        lightThemeHexColour = R.color.instances_green
    ),
    YELLOW(
        displayString = R.string.yellow,
        darkThemeHexColour = R.color.instances_yellow,
        lightThemeHexColour = R.color.instances_yellow
    ),
    BLACK(
        displayString = R.string.black,
        darkThemeHexColour = R.color.instances_black,
        lightThemeHexColour = R.color.instances_black
    ),
    WHITE(
        displayString = R.string.white,
        darkThemeHexColour = R.color.instances_white,
        lightThemeHexColour = R.color.instances_white
    );

    open fun isAvailable() = true

    fun getInstancesColour(isToday: Boolean, widgetTheme: Theme) = when (isToday) {
        true -> R.color.instances_today
        else -> when (widgetTheme) {
            Theme.DARK -> darkThemeHexColour
            Theme.LIGHT -> lightThemeHexColour
        }
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
