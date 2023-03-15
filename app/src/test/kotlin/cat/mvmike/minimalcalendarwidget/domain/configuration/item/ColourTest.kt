// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource

internal const val INSTANCES_COLOUR_TODAY_ID = 2131034188

internal const val CYAN_INSTANCES_COLOUR_ID = 2131034183
private const val MINT_INSTANCES_COLOUR_ID = 2131034185
private const val BLUE_INSTANCES_COLOUR_ID = 2131034182
private const val GREEN_INSTANCES_COLOUR_ID = 2131034184
private const val YELLOW_INSTANCES_COLOUR_ID = 2131034190
private const val BLACK_INSTANCES_COLOUR_ID = 2131034181
private const val WHITE_INSTANCES_COLOUR_ID = 2131034189

private const val SYSTEM_ACCENT_DARK_THEME_INSTANCES_COLOUR_ID = 2131034187
private const val SYSTEM_ACCENT_LIGHT_THEME_INSTANCES_COLOUR_ID = 2131034186

internal class ColourTest : BaseTest() {

    @ParameterizedTest
    @EnumSource(value = Colour::class)
    fun getInstancesColour_shouldAlwaysReturnTheSameValueWhenIsToday(colour: Colour) {
        Theme.values().forEach {
            val todayInstancesColour = colour.getInstancesColour(true, it)
            assertThat(todayInstancesColour).isEqualTo(INSTANCES_COLOUR_TODAY_ID)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "SYSTEM_ACCENT,$SYSTEM_ACCENT_DARK_THEME_INSTANCES_COLOUR_ID,$SYSTEM_ACCENT_LIGHT_THEME_INSTANCES_COLOUR_ID",
        "CYAN,$CYAN_INSTANCES_COLOUR_ID,$CYAN_INSTANCES_COLOUR_ID",
        "MINT,$MINT_INSTANCES_COLOUR_ID,$MINT_INSTANCES_COLOUR_ID",
        "BLUE,$BLUE_INSTANCES_COLOUR_ID,$BLUE_INSTANCES_COLOUR_ID",
        "GREEN,$GREEN_INSTANCES_COLOUR_ID,$GREEN_INSTANCES_COLOUR_ID",
        "YELLOW,$YELLOW_INSTANCES_COLOUR_ID,$YELLOW_INSTANCES_COLOUR_ID",
        "BLACK,$BLACK_INSTANCES_COLOUR_ID,$BLACK_INSTANCES_COLOUR_ID",
        "WHITE,$WHITE_INSTANCES_COLOUR_ID,$WHITE_INSTANCES_COLOUR_ID"
    )
    fun getInstancesColour_shouldReturnThemedInstancesColourWhenIsNotToday(
        colour: Colour,
        expectedDarkThemeColour: Int,
        expectedLightThemeColour: Int
    ) {
        val darkThemeInstancesColour = colour.getInstancesColour(false, Theme.DARK)
        val lightThemeInstancesColour = colour.getInstancesColour(false, Theme.LIGHT)

        assertThat(darkThemeInstancesColour).isEqualTo(expectedDarkThemeColour)
        assertThat(lightThemeInstancesColour).isEqualTo(expectedLightThemeColour)
    }

    @ParameterizedTest
    @CsvSource(
        "1,false",
        "28,false",
        "29,false",
        "30,false",
        "31,true",
        "32,true",
        "99,true"
    )
    fun systemAccentColourAvailability_shouldDependOnSystemSDK(
        sdkVersion: Int,
        expectedAvailability: Boolean
    ) {
        mockGetRuntimeSDK(sdkVersion)

        val result = Colour.SYSTEM_ACCENT.isAvailable()

        assertThat(result).isEqualTo(expectedAvailability)
        verify { SystemResolver.getRuntimeSDK() }
    }
}
