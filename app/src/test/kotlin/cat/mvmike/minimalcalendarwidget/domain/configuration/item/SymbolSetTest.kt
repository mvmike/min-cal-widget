// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource

internal class SymbolSetTest : BaseTest() {

    @ParameterizedTest
    @EnumSource(value = SymbolSet::class)
    fun get_shouldReturnEmptyCharacter_whenNoInstances(symbolSet: SymbolSet) {
        assertThat(symbolSet.get(0)).isEqualTo(' ')
    }

    @ParameterizedTest
    @CsvSource(
        "MINIMAL,1,·",
        "MINIMAL,6,◈",
        "MINIMAL,7,◈",
        "VERTICAL,4,⁞",
        "VERTICAL,5,|",
        "CIRCLES,1,◔",
        "CIRCLES,4,●",
        "CIRCLES,5,๑",
        "BLOCKS,1,▁",
        "BLOCKS,4,▄",
        "BLOCKS,9,▅",
        "NUMBERS,5,5",
        "NUMBERS,9,9",
        "NUMBERS,10,+",
        "NUMBERS,11,+",
        "ROMAN,2,Ⅱ",
        "ROMAN,10,Ⅹ",
        "ROMAN,11,∾",
        "ROMAN,100,∾",
        "BINARY,1,☱",
        "BINARY,8,※",
        "BINARY,9,※",
        "NONE,1,' '",
        "NONE,3,' '",
        "NONE,99,' '"
    )
    fun get_shouldReturnExpectedCharacter(
        symbolSet: SymbolSet,
        numberOfInstances: Int,
        expectedCharacter: Char
    ) {
        assertThat(symbolSet.get(numberOfInstances)).isEqualTo(expectedCharacter)
    }

    @ParameterizedTest
    @CsvSource(
        "MINIMAL,1.0",
        "VERTICAL,1.0",
        "CIRCLES,1.0",
        "BLOCKS,1.0",
        "NUMBERS,0.6",
        "ROMAN,0.6",
        "BINARY,0.8",
        "NONE,1.0"
    )
    fun relativeSize_shouldBeReducedForDenseSymbolSets(
        symbolSet: SymbolSet,
        expectedRelativeSize: Float
    ) {
        assertThat(symbolSet.relativeSize).isEqualTo(expectedRelativeSize)
    }

    @ParameterizedTest
    @CsvSource(
        "MINIMAL,· ∶ ∴ ∷ ◇ ◈",
        "VERTICAL,· ∶ ⁝ ⁞ |",
        "CIRCLES,◔ ◑ ◕ ● ๑",
        "BLOCKS,▁ ▂ ▃ ▄ ▅",
        "NUMBERS,1 2 3 4 5 6 7 8 9 +",
        "ROMAN,Ⅰ Ⅱ Ⅲ Ⅳ Ⅴ Ⅵ Ⅶ Ⅷ Ⅸ Ⅹ ∾",
        "BINARY,☱ ☲ ☳ ☴ ☵ ☶ ☷ ※",
        "NONE,' '"
    )
    fun getDisplayValue_shouldJoinSymbolsWithSpaces(
        symbolSet: SymbolSet,
        expectedDisplayValue: String
    ) {
        assertThat(symbolSet.getDisplayValue()).isEqualTo(expectedDisplayValue)
    }

    @Test
    fun getSymbolSetDisplayValues_shouldReturnDisplayValueOfEverySymbolSet() {
        assertThat(getSymbolSetDisplayValues()).containsExactly(
            "· ∶ ∴ ∷ ◇ ◈",
            "· ∶ ⁝ ⁞ |",
            "◔ ◑ ◕ ● ๑",
            "▁ ▂ ▃ ▄ ▅",
            "1 2 3 4 5 6 7 8 9 +",
            "Ⅰ Ⅱ Ⅲ Ⅳ Ⅴ Ⅵ Ⅶ Ⅷ Ⅸ Ⅹ ∾",
            "☱ ☲ ☳ ☴ ☵ ☶ ☷ ※",
            " "
        )
    }
}