// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.configuration.item;

import cat.mvmike.minimalcalendarwidget.R;

public enum Colour {

    CYAN(R.color.instances_cyan),

    MINT(R.color.instances_mint),

    BLUE(R.color.instances_blue),

    GREEN(R.color.instances_green),

    YELLOW(R.color.instances_yellow),

    WHITE(R.color.instances_white);

    private final int hexValue;

    Colour(final int hexValue) {
        this.hexValue = hexValue;
    }

    public int getHexValue() {
        return hexValue;
    }
}
