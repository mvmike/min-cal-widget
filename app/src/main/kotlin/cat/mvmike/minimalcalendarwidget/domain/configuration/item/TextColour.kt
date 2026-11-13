// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.content.Context
import android.os.Build
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextColour.CustomHex
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextColour.SystemAccent
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver

internal const val SYSTEM_ACCENT_KEY = "SYSTEM_ACCENT"
private const val DEFAULT_CUSTOM_HEX = "#FF00CCCC"
private val HEX_COLOUR_REGEX = Regex("^#([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$")

private val LEGACY_COLOUR_HEX = mapOf(
    "CYAN" to DEFAULT_CUSTOM_HEX,
    "MINT" to "#FF7EFF7E",
    "BLUE" to "#FF003BFF",
    "GREEN" to "#FF007E00",
    "YELLOW" to "#FFFFFF00",
    "BLACK" to "#FF000000",
    "WHITE" to "#FFFFFFFF"
)

sealed class TextColour {

    abstract fun getDisplayValue(context: Context): String

    abstract fun getStoredValue(): String

    abstract fun getInstancesColour(
        isToday: Boolean,
        widgetTheme: Theme
    ): Colour

    data object SystemAccent : TextColour() {

        override fun getDisplayValue(context: Context) =
            context.getString(R.string.system_accent).replaceFirstChar { it.uppercase() }

        override fun getStoredValue() = SYSTEM_ACCENT_KEY

        override fun getInstancesColour(
            isToday: Boolean,
            widgetTheme: Theme
        ) = when {
            isToday -> Colour.Resource(R.color.instances_today)
            else -> when (widgetTheme) {
                Theme.DARK -> Colour.Resource(R.color.instances_system_accent_light)
                Theme.LIGHT -> Colour.Resource(R.color.instances_system_accent_dark)
            }
        }
    }

    data class CustomHex(
        val hexColour: String
    ) : TextColour() {

        override fun getDisplayValue(context: Context) = hexColour.uppercase()

        override fun getStoredValue() = hexColour.uppercase()

        override fun getInstancesColour(
            isToday: Boolean,
            widgetTheme: Theme
        ) = when {
            isToday -> Colour.Resource(R.color.instances_today)
            else -> Colour.Hex(hexColour)
        }
    }
}

fun textColourFromStoredValue(value: String?): TextColour = when {
    value == null -> defaultTextColour()
    value == SYSTEM_ACCENT_KEY -> when {
        isSystemAccentAvailable() -> SystemAccent
        else -> CustomHex(DEFAULT_CUSTOM_HEX)
    }
    value in LEGACY_COLOUR_HEX -> CustomHex(LEGACY_COLOUR_HEX.getValue(value))
    HEX_COLOUR_REGEX.matches(value) -> CustomHex(value)
    else -> defaultTextColour()
}

fun isSystemAccentAvailable() =
    SystemResolver.getRuntimeSDK() >= Build.VERSION_CODES.S

fun defaultTextColour() = when {
    isSystemAccentAvailable() -> SystemAccent
    else -> CustomHex(DEFAULT_CUSTOM_HEX)
}