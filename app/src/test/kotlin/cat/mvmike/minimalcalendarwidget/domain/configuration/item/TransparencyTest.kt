// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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
    @CsvSource(
        "0,COMPLETE,255",
        "0,MODERATE,80",
        "0,LOW,30",
        "1,COMPLETE,252",
        "1,MODERATE,79",
        "1,LOW,29",
        "5,COMPLETE,242",
        "20,COMPLETE,204",
        "20,MODERATE,64",
        "20,LOW,24",
        "25,COMPLETE,191",
        "65,COMPLETE,89",
        "70,COMPLETE,76",
        "70,MODERATE,24",
        "70,LOW,9",
        "75,COMPLETE,63",
        "95,LOW,1",
        "99,COMPLETE,2",
        "100,COMPLETE,0",
        "100,MODERATE,0",
        "100,LOW,0"
    )
    fun getAlpha(
        transparencyPercentage: Int,
        transparencyRange: TransparencyRange,
        expectedAlpha: Int
    ) {
        val result = Transparency(transparencyPercentage)
            .getAlpha(transparencyRange)

        assertThat(result).isEqualTo(expectedAlpha)
    }

    @ParameterizedTest
    @CsvSource(
        "0,COMPLETE,FF",
        "0,MODERATE,50",
        "0,LOW,1E",
        "1,COMPLETE,FC",
        "1,MODERATE,4F",
        "1,LOW,1D",
        "5,COMPLETE,F2",
        "20,COMPLETE,CC",
        "20,MODERATE,40",
        "20,LOW,18",
        "25,COMPLETE,BF",
        "65,COMPLETE,59",
        "70,COMPLETE,4C",
        "70,MODERATE,18",
        "70,LOW,09",
        "75,COMPLETE,3F",
        "95,LOW,01",
        "99,COMPLETE,02",
        "100,COMPLETE,00",
        "100,MODERATE,00",
        "100,LOW,00"
    )
    fun getAlphaInHex(
        transparencyPercentage: Int,
        transparencyRange: TransparencyRange,
        expectedAlphaInHex: String
    ) {
        val result = Transparency(transparencyPercentage)
            .getAlphaInHex(transparencyRange)

        assertThat(result).isEqualTo(expectedAlphaInHex)
    }
}