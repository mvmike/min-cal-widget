// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

internal class TransparencyTest : BaseTest() {

    @ParameterizedTest
    @ValueSource(ints = [Integer.MIN_VALUE, -1, 101, Integer.MAX_VALUE])
    fun constructorShouldNotAllowIntsOutsideRange(percentage: Int) {
        assertThrows<IllegalArgumentException> {
            Transparency(percentage)
        }
    }

    @ParameterizedTest
    @MethodSource("getTransparencyWithAlphaLimitsAndExpectedOutputs")
    fun getAlpha(transparencyProperties: TransparencyTestProperties) {
        val result = transparencyProperties.getTransparency()
            .getAlpha(transparencyProperties.transparencyRange)

        assertThat(result).isEqualTo(transparencyProperties.expectedAlpha)
    }

    @ParameterizedTest
    @MethodSource("getTransparencyWithAlphaLimitsAndExpectedOutputs")
    fun getAlphaInHex(transparencyProperties: TransparencyTestProperties) {
        val result = transparencyProperties.getTransparency()
            .getAlphaInHex(transparencyProperties.transparencyRange)

        assertThat(result).isEqualTo(transparencyProperties.expectedAlphaInHex)
    }

    private fun getTransparencyWithAlphaLimitsAndExpectedOutputs() = listOf(
        TransparencyTestProperties(0, TransparencyRange.COMPLETE, 255, "FF"),
        TransparencyTestProperties(0, TransparencyRange.MODERATE, 80, "50"),
        TransparencyTestProperties(0, TransparencyRange.LOW, 30, "1E"),
        TransparencyTestProperties(1, TransparencyRange.COMPLETE, 252, "FC"),
        TransparencyTestProperties(1, TransparencyRange.MODERATE, 79, "4F"),
        TransparencyTestProperties(1, TransparencyRange.LOW, 29, "1D"),
        TransparencyTestProperties(5, TransparencyRange.COMPLETE, 242, "F2"),
        TransparencyTestProperties(10, TransparencyRange.COMPLETE, 229, "E5"),
        TransparencyTestProperties(15, TransparencyRange.COMPLETE, 216, "D8"),
        TransparencyTestProperties(17, TransparencyRange.COMPLETE, 211, "D3"),
        TransparencyTestProperties(20, TransparencyRange.COMPLETE, 204, "CC"),
        TransparencyTestProperties(20, TransparencyRange.MODERATE, 64, "40"),
        TransparencyTestProperties(20, TransparencyRange.LOW, 24, "18"),
        TransparencyTestProperties(25, TransparencyRange.COMPLETE, 191, "BF"),
        TransparencyTestProperties(30, TransparencyRange.COMPLETE, 178, "B2"),
        TransparencyTestProperties(35, TransparencyRange.COMPLETE, 165, "A5"),
        TransparencyTestProperties(40, TransparencyRange.COMPLETE, 153, "99"),
        TransparencyTestProperties(45, TransparencyRange.COMPLETE, 140, "8C"),
        TransparencyTestProperties(50, TransparencyRange.COMPLETE, 127, "7F"),
        TransparencyTestProperties(55, TransparencyRange.COMPLETE, 114, "72"),
        TransparencyTestProperties(60, TransparencyRange.COMPLETE, 102, "66"),
        TransparencyTestProperties(60, TransparencyRange.MODERATE, 32, "20"),
        TransparencyTestProperties(60, TransparencyRange.LOW, 12, "0C"),
        TransparencyTestProperties(65, TransparencyRange.COMPLETE, 89, "59"),
        TransparencyTestProperties(68, TransparencyRange.COMPLETE, 81, "51"),
        TransparencyTestProperties(70, TransparencyRange.COMPLETE, 76, "4C"),
        TransparencyTestProperties(70, TransparencyRange.MODERATE, 24, "18"),
        TransparencyTestProperties(70, TransparencyRange.LOW, 9, "09"),
        TransparencyTestProperties(75, TransparencyRange.COMPLETE, 63, "3F"),
        TransparencyTestProperties(80, TransparencyRange.COMPLETE, 51, "33"),
        TransparencyTestProperties(85, TransparencyRange.COMPLETE, 38, "26"),
        TransparencyTestProperties(90, TransparencyRange.COMPLETE, 25, "19"),
        TransparencyTestProperties(90, TransparencyRange.MODERATE, 8, "08"),
        TransparencyTestProperties(90, TransparencyRange.LOW, 3, "03"),
        TransparencyTestProperties(95, TransparencyRange.COMPLETE, 12, "0C"),
        TransparencyTestProperties(95, TransparencyRange.MODERATE, 4, "04"),
        TransparencyTestProperties(95, TransparencyRange.LOW, 1, "01"),
        TransparencyTestProperties(99, TransparencyRange.COMPLETE, 2, "02"),
        TransparencyTestProperties(100, TransparencyRange.COMPLETE, 0, "00"),
        TransparencyTestProperties(100, TransparencyRange.MODERATE, 0, "00"),
        TransparencyTestProperties(100, TransparencyRange.LOW, 0, "00")
    )

    internal data class TransparencyTestProperties(
        private val percentage: Int,
        val transparencyRange: TransparencyRange,
        val expectedAlpha: Int,
        val expectedAlphaInHex: String
    ) {
        fun getTransparency() = Transparency(percentage)
    }
}