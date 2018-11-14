// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.service.enums;

// https://unicode-table.com
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
    private final char[] symbolArray;

    Symbol(final float relativeSize, final String values) {

        this.relativeSize = relativeSize;
        this.symbolArray = values.replaceAll(ALL_SPACES, EMPTY).toCharArray();
    }

    public float getRelativeSize() {
        return relativeSize;
    }

    public String getSymbol(final int numOfInstances) {

        if (numOfInstances == 0) {
            return INSTANCES_SYMBOLS_EMPTY;
        }

        int max = symbolArray.length - 1;
        return String.valueOf(numOfInstances > max ? symbolArray[max] : symbolArray[numOfInstances - 1]);
    }
}
