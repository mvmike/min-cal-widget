// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource

internal class SymbolSetTest : BaseTest() {

    @ParameterizedTest
    @EnumSource(value = SymbolSet::class)
    fun get_shouldReturnEmptyCharacter_whenNoInstances(symbolSet: SymbolSet) {
        assertThat(symbolSet.get(0)).isEqualTo(' ')
    }

    @ParameterizedTest
    @MethodSource("getSymbolSetAndExpectedCharacter")
    fun get_shouldReturnExpectedCharacter(
        symbolSet: SymbolSet,
        numberOfInstances: Int,
        expectedCharacter: Char
    ) {
        assertThat(symbolSet.get(numberOfInstances)).isEqualTo(expectedCharacter)
    }

    private fun getSymbolSetAndExpectedCharacter() = listOf(
        of(SymbolSet.MINIMAL, 1, '·'),
        of(SymbolSet.MINIMAL, 6, '◈'),
        of(SymbolSet.MINIMAL, 7, '◈'),
        of(SymbolSet.VERTICAL, 4, '⁞'),
        of(SymbolSet.VERTICAL, 5, '|'),
        of(SymbolSet.CIRCLES, 1, '◔'),
        of(SymbolSet.CIRCLES, 4, '●'),
        of(SymbolSet.CIRCLES, 5, '๑'),
        of(SymbolSet.NUMBERS, 5, '5'),
        of(SymbolSet.NUMBERS, 9, '9'),
        of(SymbolSet.NUMBERS, 10, '+'),
        of(SymbolSet.NUMBERS, 11, '+'),
        of(SymbolSet.ROMAN, 2, 'Ⅱ'),
        of(SymbolSet.ROMAN, 10, 'Ⅹ'),
        of(SymbolSet.ROMAN, 11, '∾'),
        of(SymbolSet.ROMAN, 100, '∾'),
        of(SymbolSet.BINARY, 1, '☱'),
        of(SymbolSet.BINARY, 8, '※'),
        of(SymbolSet.BINARY, 9, '※')
    )
}