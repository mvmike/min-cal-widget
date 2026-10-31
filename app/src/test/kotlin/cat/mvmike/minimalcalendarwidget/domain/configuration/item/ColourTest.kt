// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource

internal class ColourTest : BaseTest() {

    @ParameterizedTest
    @EnumSource(value = Colour::class)
    fun getInstancesColour_shouldAlwaysReturnTheSameValueWhenIsToday(colour: Colour) {
        val todayInstancesColours = Theme.entries
            .map { colour.getInstancesColour(true, it) }
            .toSet()

        assertThat(todayInstancesColours).containsExactly(R.color.instances_today)
    }

    @ParameterizedTest
    @MethodSource("getColoursWithExpectedThemedInstancesColours")
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
        verifyGetRuntimeSDK()
    }

    @ParameterizedTest
    @CsvSource(
        "30,false",
        "31,true"
    )
    fun getAvailableColours_shouldIncludeSystemAccentOnlyWhenAvailable(
        sdkVersion: Int,
        isSystemAccentAvailable: Boolean
    ) {
        mockGetRuntimeSDK(sdkVersion)

        val result = getAvailableColours()

        assertThat(result).containsExactlyElementsOf(
            listOfNotNull(
                Colour.SYSTEM_ACCENT.takeIf { isSystemAccentAvailable },
                Colour.CYAN,
                Colour.MINT,
                Colour.BLUE,
                Colour.GREEN,
                Colour.YELLOW,
                Colour.BLACK,
                Colour.WHITE
            )
        )
        verifyGetRuntimeSDK()
    }

    @ParameterizedTest
    @MethodSource("getColoursWithExpectedStringResource")
    fun getDisplayValue_shouldReturnCapitalisedTranslation(
        colour: Colour,
        stringResourceId: Int
    ) {
        every { context.getString(stringResourceId) } returns "some colour"

        val result = colour.getDisplayValue(context)

        assertThat(result).isEqualTo("Some colour")
        verify { context.getString(stringResourceId) }
    }

    private fun getColoursWithExpectedStringResource() = listOf(
        of(Colour.SYSTEM_ACCENT, R.string.system_accent),
        of(Colour.CYAN, R.string.cyan),
        of(Colour.MINT, R.string.mint),
        of(Colour.BLUE, R.string.blue),
        of(Colour.GREEN, R.string.green),
        of(Colour.YELLOW, R.string.yellow),
        of(Colour.BLACK, R.string.black),
        of(Colour.WHITE, R.string.white)
    )

    private fun getColoursWithExpectedThemedInstancesColours() = listOf(
        of(Colour.SYSTEM_ACCENT, R.color.instances_system_accent_light, R.color.instances_system_accent_dark),
        of(Colour.CYAN, R.color.instances_cyan, R.color.instances_cyan),
        of(Colour.MINT, R.color.instances_mint, R.color.instances_mint),
        of(Colour.BLUE, R.color.instances_blue, R.color.instances_blue),
        of(Colour.GREEN, R.color.instances_green, R.color.instances_green),
        of(Colour.YELLOW, R.color.instances_yellow, R.color.instances_yellow),
        of(Colour.BLACK, R.color.instances_black, R.color.instances_black),
        of(Colour.WHITE, R.color.instances_white, R.color.instances_white)
    )
}