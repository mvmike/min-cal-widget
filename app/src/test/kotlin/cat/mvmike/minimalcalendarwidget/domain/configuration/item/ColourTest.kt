// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource

internal const val instancesColourTodayId = 2131034188

internal const val cyanInstancesColourId = 2131034183
private const val mintInstancesColourId = 2131034185
private const val blueInstancesColourId = 2131034182
private const val greenInstancesColourId = 2131034184
private const val yellowInstancesColourId = 2131034190
private const val blackInstancesColourId = 2131034181
private const val whiteInstancesColourId = 2131034189

private const val systemAccentDarkThemeInstancesColourId = 2131034187
private const val systemAccentLightThemeInstancesColourId = 2131034186

internal class ColourTest : BaseTest() {

    @ParameterizedTest
    @EnumSource(value = Colour::class)
    fun getInstancesColour_shouldAlwaysReturnTheSameValue(colour: Colour){
        Theme.values().forEach {
            val todayInstancesColour = colour.getInstancesColour(true, it)
            assertThat(todayInstancesColour).isEqualTo(instancesColourTodayId)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "CYAN,$cyanInstancesColourId",
        "MINT,$mintInstancesColourId",
        "BLUE,$blueInstancesColourId",
        "GREEN,$greenInstancesColourId",
        "YELLOW,$yellowInstancesColourId",
        "BLACK,$blackInstancesColourId",
        "WHITE,$whiteInstancesColourId"
    )
    fun getHexValue_shouldReturnSameValueForAllThemes(colour: Colour, expectedColour: Int) {
        Theme.values().forEach {
            val hexValue = colour.getHexValue(it)
            assertThat(hexValue).isEqualTo(expectedColour)
        }
    }

    @Test
    fun getHexValue_shouldReturnDifferentValuesPerTheme() {
        val darkThemeSystemAccentColour = Colour.SYSTEM_ACCENT.getHexValue(Theme.DARK)
        val lightThemeSystemAccentColour = Colour.SYSTEM_ACCENT.getHexValue(Theme.LIGHT)

        assertThat(darkThemeSystemAccentColour).isEqualTo(systemAccentDarkThemeInstancesColourId)
        assertThat(lightThemeSystemAccentColour).isEqualTo(systemAccentLightThemeInstancesColourId)
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
    fun systemAccentColourAvailability_shouldDependOnSystemSDK(sdkVersion: Int, expectedAvailability: Boolean) {
        mockGetRuntimeSDK(sdkVersion)

        val result = Colour.SYSTEM_ACCENT.isAvailable()

        assertThat(result).isEqualTo(expectedAvailability)
        verify { SystemResolver.getRuntimeSDK() }
    }
}
