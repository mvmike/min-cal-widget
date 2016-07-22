// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.calendarwidgetminimal.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.Context;
import android.widget.RemoteViews;

import cat.mvmike.calendarwidgetminimal.R;

public abstract class WeekDayHeaderUtil {

    public static void setCellHeaderWeekDays(final RemoteViews headerRowRv, final int firstDayOfWeek, final Context context) {

        DateFormatSymbols dfs = DateFormatSymbols.getInstance();
        String[] weekdays = dfs.getShortWeekdays();

        int current;
        for (int i = 0; i < 7; i++) {

            current = (firstDayOfWeek + i) % 7 == 0 ? firstDayOfWeek + i : (firstDayOfWeek + i) % 7;

            if (current == Calendar.SATURDAY)
                headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[current], R.layout.cell_header_saturday));
            else if (current == Calendar.SUNDAY)
                headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[current], R.layout.cell_header_sunday));
            else
                headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[current], R.layout.cell_header));
        }
    }

    private static RemoteViews setSpecificWeekDay(final Context context, final String text, final int layoutId) {
        RemoteViews dayRv = new RemoteViews(context.getPackageName(), layoutId);
        dayRv.setTextViewText(android.R.id.text1, text);
        return dayRv;
    }
}