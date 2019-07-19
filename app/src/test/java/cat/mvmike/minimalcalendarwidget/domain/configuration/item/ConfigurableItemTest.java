// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.configuration.item;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurableItem;

import static cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurableItem.getDisplayValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurableItemTest {

    @ParameterizedTest
    @ValueSource(strings = {"someRAndOmInputT", "ALL_UPPER_CASE", "alllowercase"})
    void getDisplayValue_shouldReturnOnlyFirstLetterUpperCase(final String input) {
        assertFirstLetterUpperCaseOthersLowercase(getDisplayValue(input));
    }

    @ParameterizedTest
    @EnumSource(value = ConfigurableItem.class)
    void key_shouldReturnLowerCaseName(final ConfigurableItem configurableItem) {
        assertEquals(configurableItem.name().toLowerCase(Locale.ENGLISH), configurableItem.key());
    }

    private static void assertFirstLetterUpperCaseOthersLowercase(final String input) {
        assertTrue(Character.isUpperCase(input.charAt(0)));
        assertEquals(input.substring(1), input.substring(1).toLowerCase(Locale.ENGLISH));
    }
}
