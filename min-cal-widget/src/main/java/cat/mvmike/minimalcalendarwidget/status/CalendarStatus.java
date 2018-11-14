// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.status;

import java.time.LocalDate;
import java.time.temporal.TemporalUnit;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoUnit.DAYS;

public final class CalendarStatus {

    private static final int MONTH_FIRST_DAY = 1;

    private static final int DAYS_IN_WEEK = 7;

    private static final int MAXIMUM_DAYS_IN_MONTH = 31;

    private final int dayOfYear;

    private final int year;

    private final int monthOfYear;

    private LocalDate localDate;

    public CalendarStatus(final int firstDayOfWeek) {

        LocalDate now = LocalDate.now();

        dayOfYear = now.get(DAY_OF_YEAR);
        year = now.get(YEAR);
        monthOfYear = now.get(MONTH_OF_YEAR);

        localDate = LocalDate.of(year, monthOfYear, MONTH_FIRST_DAY);

        int difference = firstDayOfWeek - localDate.get(DAY_OF_WEEK) - 1;
        localDate = localDate.plus(difference, DAYS);

        // overlap month manually if dayOfMonth is in current month and greater than 1
        if (localDate.get(DAY_OF_MONTH) > MONTH_FIRST_DAY
            && localDate.get(DAY_OF_MONTH) < (MAXIMUM_DAYS_IN_MONTH / 2)) {
            localDate = localDate.minus(DAYS_IN_WEEK, DAYS);
        }
    }

    public static boolean isMonthFirstDay(final LocalDate ins) {
        return MONTH_FIRST_DAY == ins.getDayOfMonth();
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public int getYear() {
        return year;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void alterLocalDate(final long amountToAdd, final TemporalUnit unit) {
        localDate = localDate.plus(amountToAdd, unit);
    }
}