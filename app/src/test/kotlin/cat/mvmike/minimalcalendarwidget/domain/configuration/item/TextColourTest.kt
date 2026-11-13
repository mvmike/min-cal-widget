// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.Colour
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource

internal class TextColourTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getTextColours")
    fun getInstancesColour_shouldAlwaysReturnTodayResourceWhenIsToday(textColour: TextColour) {
        val todayInstancesColours = Theme.entries
            .map { textColour.getInstancesColour(true, it) }
            .toSet()

        assertThat(todayInstancesColours).containsExactly(Colour.Resource(R.color.instances_today))
    }

    @ParameterizedTest
    @MethodSource("getSystemAccentThemedInstancesColours")
    fun getInstancesColour_shouldReturnThemedSystemAccentWhenIsNotToday(
        theme: Theme,
        expectedColourResource: Int
    ) {
        val result = TextColour.SystemAccent.getInstancesColour(false, theme)

        assertThat(result).isEqualTo(Colour.Resource(expectedColourResource))
    }

    @ParameterizedTest
    @CsvSource(
        "#FF00CCCC",
        "#FFFF0000"
    )
    fun getInstancesColour_shouldReturnHexColourForCustomHexWhenIsNotToday(hexColour: String) {
        Theme.entries.forEach { theme ->
            val result = TextColour.CustomHex(hexColour).getInstancesColour(false, theme)
            assertThat(result).isEqualTo(Colour.Hex(hexColour))
        }
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
    fun isSystemAccentAvailable_shouldDependOnSystemSDK(
        sdkVersion: Int,
        expectedAvailability: Boolean
    ) {
        mockGetRuntimeSDK(sdkVersion)

        val result = isSystemAccentAvailable()

        assertThat(result).isEqualTo(expectedAvailability)
        verifyGetRuntimeSDK()
    }

    @ParameterizedTest
    @CsvSource(
        "30,false",
        "31,true"
    )
    fun defaultTextColour_shouldReturnSystemAccentOnlyWhenAvailable(
        sdkVersion: Int,
        isSystemAccentAvailable: Boolean
    ) {
        mockGetRuntimeSDK(sdkVersion)

        val result = defaultTextColour()

        assertThat(result).isEqualTo(
            when {
                isSystemAccentAvailable -> TextColour.SystemAccent
                else -> TextColour.CustomHex("#FF00CCCC")
            }
        )
        verifyGetRuntimeSDK()
    }

    @ParameterizedTest
    @CsvSource(
        "31,,SYSTEM_ACCENT",
        "30,,#FF00CCCC",
        "31,SYSTEM_ACCENT,SYSTEM_ACCENT",
        "30,SYSTEM_ACCENT,#FF00CCCC",
        "31,CYAN,#FF00CCCC",
        "31,MINT,#FF7EFF7E",
        "31,BLUE,#FF003BFF",
        "31,GREEN,#FF007E00",
        "31,YELLOW,#FFFFFF00",
        "31,BLACK,#FF000000",
        "31,WHITE,#FFFFFFFF",
        "31,#FF123456,#FF123456",
        "31,#123456,#123456",
        "31,INVALID_HEX,SYSTEM_ACCENT",
        "30,INVALID_HEX,#FF00CCCC"
    )
    fun textColourFromStoredValue_shouldResolveSystemAccentLegacyCustomAndFallbackValues(
        sdkVersion: Int,
        storedValue: String?,
        expectedStoredValue: String
    ) {
        mockGetRuntimeSDK(sdkVersion)

        val result = textColourFromStoredValue(storedValue)

        assertThat(result.getStoredValue()).isEqualTo(expectedStoredValue)
        if (storedValue !in setOf("CYAN", "MINT", "BLUE", "GREEN", "YELLOW", "BLACK", "WHITE", "#FF123456", "#123456")) {
            verifyGetRuntimeSDK()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "some colour,Some colour"
    )
    fun getDisplayValue_shouldReturnCapitalisedTranslationForSystemAccent(
        rawTranslation: String,
        expectedDisplayValue: String
    ) {
        every { context.getString(R.string.system_accent) } returns rawTranslation

        val result = TextColour.SystemAccent.getDisplayValue(context)

        assertThat(result).isEqualTo(expectedDisplayValue)
        verify { context.getString(R.string.system_accent) }
    }

    @ParameterizedTest
    @CsvSource(
        "#ff00cccc,#FF00CCCC",
        "#FF12AB34,#FF12AB34"
    )
    fun getDisplayValue_shouldReturnUppercasedHexForCustomHex(
        hexColour: String,
        expectedDisplayValue: String
    ) {
        val result = TextColour.CustomHex(hexColour).getDisplayValue(context)

        assertThat(result).isEqualTo(expectedDisplayValue)
    }

    private fun getTextColours() = listOf(
        TextColour.SystemAccent,
        TextColour.CustomHex("#FF00CCCC")
    )

    private fun getSystemAccentThemedInstancesColours() = listOf(
        of(Theme.DARK, R.color.instances_system_accent_light),
        of(Theme.LIGHT, R.color.instances_system_accent_dark)
    )
}