// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

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

    private fun getTextSizeWithAlphaLimitsAndExpectedOutputs(): Stream<TextSizeTestProperties> = Stream.of(
        TextSizeTestProperties(0, 3, 1, 0.500f),
        TextSizeTestProperties(1, 3, 1, 0.515f),
        TextSizeTestProperties(5, 3, 1, 0.575f),
        TextSizeTestProperties(10, 3, 1, 0.650f),
        TextSizeTestProperties(15, 3, 1, 0.725f),
        TextSizeTestProperties(17, 3, 1, 0.755f),
        TextSizeTestProperties(20, 3, 1, 0.800f),
        TextSizeTestProperties(29, 3, 1, 0.935f),
        TextSizeTestProperties(30, Int.MAX_VALUE, 3, 0.950f),
        TextSizeTestProperties(31, Int.MAX_VALUE, 3, 0.965f),
        TextSizeTestProperties(40, Int.MAX_VALUE, 3, 1.100f),
        TextSizeTestProperties(45, Int.MAX_VALUE, 3, 1.175f),
        TextSizeTestProperties(50, Int.MAX_VALUE, 3, 1.250f),
        TextSizeTestProperties(55, Int.MAX_VALUE, 3, 1.325f),
        TextSizeTestProperties(60, Int.MAX_VALUE, 3, 1.400f),
        TextSizeTestProperties(65, Int.MAX_VALUE, 3, 1.475f),
        TextSizeTestProperties(68, Int.MAX_VALUE, 3, 1.520f),
        TextSizeTestProperties(70, Int.MAX_VALUE, 3, 1.550f),
        TextSizeTestProperties(75, Int.MAX_VALUE, 3, 1.625f),
        TextSizeTestProperties(80, Int.MAX_VALUE, 3, 1.700f),
        TextSizeTestProperties(85, Int.MAX_VALUE, 3, 1.775f),
        TextSizeTestProperties(90, Int.MAX_VALUE, 3, 1.850f),
        TextSizeTestProperties(95, Int.MAX_VALUE, 3, 1.925f),
        TextSizeTestProperties(99, Int.MAX_VALUE, 3, 1.985f),
        TextSizeTestProperties(100, Int.MAX_VALUE, 3, 2.000f),
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