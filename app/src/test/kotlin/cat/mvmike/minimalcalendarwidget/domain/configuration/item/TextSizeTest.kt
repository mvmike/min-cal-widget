// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

internal class TextSizeTest : BaseTest() {

    @ParameterizedTest
    @ValueSource(ints = [Integer.MIN_VALUE, -1, 101, Integer.MAX_VALUE])
    fun constructorShouldNotAllowIntsOutsideRange(percentage: Int) {
        assertThrows<IllegalArgumentException> {
            TextSize(percentage)
        }
    }

    @ParameterizedTest
    @MethodSource("getTextSizeWithAlphaLimitsAndExpectedOutputs")
    fun getAlphaInHex(textSizeProperties: TextSizeTestProperties) {
        val result = textSizeProperties.getTextSize()

        assertThat(result.monthHeaderLabelLength).isEqualTo(textSizeProperties.monthHeaderLabelLength)
        assertThat(result.dayHeaderLabelLength).isEqualTo(textSizeProperties.dayHeaderLabelLength)
        assertThat(result.relativeValue).isEqualTo(textSizeProperties.relativeValue)
    }

    private fun getTextSizeWithAlphaLimitsAndExpectedOutputs() = listOf(
        TextSizeTestProperties(0, 3, 1, 0.500f),
        TextSizeTestProperties(1, 3, 1, 0.513f),
        TextSizeTestProperties(5, 3, 1, 0.565f),
        TextSizeTestProperties(10, 3, 1, 0.630f),
        TextSizeTestProperties(15, 3, 1, 0.695f),
        TextSizeTestProperties(17, 3, 1, 0.721f),
        TextSizeTestProperties(20, 3, 1, 0.760f),
        TextSizeTestProperties(23, 3, 1, 0.799f),
        TextSizeTestProperties(24, 3, 1, 0.812f),
        TextSizeTestProperties(25, Int.MAX_VALUE, 3, 0.825f),
        TextSizeTestProperties(30, Int.MAX_VALUE, 3, 0.890f),
        TextSizeTestProperties(35, Int.MAX_VALUE, 3, 0.955f),
        TextSizeTestProperties(40, Int.MAX_VALUE, 3, 1.020f),
        TextSizeTestProperties(45, Int.MAX_VALUE, 3, 1.085f),
        TextSizeTestProperties(50, Int.MAX_VALUE, 3, 1.150f),
        TextSizeTestProperties(55, Int.MAX_VALUE, 3, 1.215f),
        TextSizeTestProperties(60, Int.MAX_VALUE, 3, 1.280f),
        TextSizeTestProperties(65, Int.MAX_VALUE, 3, 1.345f),
        TextSizeTestProperties(68, Int.MAX_VALUE, 3, 1.384f),
        TextSizeTestProperties(70, Int.MAX_VALUE, 3, 1.410f),
        TextSizeTestProperties(75, Int.MAX_VALUE, 3, 1.475f),
        TextSizeTestProperties(80, Int.MAX_VALUE, 3, 1.540f),
        TextSizeTestProperties(85, Int.MAX_VALUE, 3, 1.605f),
        TextSizeTestProperties(90, Int.MAX_VALUE, 3, 1.670f),
        TextSizeTestProperties(95, Int.MAX_VALUE, 3, 1.735f),
        TextSizeTestProperties(99, Int.MAX_VALUE, 3, 1.787f),
        TextSizeTestProperties(100, Int.MAX_VALUE, 3, 1.800f)
    )

    internal data class TextSizeTestProperties(
        private val percentage: Int,
        val monthHeaderLabelLength: Int,
        val dayHeaderLabelLength: Int,
        val relativeValue: Float
    ) {
        fun getTextSize() = TextSize(percentage)
    }
}