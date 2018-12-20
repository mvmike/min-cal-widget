// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.service;

import android.widget.RemoteViews;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import cat.mvmike.minimalcalendarwidget.external.SystemResolver;

public final class MonthYearHeaderService {

    private static final String MONTH_FORMAT = "MMMM";

    private static final String YEAR_FORMAT = "yyyy";

    private static final String HEADER_DATE_FORMAT = MONTH_FORMAT + " " + YEAR_FORMAT;

    private static final float HEADER_RELATIVE_YEAR_SIZE = 0.7f;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
        .ofPattern(HEADER_DATE_FORMAT)
        .withLocale(Locale.ENGLISH)
        .withZone(ZoneId.systemDefault());

    public static void setMonthYearHeader(final RemoteViews widgetRemoteView) {

        String monthAndYear = DATE_TIME_FORMATTER.format(SystemResolver.get().getInstant());
        SystemResolver.get().createMonthYearHeader(widgetRemoteView, monthAndYear, HEADER_RELATIVE_YEAR_SIZE);
    }
}