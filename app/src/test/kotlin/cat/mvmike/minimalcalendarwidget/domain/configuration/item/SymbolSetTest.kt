// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class SymbolSetTest : BaseTest() {

    @ParameterizedTest
    @EnumSource(value = SymbolSet::class)
    fun get_shouldReturnEmptyCharacter_whenNoInstances(symbolSet: SymbolSet) {
        assertThat(symbolSet.get(0)).isEqualTo(' ')
    }

    @ParameterizedTest
    @MethodSource("getSymbolSetAndExpectedCharacter")
    fun get_shouldReturnExpectedCharacter(symbolSet: SymbolSet, numberOfInstances: Int, expectedCharacter: Char) {
        assertThat(symbolSet.get(numberOfInstances)).isEqualTo(expectedCharacter)
    }

    private fun getSymbolSetAndExpectedCharacter(): Stream<Arguments> = Stream.of(
        Arguments.of(SymbolSet.MINIMAL, 1, '·'),
        Arguments.of(SymbolSet.MINIMAL, 6, '◈'),
        Arguments.of(SymbolSet.MINIMAL, 7, '◈'),
        Arguments.of(SymbolSet.VERTICAL, 4, '⁞'),
        Arguments.of(SymbolSet.VERTICAL, 5, '|'),
        Arguments.of(SymbolSet.CIRCLES, 1, '◔'),
        Arguments.of(SymbolSet.CIRCLES, 4, '●'),
        Arguments.of(SymbolSet.CIRCLES, 5, '๑'),
        Arguments.of(SymbolSet.NUMBERS, 5, '5'),
        Arguments.of(SymbolSet.NUMBERS, 9, '9'),
        Arguments.of(SymbolSet.NUMBERS, 10, '+'),
        Arguments.of(SymbolSet.NUMBERS, 11, '+'),
        Arguments.of(SymbolSet.ROMAN, 2, 'Ⅱ'),
        Arguments.of(SymbolSet.ROMAN, 10, 'Ⅹ'),
        Arguments.of(SymbolSet.ROMAN, 11, '∾'),
        Arguments.of(SymbolSet.ROMAN, 100, '∾'),
        Arguments.of(SymbolSet.BINARY, 1, '☱'),
        Arguments.of(SymbolSet.BINARY, 8, '※'),
        Arguments.of(SymbolSet.BINARY, 9, '※')
    )
}