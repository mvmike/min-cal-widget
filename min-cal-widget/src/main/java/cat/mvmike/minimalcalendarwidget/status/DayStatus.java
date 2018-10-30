// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.status;

import java.util.Calendar;

public final class DayStatus {

    private final boolean inMonth;

    private final boolean isToday;

    private final boolean isSaturday;

    private final boolean isSunday;

    private final int dayOfMonthInt;

    private final int monthNumberInt;

    public DayStatus(final Calendar cal, final int todayYear, final int thisMonth, final int today) {

        boolean inYear = cal.get(Calendar.YEAR) == todayYear;
        inMonth = cal.get(Calendar.MONTH) == thisMonth;
        isToday = inYear && inMonth && cal.get(Calendar.DAY_OF_YEAR) == today;

        isSaturday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
        isSunday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;

        dayOfMonthInt = cal.get(Calendar.DAY_OF_MONTH);
        monthNumberInt = cal.get(Calendar.MONTH);
    }

    public boolean isInMonth() {
        return inMonth;
    }

    public boolean isToday() {
        return isToday;
    }

    public boolean isSaturday() {
        return isSaturday;
    }

    public boolean isSunday() {
        return isSunday;
    }

    public int getDayOfMonthInt() {
        return dayOfMonthInt;
    }

    public boolean isInDay(final Calendar startCalendar, final Calendar endCalendar) {

        return startCalendar.get(Calendar.MONTH) <= monthNumberInt && startCalendar.get(Calendar.DAY_OF_MONTH) <= dayOfMonthInt
            && endCalendar.get(Calendar.MONTH) >= monthNumberInt && endCalendar.get(Calendar.DAY_OF_MONTH) >= dayOfMonthInt;
    }
}
