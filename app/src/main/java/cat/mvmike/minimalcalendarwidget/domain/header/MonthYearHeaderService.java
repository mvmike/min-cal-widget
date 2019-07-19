// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.header;

import android.content.Context;
import android.widget.RemoteViews;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver;

public final class MonthYearHeaderService {

    private static final String MONTH_FORMAT_NON_STANDALONE = "MMMM";

    private static final String MONTH_FORMAT_STANDALONE = "LLLL";

    // need to differ special standalone locales because of https://bugs.openjdk.java.net/browse/JDK-8114833
    private static final Set<String> LANGUAGES_WITH_STANDALONE_CASE = new HashSet<>(Collections.singletonList("ru"));

    private static final String YEAR_FORMAT = "yyyy";

    private static final float HEADER_RELATIVE_YEAR_SIZE = 0.7f;

    public static void setMonthYearHeader(final Context context, final RemoteViews widgetRemoteView) {

        Locale locale = SystemResolver.get().getLocale(context);
        Instant systemInstant = SystemResolver.get().getInstant();

        String displayMonth = getMonthDisplayValue(getMonthFormatter(locale).format(systemInstant), locale);
        String displayYear = getYearFormatter(locale).format(systemInstant);

        SystemResolver.get().createMonthYearHeader(widgetRemoteView, displayMonth + " " + displayYear, HEADER_RELATIVE_YEAR_SIZE);
    }

    private static DateTimeFormatter getMonthFormatter(final Locale locale) {
        return DateTimeFormatter
            .ofPattern(LANGUAGES_WITH_STANDALONE_CASE.contains(locale.getLanguage()) ?
                MONTH_FORMAT_STANDALONE : MONTH_FORMAT_NON_STANDALONE)
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