// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.configuration;

import java.util.Locale;

public enum ConfigurableItem {

    FIRST_DAY_OF_WEEK,

    THEME,

    INSTANCES_SYMBOLS,

    INSTANCES_SYMBOLS_COLOUR;

    public static String getDisplayValue(final String name) {

        return name.substring(0, 1).toUpperCase(Locale.ENGLISH)
            + name.substring(1).toLowerCase(Locale.ENGLISH);
    }

    public String key() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }
}
