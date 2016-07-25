// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.util;

public abstract class SymbolsUtil {

    private static final String INSTANCES_SYMBOLS_EMPTY = " ";

    private static final String ALL_SPACES = "\\s+";

    private static final String EMPTY = "";

    public static String[] getAllSymbolNames() {

        String[] result = new String[Symbols.values().length];

        for (int i = 0; i < Symbols.values().length; i++)
            result[i] = Symbols.values()[i].name().toLowerCase();

        return result;
    }

    public enum Symbols {

        MINIMAL("· ∶ ∴ ∷ ◇ ◈"), ROMAN("Ⅰ Ⅱ Ⅲ Ⅳ Ⅴ ∾"), CLASSIC("1 2 3 4 5 ◉");

        private String values;

        Symbols(final String values) {
            this.values = values;
        }

        public String getValues() {
            return values;
        }

        public Character[] getArray() {
            return toCharacterArray(INSTANCES_SYMBOLS_EMPTY + getValues().replaceAll(ALL_SPACES, EMPTY));
        }
    }

    private static Character[] toCharacterArray(final String symbols) {

        if (symbols == null)
            return null;

        int len = symbols.length();
        Character[] array = new Character[len];

        for (int i = 0; i < len; i++)
            array[i] = symbols.charAt(i);

        return array;
    }
}
