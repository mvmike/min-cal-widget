// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.configuration;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.DayOfWeek;

import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Symbol;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme;

import static android.content.Context.MODE_PRIVATE;
import static cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurableItem.FIRST_DAY_OF_WEEK;
import static cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurableItem.INSTANCES_SYMBOLS;
import static cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurableItem.INSTANCES_SYMBOLS_COLOUR;
import static cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurableItem.THEME;

public final class ConfigurationService {

    private static final String PREFERENCES_ID = "mincal_prefs";

    public static void clearConfiguration(final Context context) {
        getConfiguration(context).edit().clear().apply();
    }

    public static Theme getTheme(final Context context) {
        return Theme.valueOf(getEnumString(context, THEME, Theme.BLACK));
    }

    public static DayOfWeek getStartWeekDay(final Context context) {
        return DayOfWeek.valueOf(getEnumString(context, FIRST_DAY_OF_WEEK, DayOfWeek.MONDAY));
    }

    public static Symbol getInstancesSymbols(final Context context) {
        return Symbol.valueOf(getEnumString(context, INSTANCES_SYMBOLS, Symbol.MINIMAL));
    }

    public static Colour getInstancesSymbolsColours(final Context context) {
        return Colour.valueOf(getEnumString(context, INSTANCES_SYMBOLS_COLOUR, Colour.CYAN));
    }

    public static <T> void set(final Context context, final ConfigurableItem configurableItem, final T value) {
        getConfiguration(context).edit().putString(configurableItem.key(), ((Enum) value).name()).apply();
    }

    private static String getEnumString(final Context context, final ConfigurableItem configurableItem, final Enum defaultValue) {
        return getConfiguration(context).getString(configurableItem.key(), defaultValue.name());
    }

    private static SharedPreferences getConfiguration(final Context context) {
        return context.getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
    }

}
