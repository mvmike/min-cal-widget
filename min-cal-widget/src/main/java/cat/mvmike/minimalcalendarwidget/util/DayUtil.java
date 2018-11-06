// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import cat.mvmike.minimalcalendarwidget.R;
import cat.mvmike.minimalcalendarwidget.activity.PermissionsActivity;
import cat.mvmike.minimalcalendarwidget.resolver.CalendarResolver;
import cat.mvmike.minimalcalendarwidget.resolver.dto.InstanceDto;
import cat.mvmike.minimalcalendarwidget.status.CalendarStatus;
import cat.mvmike.minimalcalendarwidget.status.DayStatus;

public abstract class DayUtil {

    private static final int NUM_WEEKS = 6;

    private static final String PADDING = " ";

    private static final String DOUBLE_PADDING = PADDING + PADDING;

    public static void setDays(final Context context, final Calendar cal, final SpannableString ss, final RemoteViews remoteViews) {

        int firstDayOfWeek = ConfigurationUtil.getStartWeekDay(context);
        ThemesUtil.Theme theme = ConfigurationUtil.getTheme(context);
        CalendarStatus cs = new CalendarStatus(context, cal, firstDayOfWeek);

        Set<InstanceDto> instanceSet = PermissionsActivity.isPermitted(context) ?
            CalendarResolver.readAllInstances(context.getContentResolver(), cal) : new HashSet<>();

        RemoteViews rowRv;
        for (int week = 0; week < NUM_WEEKS; week++) {

            rowRv = new RemoteViews(context.getPackageName(), R.layout.row_week);

            DayStatus ds;
            for (int day = 0; day < Calendar.DAY_OF_WEEK; day++) {

                ds = new DayStatus(cal, cs.getTodayYear(), cs.getThisMonth(), cs.getToday());
                RemoteViews cellRv = new RemoteViews(context.getPackageName(), getDayLayout(theme, ds));

                int numberOfInstances = getNumberOfInstances(instanceSet, ds);
                setInstanceNumber(context, cellRv, Integer.toString(ds.getDayOfMonthInt()), ds.isToday(), numberOfInstances);
                checkMonthBeginningStyleChange(cs.getCalendar(), cellRv, ss);

                cs.getCalendar().add(Calendar.DAY_OF_MONTH, 1);

                rowRv.addView(R.id.row_container, cellRv);
            }

            remoteViews.addView(R.id.calendar_widget, rowRv);
        }
    }

    private static void setInstanceNumber(final Context context, final RemoteViews cellRv, final String dayOfMonth, final boolean isToday, final int found) {

        SymbolsUtil.Symbol symbols = ConfigurationUtil.getInstancesSymbols(context);
        Character[] symbolArray = symbols.getArray();

        int max = symbolArray.length - 1;
        String symbol = String.valueOf(found > max ? symbolArray[max] : symbolArray[found]);
        String dayOfMonthSpSt = PADDING + (dayOfMonth.length() == 1 ? dayOfMonth + DOUBLE_PADDING : dayOfMonth) + PADDING + symbol;
        SpannableString daySpSt = new SpannableString(dayOfMonthSpSt);
        daySpSt.setSpan(new StyleSpan(Typeface.BOLD), dayOfMonthSpSt.length() - 1, dayOfMonthSpSt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int color;
        if (isToday) {
            color = ContextCompat.getColor(context, R.color.instances_today);
            daySpSt.setSpan(new StyleSpan(Typeface.BOLD), 0, dayOfMonthSpSt.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            color = ContextCompat.getColor(context, ConfigurationUtil.getInstancesSymbolColours(context).getHexValue());
        }

        daySpSt.setSpan(new ForegroundColorSpan(color), dayOfMonthSpSt.length() - 1, dayOfMonthSpSt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        daySpSt.setSpan(new RelativeSizeSpan(symbols.getRelativeSize()), dayOfMonthSpSt.length() - 1, dayOfMonthSpSt.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        cellRv.setTextViewText(android.R.id.text1, daySpSt);
    }

    private static int getDayLayout(final ThemesUtil.Theme theme, final DayStatus ds) {

        int cellLayoutResId = theme.getCellDay();

        if (ds.isInMonth()) {
            cellLayoutResId = theme.getCellDayThisMonth();
        }

        if (ds.isToday()) {
            if (ds.isSaturday()) {
                cellLayoutResId = theme.getCellDaySaturdayToday();
            } else if (ds.isSunday()) {
                cellLayoutResId = theme.getCellDaySundayToday();
            } else {
                cellLayoutResId = theme.getCellDayToday();
            }

        } else if (ds.isInMonth()) {
            if (ds.isSaturday()) {
                cellLayoutResId = theme.getCellDaySaturday();
            } else if (ds.isSunday()) {
                cellLayoutResId = theme.getCellDaySunday();
            }
        }

        return cellLayoutResId;
    }

    private static void checkMonthBeginningStyleChange(final Calendar cal, final RemoteViews cellRv, final SpannableString ss) {

        if (CalendarStatus.isMonthFirstDay(cal)) {
            cellRv.setTextViewText(R.id.month_year_label, ss);
        }
    }

    private static int getNumberOfInstances(final Set<InstanceDto> instanceSet, final DayStatus ds) {

        int found = 0;
        if (instanceSet == null || instanceSet.isEmpty()) {
            return found;
        }

        for (InstanceDto instance : instanceSet) {

            Calendar startCalendar = Calendar.getInstance(TimeZone.getDefault());
            startCalendar.setTime(instance.getDateStart());

            Calendar endCalendar = Calendar.getInstance(TimeZone.getDefault());
            endCalendar.setTime(instance.getDateEnd());

            // take out 5 milliseconds to avoid erratic behaviour with full day events (or those that end at 00:00)
            endCalendar.add(Calendar.MILLISECOND, -5);

            if (ds.isInDay(startCalendar, endCalendar)) {
                found++;
            }
        }

        return found;
    }
}