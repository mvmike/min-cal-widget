// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.header;

import android.content.Context;
import android.widget.RemoteViews;

import java.time.DayOfWeek;

import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme;
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver;

public final class DayHeaderService {

    public static void setDayHeaders(final Context context, final RemoteViews widgetRv) {

        RemoteViews headerRowRv = SystemResolver.get().createHeaderRow(context);

        int firstDayOfWeek = ConfigurationService.getStartWeekDay(context).ordinal();
        Theme theme = ConfigurationService.getTheme(context);

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {

            int currentOrdinal = dayOfWeek.ordinal();
            int newOrdinal = (firstDayOfWeek + currentOrdinal) < DayOfWeek.values().length ?
                firstDayOfWeek + currentOrdinal : (firstDayOfWeek + currentOrdinal) % DayOfWeek.values().length;

            DayOfWeek current = DayOfWeek.values()[newOrdinal];

            int cellHeaderThemeId;
            switch (current) {

                case SATURDAY:
                    cellHeaderThemeId = theme.getCellHeaderSaturday();
                    break;

                case SUNDAY:
                    cellHeaderThemeId = theme.getCellHeaderSunday();
                    break;

                default:
                    cellHeaderThemeId = theme.getCellHeader();
            }

            SystemResolver.get().addHeaderDayToHeader(context, headerRowRv,
                SystemResolver.get().getAbbreviatedDayOfWeekTranslated(context, current), cellHeaderThemeId);
        }

        SystemResolver.get().addHeaderRowToWidget(widgetRv, headerRowRv);
    }
}
