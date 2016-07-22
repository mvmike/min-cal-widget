// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.calendarwidgetminimal.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.Context;
import android.widget.RemoteViews;

import cat.mvmike.calendarwidgetminimal.R;

public abstract class WeekDayHeaderUtil {

    public static void setCellHeaderWeekDays(RemoteViews headerRowRv, Context context) {

        DateFormatSymbols dfs = DateFormatSymbols.getInstance();
        String[] weekdays = dfs.getShortWeekdays();

        // add 7 days - ORDER MATTERS!
        headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[Calendar.MONDAY], R.layout.cell_header));
        headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[Calendar.TUESDAY], R.layout.cell_header));
        headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[Calendar.WEDNESDAY], R.layout.cell_header));
        headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[Calendar.THURSDAY], R.layout.cell_header));
        headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[Calendar.FRIDAY], R.layout.cell_header));
        headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[Calendar.SATURDAY], R.layout.cell_header_saturday));
        headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[Calendar.SUNDAY], R.layout.cell_header_sunday));
    }

    private static RemoteViews setSpecificWeekDay(final Context context, final String text, final int layoutId) {
        RemoteViews dayRv = new RemoteViews(context.getPackageName(), layoutId);
        dayRv.setTextViewText(android.R.id.text1, text);
        return dayRv;
    }
}