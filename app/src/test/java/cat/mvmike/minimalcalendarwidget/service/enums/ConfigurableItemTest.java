// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.service.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

import static cat.mvmike.minimalcalendarwidget.service.enums.ConfigurableItem.getDisplayValue;
import static cat.mvmike.minimalcalendarwidget.service.enums.ConfigurableItem.getDisplayValues;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurableItemTest {

    private static void assertFirstLetterUpperCaseOthersLowercase(final String input) {
        assertTrue(Character.isUpperCase(input.charAt(0)));
        assertEquals(input.substring(1), input.substring(1).toLowerCase(Locale.ENGLISH));
    }

    @ParameterizedTest
    @ValueSource(classes = {Theme.class, Symbol.class, Colour.class})
    <T extends Enum<T>> void getDisplayValues_shouldReturnDisplayValuesArray(final Class<T> enumClass) {

        String[] displayValues = getDisplayValues(enumClass);

        assertEquals(displayValues.length, enumClass.getEnumConstants().length);
        for (String displayValue : displayValues) {
            assertFirstLetterUpperCaseOthersLowercase(displayValue);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"someRAndomInputT", "ALL_UPPER_CASE", "alllowercase"})
    void getDisplayValue_shouldReturnOnlyFirstLetterUpperCase(final String input) {
        assertFirstLetterUpperCaseOthersLowercase(getDisplayValue(input));
    }

    @ParameterizedTest
    @EnumSource(value = ConfigurableItem.class)
    void key_shouldReturnLowerCaseName(final ConfigurableItem configurableItem) {
        assertEquals(configurableItem.name().toLowerCase(Locale.ENGLISH), configurableItem.key());
    }
}
