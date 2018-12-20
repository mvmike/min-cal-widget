package cat.mvmike.minimalcalendarwidget.service.enums;

import java.util.Locale;

public enum DayOfWeek {

    MONDAY,

    TUESDAY,

    WEDNESDAY,

    THURSDAY,

    FRIDAY,

    SATURDAY,

    SUNDAY;

    public String getHeaderName() {
        return this.name().substring(0, 3).toUpperCase(Locale.ENGLISH);
    }
}
