// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.util;

import android.content.Context;
import android.widget.RemoteViews;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import cat.mvmike.minimalcalendarwidget.R;

public abstract class DayHeaderUtil {

    public static void setDayHeaders(final Context context, final RemoteViews widgetRv) {

        RemoteViews headerRowRv = new RemoteViews(context.getPackageName(), R.layout.row_header);

        int firstDayOfWeek = ConfigurationUtil.getStartWeekDay(context);
        DateFormatSymbols dfs = DateFormatSymbols.getInstance();
        String[] weekdays = dfs.getShortWeekdays();

        ThemesUtil.Theme theme = ConfigurationUtil.getTheme(context);

        for (int i = 0; i < Calendar.DAY_OF_WEEK; i++) {

            RemoteViews rv;
            int current = (firstDayOfWeek + i) % Calendar.DAY_OF_WEEK == 0 ? firstDayOfWeek + i : (firstDayOfWeek + i) % Calendar.DAY_OF_WEEK;

            int cellHeaderThemeId;
            switch (current) {

                case Calendar.SATURDAY:
                    cellHeaderThemeId = theme.getCellHeaderSaturday();
                    break;

                case Calendar.SUNDAY:
                    cellHeaderThemeId = theme.getCellHeaderSunday();
                    break;

                default:
                    cellHeaderThemeId = theme.getCellHeader();
            }

            rv = setSpecificWeekDay(context, weekdays[current], cellHeaderThemeId);
            headerRowRv.addView(R.id.row_container, rv);
        }

        widgetRv.addView(R.id.calendar_widget, headerRowRv);
    }

    private static RemoteViews setSpecificWeekDay(final Context context, final String text, final int layoutId) {
        RemoteViews dayRv = new RemoteViews(context.getPackageName(), layoutId);
        dayRv.setTextViewText(android.R.id.text1, text);
        return dayRv;
    }
}
