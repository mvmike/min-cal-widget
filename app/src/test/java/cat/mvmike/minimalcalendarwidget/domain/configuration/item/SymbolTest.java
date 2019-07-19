package cat.mvmike.minimalcalendarwidget.domain.configuration.item;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SymbolTest {

    @ParameterizedTest
    @EnumSource(value = Symbol.class)
    void getSymbol_shouldReturnEmptyCharacterWhenNoInstances(final Symbol symbol) {
        assertEquals(" ", symbol.getSymbol(0));
    }

    @ParameterizedTest
    @MethodSource("getSymbolSetAndExpectedCharacter")
    void getSymbol_shouldReturnExpectedCharacter(final Symbol symbol, final int numberOfInstances, final char expectedCharacter) {
        assertEquals(String.valueOf(expectedCharacter), symbol.getSymbol(numberOfInstances));
    }

    private static Stream<Arguments> getSymbolSetAndExpectedCharacter() {
        return Stream.of(
            Arguments.of(Symbol.MINIMAL, 1, '·'),
            Arguments.of(Symbol.MINIMAL, 6, '◈'),
            Arguments.of(Symbol.MINIMAL, 7, '◈'),
            Arguments.of(Symbol.VERTICAL, 4, '⁞'),
            Arguments.of(Symbol.VERTICAL, 5, '|'),
            Arguments.of(Symbol.CIRCLES, 1, '◔'),
            Arguments.of(Symbol.CIRCLES, 4, '●'),
            Arguments.of(Symbol.CIRCLES, 5, '๑'),
            Arguments.of(Symbol.NUMBERS, 5, '5'),
            Arguments.of(Symbol.NUMBERS, 9, '9'),
            Arguments.of(Symbol.NUMBERS, 10, '+'),
            Arguments.of(Symbol.NUMBERS, 11, '+'),
            Arguments.of(Symbol.ROMAN, 2, 'Ⅱ'),
            Arguments.of(Symbol.ROMAN, 10, 'Ⅹ'),
            Arguments.of(Symbol.ROMAN, 11, '∾'),
            Arguments.of(Symbol.ROMAN, 100, '∾'),
            Arguments.of(Symbol.BINARY, 1, '☱'),
            Arguments.of(Symbol.BINARY, 8, '※'),
            Arguments.of(Symbol.BINARY, 9, '※')
        );
    }
}
