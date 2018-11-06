// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.util;

import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.widget.RemoteViews;

import java.util.Calendar;

import cat.mvmike.minimalcalendarwidget.R;

public abstract class MonthYearHeaderUtil {

    private static final String MONTH_FORMAT = "MMMM";

    private static final String YEAR_FORMAT = "yyyy";

    private static final String HEADER_DATE_FORMAT = MONTH_FORMAT + " " + YEAR_FORMAT;

    private static final float HEADER_RELATIVE_YEAR_SIZE = 0.7f;

    public static SpannableString setMonthYearHeader(final Calendar cal, final RemoteViews widgetRemoteView) {

        String monthAndYear = String.valueOf(DateFormat.format(HEADER_DATE_FORMAT, cal));
        SpannableString ss = new SpannableString(monthAndYear);
        ss.setSpan(new RelativeSizeSpan(HEADER_RELATIVE_YEAR_SIZE), monthAndYear.length() - YEAR_FORMAT.length(), monthAndYear.length(), 0);

        widgetRemoteView.setTextViewText(R.id.month_year_label, ss);
        return ss;
    }
}
