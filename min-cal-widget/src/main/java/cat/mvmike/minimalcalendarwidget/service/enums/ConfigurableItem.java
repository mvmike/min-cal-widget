// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.service.enums;

import java.util.Locale;

public enum ConfigurableItem {

    START_WEEK_DAY,

    THEME,

    INSTANCES_SYMBOLS,

    INSTANCES_SYMBOLS_COLOUR;

    public String key() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }
}
