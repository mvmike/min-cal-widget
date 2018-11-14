// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.service.configuration;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.DayOfWeek;

import cat.mvmike.minimalcalendarwidget.service.enums.Colour;
import cat.mvmike.minimalcalendarwidget.service.enums.ConfigurableItem;
import cat.mvmike.minimalcalendarwidget.service.enums.Symbol;
import cat.mvmike.minimalcalendarwidget.service.enums.Theme;

import static android.content.Context.MODE_PRIVATE;
import static cat.mvmike.minimalcalendarwidget.service.enums.ConfigurableItem.INSTANCES_SYMBOLS;
import static cat.mvmike.minimalcalendarwidget.service.enums.ConfigurableItem.INSTANCES_SYMBOLS_COLOUR;
import static cat.mvmike.minimalcalendarwidget.service.enums.ConfigurableItem.START_WEEK_DAY;
import static cat.mvmike.minimalcalendarwidget.service.enums.ConfigurableItem.THEME;

public final class ConfigurationService {

    private static final String PREFERENCES_ID = "mincal_prefs";

    public static void clearConfiguration(final Context context) {
        getConfiguration(context).edit().clear().apply();
    }

    public static Theme getTheme(final Context context) {
        return Theme.valueOf(getEnumString(context, THEME, Theme.BLACK));
    }

    public static int getStartWeekDay(final Context context) {
        return getConfiguration(context).getInt(START_WEEK_DAY.key(), DayOfWeek.MONDAY.getValue());
    }

    public static Symbol getInstancesSymbols(final Context context) {
        return Symbol.valueOf(getEnumString(context, INSTANCES_SYMBOLS, Symbol.MINIMAL));
    }

    public static Colour getInstancesSymbolsColours(final Context context) {
        return Colour.valueOf(getEnumString(context, INSTANCES_SYMBOLS_COLOUR, Colour.CYAN));
    }

    public static <T> void set(final Context context, final ConfigurableItem configurableItem, final T value) {

        switch (configurableItem) {

            case START_WEEK_DAY:
                getConfiguration(context).edit().putInt(configurableItem.key(), (Integer) value).apply();
                break;

            case THEME:
            case INSTANCES_SYMBOLS:
            case INSTANCES_SYMBOLS_COLOUR:
                getConfiguration(context).edit().putString(configurableItem.key(), ((Enum) value).name()).apply();

        }
    }

    private static String getEnumString(final Context context, final ConfigurableItem configurableItem, final Enum defaultValue) {
        return getConfiguration(context).getString(configurableItem.key(), defaultValue.name());
    }

    private static SharedPreferences getConfiguration(final Context context) {
        return context.getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
    }

}
