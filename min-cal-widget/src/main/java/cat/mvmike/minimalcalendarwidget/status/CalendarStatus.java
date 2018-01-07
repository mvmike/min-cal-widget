// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.status;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class CalendarStatus {

    private static final String PREF_MONTH = "month";

    private static final String PREF_YEAR = "year";

    private static final int MONTH_FIRST_DAY = 1;

    private static final int DAYS_IN_WEEK = 7;

    private static final int DECEMBER_LAST_DAY = 31;

    private final int today;

    private final int todayYear;

    private final int thisMonth;

    private final int thisYear;

    private final Calendar calendar;

    public CalendarStatus(final Context context, final Calendar cal, final int firstDayOfWeek) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        today = cal.get(Calendar.DAY_OF_YEAR);
        todayYear = cal.get(Calendar.YEAR);
        thisMonth = sp.getInt(PREF_MONTH, cal.get(Calendar.MONTH));
        thisYear = sp.getInt(PREF_YEAR, cal.get(Calendar.YEAR));

        cal.set(Calendar.DAY_OF_MONTH, MONTH_FIRST_DAY);
        cal.set(Calendar.MONTH, thisMonth);
        cal.set(Calendar.YEAR, thisYear);

        int monthStartDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DAY_OF_MONTH, firstDayOfWeek - monthStartDayOfWeek);

        // overlap month manually if dayOfMonth is in current month and greater than 1
        if (cal.get(Calendar.DAY_OF_MONTH) > MONTH_FIRST_DAY
                && cal.get(Calendar.DAY_OF_MONTH) < (DECEMBER_LAST_DAY / 2)) {
            cal.add(Calendar.DAY_OF_MONTH, -DAYS_IN_WEEK);
        }

        calendar = cal;
    }

    public int getToday() {
        return today;
    }

    public int getTodayYear() {
        return todayYear;
    }

    public int getThisMonth() {
        return thisMonth;
    }

    public int getThisYear() {
        return thisYear;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public static boolean isMonthFirstDay(final Calendar cal) {
        return Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH)).equals(MONTH_FIRST_DAY);
    }
}