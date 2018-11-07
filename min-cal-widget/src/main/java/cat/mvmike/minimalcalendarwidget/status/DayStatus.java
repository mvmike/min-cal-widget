// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.status;

import java.util.Calendar;

public final class DayStatus {

    private final boolean inMonth;

    private final boolean today;

    private final boolean saturday;

    private final boolean sunday;

    private final int dayOfMonth;

    private final int month;

    public DayStatus(final Calendar cal, final int todayYear, final int thisMonth, final int thisDay) {

        boolean inYear = cal.get(Calendar.YEAR) == todayYear;
        inMonth = cal.get(Calendar.MONTH) == thisMonth;
        today = inYear && inMonth && cal.get(Calendar.DAY_OF_YEAR) == thisDay;

        saturday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
        sunday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;

        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
    }

    public boolean isInMonth() {
        return inMonth;
    }

    public boolean isToday() {
        return today;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public boolean isInDay(final Calendar startCalendar, final Calendar endCalendar) {

        return startCalendar.get(Calendar.MONTH) <= month && startCalendar.get(Calendar.DAY_OF_MONTH) <= dayOfMonth
            && endCalendar.get(Calendar.MONTH) >= month && endCalendar.get(Calendar.DAY_OF_MONTH) >= dayOfMonth;
    }
}
