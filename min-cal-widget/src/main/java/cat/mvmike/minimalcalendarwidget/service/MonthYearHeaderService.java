// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.service;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.RemoteViews;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import cat.mvmike.minimalcalendarwidget.R;

public final class MonthYearHeaderService {

    private static final String MONTH_FORMAT = "MMMM";

    private static final String YEAR_FORMAT = "yyyy";

    private static final String HEADER_DATE_FORMAT = MONTH_FORMAT + " " + YEAR_FORMAT;

    private static final float HEADER_RELATIVE_YEAR_SIZE = 0.7f;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
        .ofPattern(HEADER_DATE_FORMAT)
        .withLocale(Locale.ENGLISH)
        .withZone(ZoneId.systemDefault());

    public static SpannableString setMonthYearHeader(final RemoteViews widgetRemoteView) {

        String monthAndYear = DATE_TIME_FORMATTER.format(Instant.now());
        SpannableString ss = new SpannableString(monthAndYear);
        ss.setSpan(new RelativeSizeSpan(HEADER_RELATIVE_YEAR_SIZE), monthAndYear.length() - YEAR_FORMAT.length(), monthAndYear.length(), 0);

        widgetRemoteView.setTextViewText(R.id.month_year_label, ss);
        return ss;
    }
}