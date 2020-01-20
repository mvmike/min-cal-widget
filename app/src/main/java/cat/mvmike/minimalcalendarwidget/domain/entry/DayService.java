// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.entry;

import android.content.Context;
import android.widget.RemoteViews;

import java.time.LocalDate;
import java.util.Set;

import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Symbol;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme;
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoUnit.DAYS;

public final class DayService {

    private static final String PADDING = " ";

    private static final int MONTH_FIRST_DAY = 1;

    private static final int MAXIMUM_DAYS_IN_MONTH = 31;

    private static final int NUM_WEEKS = 6;

    private static final int DAYS_IN_WEEK = 7;

    public static void setDays(final Context context, final RemoteViews remoteViews, final Set<Instance> instanceSet) {

        int firstDayOfWeek = ConfigurationService.getStartWeekDay(context).ordinal();
        Theme theme = ConfigurationService.getTheme(context);
        Symbol symbol = ConfigurationService.getInstancesSymbols(context);
        Colour colour = ConfigurationService.getInstancesSymbolsColours(context);

        LocalDate systemLocalDate = SystemResolver.get().getSystemLocalDate();
        LocalDate iterationLocalDate = getInitialLocalDate(systemLocalDate, firstDayOfWeek);

        RemoteViews rowRv;
        for (int week = 0; week < NUM_WEEKS; week++) {

            rowRv = SystemResolver.get().createRow(context);

            Day ds;
            RemoteViews cellRv;
            for (int day = 0; day < DAYS_IN_WEEK; day++) {

                ds = new Day(systemLocalDate, iterationLocalDate);
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

                iterationLocalDate = iterationLocalDate.plus(1, DAYS);
            }

            SystemResolver.get().addRowToWidget(remoteViews, rowRv);
        }
    }

    static int getDayLayout(final Theme theme, final Day ds) {

        if (ds.isToday()) {
            return theme.getCellToday(ds.getDayOfWeek());
        }

        if (ds.inMonth()) {
            return theme.getCellThisMonth(ds.getDayOfWeek());
        }

        return theme.getCellNotThisMonth();
    }

    static int getNumberOfInstances(final Set<Instance> instanceSet, final Day ds) {

        if (instanceSet == null || instanceSet.isEmpty()) {
            return 0;
        }

        return (int) instanceSet.stream()
            .filter(instance -> ds.isInDay(instance.getStart(), instance.getEnd(), instance.isAllDay()))
            .count();
    }

    static LocalDate getInitialLocalDate(final LocalDate systemLocalDate, final int firstDayOfWeek) {

        LocalDate firstDayOfMonth = LocalDate.of(systemLocalDate.getYear(), systemLocalDate.getMonthValue(), MONTH_FIRST_DAY);

        int difference = firstDayOfWeek - firstDayOfMonth.get(DAY_OF_WEEK) + 1;
        LocalDate localDate = firstDayOfMonth.plus(difference, DAYS);

        // overlap month manually if dayOfMonth is in current month and greater than 1
        if (localDate.get(DAY_OF_MONTH) > MONTH_FIRST_DAY
            && localDate.get(DAY_OF_MONTH) < (MAXIMUM_DAYS_IN_MONTH / 2)) {
            return localDate.minus(DAYS_IN_WEEK, DAYS);
        }

        return localDate;
    }
}