// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.entry;

import android.content.Context;
import android.widget.RemoteViews;

import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService;
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver;
import cat.mvmike.minimalcalendarwidget.domain.entry.dto.InstanceDto;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Symbol;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme;
import cat.mvmike.minimalcalendarwidget.domain.entry.status.CalendarStatus;
import cat.mvmike.minimalcalendarwidget.domain.entry.status.DayStatus;

public final class DayService {

    private static final String PADDING = " ";

    private static final int NUM_WEEKS = 6;

    private static final int DAYS_IN_WEEK = 7;

    public static void setDays(final Context context, final RemoteViews remoteViews) {

        int firstDayOfWeek = ConfigurationService.getStartWeekDay(context).ordinal();
        Theme theme = ConfigurationService.getTheme(context);
        Symbol symbol = ConfigurationService.getInstancesSymbols(context);
        Colour colour = ConfigurationService.getInstancesSymbolsColours(context);
        CalendarStatus cs = new CalendarStatus(firstDayOfWeek);

        Set<InstanceDto> instanceSet = SystemResolver.get().isReadCalendarPermitted(context) ?
            InstanceService.readAllInstances(context) : new HashSet<>();

        RemoteViews rowRv;
        for (int week = 0; week < NUM_WEEKS; week++) {

            rowRv = SystemResolver.get().createRow(context);

            DayStatus ds;
            RemoteViews cellRv;
            for (int day = 0; day < DAYS_IN_WEEK; day++) {

                ds = new DayStatus(cs.getLocalDate(), cs.getYear(), cs.getMonthOfYear(), cs.getDayOfYear());
                cellRv = SystemResolver.get().createDay(context, getDayLayout(theme, ds));

                int numberOfInstances = getNumberOfInstances(instanceSet, ds);
                int color = ds.isToday() ?
                    SystemResolver.get().getColorInstancesTodayId(context) :
                    SystemResolver.get().getColorInstancesId(context, colour);

                SystemResolver.get().addDayCellRemoteView(
                    context,
                    rowRv,
                    cellRv,
                    PADDING + ds.getDayOfMonthString() + PADDING + symbol.getSymbol(numberOfInstances),
                    ds.isToday(),
                    ds.isSingleDigitDay(),
                    symbol.getRelativeSize(),
                    color);

                cs.alterLocalDate(1, ChronoUnit.DAYS);
            }

            SystemResolver.get().addRowToWidget(remoteViews, rowRv);
        }
    }

    static int getDayLayout(final Theme theme, final DayStatus ds) {

        int cellLayoutResId = theme.getCellDay();

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
            } else {
                cellLayoutResId = theme.getCellDayThisMonth();
            }
        }

        return cellLayoutResId;
    }

    static int getNumberOfInstances(final Set<InstanceDto> instanceSet, final DayStatus ds) {

        if (instanceSet == null || instanceSet.isEmpty()) {
            return 0;
        }

        return (int) instanceSet.stream()
            .filter(instance -> ds.isInDay(instance.getStart(), instance.getEnd(), instance.isAllDay()))
            .count();
    }
}