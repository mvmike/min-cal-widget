// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.service;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.RemoteViews;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import cat.mvmike.minimalcalendarwidget.R;
import cat.mvmike.minimalcalendarwidget.activity.PermissionsActivity;
import cat.mvmike.minimalcalendarwidget.resolver.CalendarResolver;
import cat.mvmike.minimalcalendarwidget.resolver.dto.InstanceDto;
import cat.mvmike.minimalcalendarwidget.service.configuration.ConfigurationService;
import cat.mvmike.minimalcalendarwidget.service.enums.Symbol;
import cat.mvmike.minimalcalendarwidget.service.enums.Theme;
import cat.mvmike.minimalcalendarwidget.status.CalendarStatus;
import cat.mvmike.minimalcalendarwidget.status.DayStatus;

public final class DayService {

    private static final int NUM_WEEKS = 6;

    private static final String PADDING = " ";

    private static final String DOUBLE_PADDING = PADDING + PADDING;

    public static void setDays(final Context context, final SpannableString ss, final RemoteViews remoteViews) {

        int firstDayOfWeek = ConfigurationService.getStartWeekDay(context);
        Theme theme = ConfigurationService.getTheme(context);
        CalendarStatus cs = new CalendarStatus(firstDayOfWeek);

        Set<InstanceDto> instanceSet = PermissionsActivity.isPermitted(context) ?
            CalendarResolver.readAllInstances(context.getContentResolver()) : new HashSet<>();

        RemoteViews rowRv;
        for (int week = 0; week < NUM_WEEKS; week++) {

            rowRv = new RemoteViews(context.getPackageName(), R.layout.row_week);

            DayStatus ds;
            for (int day = 0; day < ChronoField.DAY_OF_WEEK.range().getMaximum(); day++) {

                ds = new DayStatus(cs.getLocalDate(), cs.getYear(), cs.getMonthOfYear(), cs.getDayOfYear());
                RemoteViews cellRv = new RemoteViews(context.getPackageName(), getDayLayout(theme, ds));

                int numberOfInstances = getNumberOfInstances(instanceSet, ds);
                setInstanceNumber(context, cellRv, Integer.toString(ds.getDayOfMonth()), ds.isToday(), numberOfInstances);
                checkMonthBeginningStyleChange(cs.getLocalDate(), cellRv, ss);

                cs.alterLocalDate(1, ChronoUnit.DAYS);

                rowRv.addView(R.id.row_container, cellRv);
            }

            remoteViews.addView(R.id.calendar_widget, rowRv);
        }
    }

    private static void setInstanceNumber(final Context context, final RemoteViews cellRv, final String dayOfMonth, final boolean isToday, final int found) {

        Symbol symbols = ConfigurationService.getInstancesSymbols(context);
        String dayOfMonthSpSt = PADDING + (dayOfMonth.length() == 1 ? dayOfMonth + DOUBLE_PADDING : dayOfMonth) + PADDING + symbols.getSymbol(found);
        SpannableString daySpSt = new SpannableString(dayOfMonthSpSt);
        daySpSt.setSpan(new StyleSpan(Typeface.BOLD), dayOfMonthSpSt.length() - 1, dayOfMonthSpSt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int color;
        if (isToday) {
            color = ContextCompat.getColor(context, R.color.instances_today);
            daySpSt.setSpan(new StyleSpan(Typeface.BOLD), 0, dayOfMonthSpSt.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            color = ContextCompat.getColor(context, ConfigurationService.getInstancesSymbolsColours(context).getHexValue());
        }

        daySpSt.setSpan(new ForegroundColorSpan(color), dayOfMonthSpSt.length() - 1, dayOfMonthSpSt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        daySpSt.setSpan(new RelativeSizeSpan(symbols.getRelativeSize()), dayOfMonthSpSt.length() - 1, dayOfMonthSpSt.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        cellRv.setTextViewText(android.R.id.text1, daySpSt);
    }

    private static int getDayLayout(final Theme theme, final DayStatus ds) {

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

    private static void checkMonthBeginningStyleChange(final LocalDate localDate, final RemoteViews cellRv, final SpannableString ss) {

        if (CalendarStatus.isMonthFirstDay(localDate)) {
            cellRv.setTextViewText(R.id.month_year_label, ss);
        }
    }

    private static int getNumberOfInstances(final Set<InstanceDto> instanceSet, final DayStatus ds) {

        int found = 0;
        if (instanceSet == null || instanceSet.isEmpty()) {
            return found;
        }

        for (InstanceDto instance : instanceSet) {
            if (ds.isInDay(instance.getStart(), instance.getEnd(), instance.isAllDay())) {
                found++;
            }
        }

        return found;
    }
}