// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.status;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

public final class DayStatus {

    private final boolean inMonth;

    private final boolean today;

    private final boolean saturday;

    private final boolean sunday;

    private final int dayOfMonth;

    private final int month;

    public DayStatus(final LocalDate localDate, final int todayYear, final int thisMonth, final int dayInYear) {

        boolean inYear = localDate.get(YEAR) == todayYear;
        inMonth = localDate.get(MONTH_OF_YEAR) == thisMonth;
        today = inYear && inMonth && localDate.get(DAY_OF_YEAR) == dayInYear;

        saturday = localDate.get(DAY_OF_WEEK) == DayOfWeek.SATURDAY.getValue();
        sunday = localDate.get(DAY_OF_WEEK) == DayOfWeek.SUNDAY.getValue();

        dayOfMonth = localDate.get(DAY_OF_MONTH);
        month = localDate.get(MONTH_OF_YEAR);
    }

    public boolean isInMonth() {
        return inMonth;
    }

    public boolean isToday() {
        return today;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public boolean isInDay(final Instant startInstant, final Instant endInstant) {

        return toLocalDate(startInstant).get(MONTH_OF_YEAR) <= month && toLocalDate(startInstant).get(DAY_OF_MONTH) <= dayOfMonth
            && toLocalDate(endInstant).get(MONTH_OF_YEAR) >= month && toLocalDate(endInstant).get(DAY_OF_MONTH) >= dayOfMonth;
    }

    private static LocalDateTime toLocalDate(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
