// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.status;

import java.util.Calendar;

public final class DayStatus {

    private boolean inYear;

    private boolean inMonth;

    private boolean isToday;

    private boolean isSaturday;

    private boolean isSunday;

    private int dayOfMonthInt;

    private int monthNumberInt;

    public DayStatus(final Calendar cal, final int todayYear, final int thisMonth, final int today) {

        inYear = cal.get(Calendar.YEAR) == todayYear;
        inMonth = cal.get(Calendar.MONTH) == thisMonth;
        isToday = inYear && inMonth && (cal.get(Calendar.DAY_OF_YEAR) == today);

        isSaturday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
        isSunday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;

        dayOfMonthInt = cal.get(Calendar.DAY_OF_MONTH);
        monthNumberInt = cal.get(Calendar.MONTH);
    }

    public boolean isInYear() {
        return inYear;
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

    public int getMonthNumberInt() {
        return monthNumberInt;
    }

    public boolean isInDay(final Calendar startCalendar, final Calendar endCalendar) {

        return startCalendar.get(Calendar.MONTH) <= monthNumberInt && startCalendar.get(Calendar.DAY_OF_MONTH) <= dayOfMonthInt
            && endCalendar.get(Calendar.MONTH) >= monthNumberInt && endCalendar.get(Calendar.DAY_OF_MONTH) >= dayOfMonthInt;
    }
}
