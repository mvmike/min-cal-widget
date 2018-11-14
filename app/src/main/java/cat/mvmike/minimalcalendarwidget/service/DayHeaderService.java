// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.service;

import android.content.Context;
import android.widget.RemoteViews;

import java.text.DateFormatSymbols;
import java.time.temporal.ChronoField;

import cat.mvmike.minimalcalendarwidget.R;
import cat.mvmike.minimalcalendarwidget.service.configuration.ConfigurationService;
import cat.mvmike.minimalcalendarwidget.service.enums.Theme;

public final class DayHeaderService {

    public static void setDayHeaders(final Context context, final RemoteViews widgetRv) {

        RemoteViews headerRowRv = new RemoteViews(context.getPackageName(), R.layout.row_header);

        int firstDayOfWeek = ConfigurationService.getStartWeekDay(context);
        DateFormatSymbols dfs = DateFormatSymbols.getInstance();
        String[] weekdays = dfs.getShortWeekdays();

        Theme theme = ConfigurationService.getTheme(context);

        for (int i = 0; i < ChronoField.DAY_OF_WEEK.range().getMaximum(); i++) {

            RemoteViews rv;
            int current = (firstDayOfWeek + i) % ChronoField.DAY_OF_WEEK.range().getMaximum() == 0 ?
                firstDayOfWeek + i : (int) ((firstDayOfWeek + i) % ChronoField.DAY_OF_WEEK.range().getMaximum());

            int cellHeaderThemeId;
            switch (current) {

                case 7:
                    cellHeaderThemeId = theme.getCellHeaderSaturday();
                    break;

                case 1:
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
