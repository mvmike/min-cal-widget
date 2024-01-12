// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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
    @CsvSource(
        "0,3,1,0.500f",
        "1,3,1,0.513f",
        "5,3,1,0.565f",
        "10,3,1,0.630f",
        "15,3,1,0.695f",
        "17,3,1,0.721f",
        "20,3,1,0.760f",
        "23,3,1,0.799f",
        "24,3,1,0.812f",
        "25,${Int.MAX_VALUE},3,0.825f",
        "30,${Int.MAX_VALUE},3,0.890f",
        "35,${Int.MAX_VALUE},3,0.955f",
        "40,${Int.MAX_VALUE},3,1.020f",
        "45,${Int.MAX_VALUE},3,1.085f",
        "50,${Int.MAX_VALUE},3,1.150f",
        "55,${Int.MAX_VALUE},3,1.215f",
        "60,${Int.MAX_VALUE},3,1.280f",
        "65,${Int.MAX_VALUE},3,1.345f",
        "68,${Int.MAX_VALUE},3,1.384f",
        "70,${Int.MAX_VALUE},3,1.410f",
        "75,${Int.MAX_VALUE},3,1.475f",
        "80,${Int.MAX_VALUE},3,1.540f",
        "85,${Int.MAX_VALUE},3,1.605f",
        "90,${Int.MAX_VALUE},3,1.670f",
        "95,${Int.MAX_VALUE},3,1.735f",
        "99,${Int.MAX_VALUE},3,1.787f",
        "100,${Int.MAX_VALUE},3,1.800"
    )
    fun getAlphaInHex(
        percentage: Int,
        expectedMonthHeaderLabelLength: Int,
        expectedDayHeaderLabelLength: Int,
        expectedRelativeValue: Float
    ) {
        val result = TextSize(percentage)

        assertThat(result.monthHeaderLabelLength).isEqualTo(expectedMonthHeaderLabelLength)
        assertThat(result.dayHeaderLabelLength).isEqualTo(expectedDayHeaderLabelLength)
        assertThat(result.relativeValue).isEqualTo(expectedRelativeValue)
    }
}