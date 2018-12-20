// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import cat.mvmike.minimalcalendarwidget.BaseTest;
import cat.mvmike.minimalcalendarwidget.service.enums.Colour;
import cat.mvmike.minimalcalendarwidget.service.enums.DayOfWeek;
import cat.mvmike.minimalcalendarwidget.service.enums.Symbol;
import cat.mvmike.minimalcalendarwidget.service.enums.Theme;

import static cat.mvmike.minimalcalendarwidget.service.ConfigurationService.clearConfiguration;
import static cat.mvmike.minimalcalendarwidget.service.ConfigurationService.getInstancesSymbols;
import static cat.mvmike.minimalcalendarwidget.service.ConfigurationService.getInstancesSymbolsColours;
import static cat.mvmike.minimalcalendarwidget.service.ConfigurationService.getStartWeekDay;
import static cat.mvmike.minimalcalendarwidget.service.ConfigurationService.getTheme;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ConfigurationServiceTest extends BaseTest {

    @Test
    void clearConfiguration_shouldRemoveAllApplicationPreferences() {

        clearConfiguration(context);

        verify(editor, times(1)).clear();
        verify(editor, times(1)).apply();
        verifyNoMoreInteractions(editor);
    }

    @ParameterizedTest
    @EnumSource(value = Theme.class)
    void getTheme_shouldReturnSharedPreferencesValue(final Theme theme) {

        mockTheme(sharedPreferences, theme);

        assertEquals(theme, getTheme(context));
        verifyNoMoreInteractions(editor);
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class)
    void getStartWeekDay_shouldReturnSharedPreferencesValue(final DayOfWeek dayOfWeek) {

        mockStartWeekDay(sharedPreferences, dayOfWeek);

        assertEquals(dayOfWeek, getStartWeekDay(context));
        verifyNoMoreInteractions(editor);
    }

    @ParameterizedTest
    @EnumSource(value = Symbol.class)
    void getInstancesSymbols_shouldReturnSharedPreferencesValue(final Symbol symbol) {

        mockInstancesSymbols(sharedPreferences, symbol);

        assertEquals(symbol, getInstancesSymbols(context));
        verifyNoMoreInteractions(editor);
    }

    @ParameterizedTest
    @EnumSource(value = Colour.class)
    void getInstancesSymbolsColours_shouldReturnSharedPreferencesValue(final Colour colour) {

        mockInstancesSymbolsColour(sharedPreferences, colour);

        assertEquals(colour, getInstancesSymbolsColours(context));
        verifyNoMoreInteractions(editor);
    }
}
