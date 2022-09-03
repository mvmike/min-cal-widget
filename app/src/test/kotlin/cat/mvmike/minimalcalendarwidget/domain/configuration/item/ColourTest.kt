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

internal class ColourTest : BaseTest() {

    @ParameterizedTest
    @EnumSource(value = Colour::class, names = ["SYSTEM_ACCENT"], mode = EnumSource.Mode.EXCLUDE)
    fun getHexValue_shouldReturnSameValue(colour: Colour) {
        val darkThemeColour = colour.getHexValue(Theme.DARK)
        val lightThemeColour = colour.getHexValue(Theme.LIGHT)

        assertThat(darkThemeColour).isEqualTo(lightThemeColour)
    }

    @Test
    fun getHexValue_shouldReturnDifferentValue() {
        val darkThemeSystemAccentColour = Colour.SYSTEM_ACCENT.getHexValue(Theme.DARK)
        val lightThemeSystemAccentColour = Colour.SYSTEM_ACCENT.getHexValue(Theme.LIGHT)

        assertThat(darkThemeSystemAccentColour).isNotEqualTo(lightThemeSystemAccentColour)
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
    fun isAvailable_shouldDependOnSystemSDK(sdkVersion: Int, expectedAvailability: Boolean) {
        mockGetRuntimeSDK(sdkVersion)

        val result = Colour.SYSTEM_ACCENT.isAvailable()

        assertThat(result).isEqualTo(expectedAvailability)
        verify { SystemResolver.getRuntimeSDK() }
    }
}
