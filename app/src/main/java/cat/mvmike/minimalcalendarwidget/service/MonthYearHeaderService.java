// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.service;

import android.content.Context;
import android.widget.RemoteViews;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import cat.mvmike.minimalcalendarwidget.external.LocaleResolver;
import cat.mvmike.minimalcalendarwidget.external.SystemResolver;

public final class MonthYearHeaderService {

    private static final String MONTH_FORMAT = "MMMM";

    private static final String YEAR_FORMAT = "yyyy";

    private static final float HEADER_RELATIVE_YEAR_SIZE = 0.7f;

    public static void setMonthYearHeader(final Context context, final RemoteViews widgetRemoteView) {

        Locale locale = LocaleResolver.get().getSafeLocale(context);
        Instant systemInstant = SystemResolver.get().getInstant();

        String displayMonth = getMonthDisplayValue(getMonthFormatter(locale).format(systemInstant), locale);
        String displayYear = getYearFormatter(locale).format(systemInstant);

        SystemResolver.get().createMonthYearHeader(widgetRemoteView, displayMonth + " " + displayYear, HEADER_RELATIVE_YEAR_SIZE);
    }

    private static DateTimeFormatter getMonthFormatter(final Locale locale) {
        return DateTimeFormatter
            .ofPattern(MONTH_FORMAT)
            .withLocale(locale)
            .withZone(ZoneId.systemDefault());
    }

    private static DateTimeFormatter getYearFormatter(final Locale locale) {
        return DateTimeFormatter
            .ofPattern(YEAR_FORMAT)
            .withLocale(locale)
            .withZone(ZoneId.systemDefault());
    }

    private static String getMonthDisplayValue(final String month, final Locale locale) {
        String[] monthTokens = month.split(" ");
        String lastToken = monthTokens[monthTokens.length - 1];
        return lastToken.substring(0, 1).toUpperCase(locale)
            + lastToken.substring(1).toLowerCase(locale);

    }
}