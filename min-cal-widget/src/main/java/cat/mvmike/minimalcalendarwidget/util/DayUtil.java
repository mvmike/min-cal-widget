// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.util;

import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.RemoteViews;

import cat.mvmike.minimalcalendarwidget.R;
import cat.mvmike.minimalcalendarwidget.resolver.dto.InstanceDTO;
import cat.mvmike.minimalcalendarwidget.status.CalendarStatus;
import cat.mvmike.minimalcalendarwidget.status.DayStatus;

public abstract class DayUtil {

    private static final int NUM_WEEKS = 6;

    private static final Character[] INSTANCES_NUMBER_SYMBOLS = {' ', '·', '∶', '∴', '∷', '◇', '◈'};

    private static final float SYMBOL_RELATIVE_SIZE = 1.2f;

    private static final String PADDING = " ";

    private static final String DOUBLE_PADDING = PADDING + PADDING;

    public static void setDays(final Context context, final Calendar cal, final int firstDayOfWeek, final SpannableString ss,
        final RemoteViews rv, final Set<InstanceDTO> instanceSet) {

        CalendarStatus cs = new CalendarStatus(context, cal, firstDayOfWeek);

        RemoteViews rowRv;
        for (int week = 0; week < NUM_WEEKS; week++) {

            rowRv = new RemoteViews(context.getPackageName(), R.layout.row_week);

            DayStatus ds;
            for (int day = 0; day < Calendar.DAY_OF_WEEK; day++) {

                ds = new DayStatus(cal, cs.getTodayYear(), cs.getThisMonth(), cs.getToday());

                int cellLayoutResId = getDayLayout(ds);
                RemoteViews cellRv = new RemoteViews(context.getPackageName(), cellLayoutResId);

                int numberOfInstances = getNumberOfInstances(instanceSet, ds);
                setInstanceNumber(context, cellRv, Integer.toString(ds.getDayOfMonthInt()), ds.isToday(), ds.isInMonth(),
                    numberOfInstances);
                checkMonthBeginningStyleChange(cs.getCalendar(), cellRv, ss);

                cs.getCalendar().add(Calendar.DAY_OF_MONTH, 1);

                rowRv.addView(R.id.row_container, cellRv);
            }
            rv.addView(R.id.calendar_widget, rowRv);
        }
    }

    private static void setInstanceNumber(final Context context, final RemoteViews cellRv, final String dayOfMonth, boolean isToday,
        boolean isInMonth, final int found) {

        int max = INSTANCES_NUMBER_SYMBOLS.length - 1;
        String symbol = String.valueOf(found > max ? INSTANCES_NUMBER_SYMBOLS[max] : INSTANCES_NUMBER_SYMBOLS[found]);
        String dayOfMonthSS = PADDING + (dayOfMonth.length() == 1 ? dayOfMonth + DOUBLE_PADDING : dayOfMonth) + PADDING + symbol;
        SpannableString daySS = new SpannableString(dayOfMonthSS);
        daySS.setSpan(new StyleSpan(Typeface.BOLD), dayOfMonthSS.length() - 1, dayOfMonthSS.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int color;
        if (isToday)
            color = ContextCompat.getColor(context, R.color.instances_today);
        else if (isInMonth)
            color = ContextCompat.getColor(context, R.color.instances_this_month);
        else
            color = ContextCompat.getColor(context, R.color.instances);

        if (isToday)
            daySS.setSpan(new StyleSpan(Typeface.BOLD), 0, dayOfMonthSS.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        daySS.setSpan(new ForegroundColorSpan(color), dayOfMonthSS.length() - 1, dayOfMonthSS.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        daySS.setSpan(new RelativeSizeSpan(SYMBOL_RELATIVE_SIZE), dayOfMonthSS.length() - 1, dayOfMonthSS.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        cellRv.setTextViewText(android.R.id.text1, daySS);
    }

    private static int getDayLayout(final DayStatus ds) {

        int cellLayoutResId = R.layout.cell_day;

        if (ds.isInMonth())
            cellLayoutResId = R.layout.cell_day_this_month;

        if (ds.isToday()) {
            if (ds.isSaturday())
                cellLayoutResId = R.layout.cell_day_saturday_today;
            else if (ds.isSunday())
                cellLayoutResId = R.layout.cell_day_sunday_today;
            else
                cellLayoutResId = R.layout.cell_day_today;

        } else if (ds.isInMonth()) {
            if (ds.isSaturday())
                cellLayoutResId = R.layout.cell_day_saturday;
            else if (ds.isSunday())
                cellLayoutResId = R.layout.cell_day_sunday;
        }

        return cellLayoutResId;
    }

    private static void checkMonthBeginningStyleChange(final Calendar cal, final RemoteViews cellRv, final SpannableString ss) {

        boolean isFirstOfMonth = cal.get(Calendar.DAY_OF_MONTH) == 1;
        if (isFirstOfMonth)
            cellRv.setTextViewText(R.id.month_year_label, ss);
    }

    private static int getNumberOfInstances(Set<InstanceDTO> instanceSet, final DayStatus ds) {

        int found = 0;
        for (InstanceDTO instance : instanceSet) {

            Calendar startCalendar = Calendar.getInstance(TimeZone.getDefault());
            startCalendar.setTime(instance.getDateStart());

            Calendar endCalendar = Calendar.getInstance(TimeZone.getDefault());
            endCalendar.setTime(instance.getDateEnd());

            // take out 5 milliseconds to avoid erratic behaviour with full day events (or those that end at 00:00)
            endCalendar.add(Calendar.MILLISECOND, -5);

            if (ds.isInDay(startCalendar, endCalendar))
                found++;
        }

        return found;
    }
}
