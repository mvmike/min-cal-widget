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

    private int today;

    private int todayYear;

    private int thisMonth;

    private int thisYear;

    private Calendar calendar;

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
}
