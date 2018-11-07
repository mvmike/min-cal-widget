// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.service.enums;

import java.util.Locale;

public enum Symbol {

    MINIMAL(1.2f, "· ∶ ∴ ∷ ◇ ◈"),

    VERTICAL(1.2f, "· ∶ ⁝ ⁞ |"),

    CIRCLES(1.2f, "◔ ◑ ◕ ● ๑"),

    NUMBERS(0.8f, "1 2 3 4 5 6 7 8 9 +"),

    ROMAN(0.8f, "Ⅰ Ⅱ Ⅲ Ⅳ Ⅴ Ⅵ Ⅶ Ⅷ Ⅸ Ⅹ ∾"),

    BINARY(1f, "☱ ☲ ☳ ☴ ☵ ☶ ☷ ※");

    private static final String INSTANCES_SYMBOLS_EMPTY = " ";
    private static final String ALL_SPACES = "\\s+";
    private static final String EMPTY = "";
    private final float relativeSize;
    private final String values;

    // https://unicode-table.com
    Symbol(final float relativeSize, final String values) {

        this.relativeSize = relativeSize;
        this.values = values;
    }

    public static String[] getAllSymbolNames() {

        String[] result = new String[Symbol.values().length];

        for (int i = 0; i < Symbol.values().length; i++) {
            String name = Symbol.values()[i].name();
            result[i] = name.substring(0, 1) + name.substring(1).toLowerCase(Locale.ENGLISH);
        }

        return result;
    }

    private static Character[] toCharacterArray(final String symbols) {

        if (symbols == null) {
            return null;
        }

        int len = symbols.length();
        Character[] array = new Character[len];

        for (int i = 0; i < len; i++) {
            array[i] = symbols.charAt(i);
        }

        return array;
    }

    public float getRelativeSize() {
        return relativeSize;
    }

    String getValues() {
        return values;
    }

    public Character[] getArray() {
        return toCharacterArray(INSTANCES_SYMBOLS_EMPTY + getValues().replaceAll(ALL_SPACES, EMPTY));
    }
}
