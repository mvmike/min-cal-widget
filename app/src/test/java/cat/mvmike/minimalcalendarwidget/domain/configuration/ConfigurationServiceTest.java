// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.DayOfWeek;

import cat.mvmike.minimalcalendarwidget.BaseTest;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Symbol;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme;

import static cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService.clearConfiguration;
import static cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService.getInstancesSymbols;
import static cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService.getInstancesSymbolsColours;
import static cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService.getStartWeekDay;
import static cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService.getTheme;
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
