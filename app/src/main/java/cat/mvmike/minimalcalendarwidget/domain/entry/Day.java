// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.entry;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@SuppressWarnings("PMD.ShortClassName")
public final class Day {

    private static final String DAY_OF_MONTH_DF_PATTERN = "00";

    private final LocalDate systemLocalDate;

    private final LocalDate localDate;

    public Day(final LocalDate systemLocalDate, final LocalDate localDate) {
        this.systemLocalDate = systemLocalDate;
        this.localDate = localDate;
    }

    public boolean inYear() {
        return localDate.getYear() == systemLocalDate.getYear();
    }

    public boolean inMonth() {
        return inYear() && localDate.getMonth() == systemLocalDate.getMonth();
    }

    public boolean isToday() {
        return inMonth() && localDate.getDayOfYear() == systemLocalDate.getDayOfYear();
    }

    public DayOfWeek getDayOfWeek() {
        return localDate.getDayOfWeek();
    }

    public String getDayOfMonthString() {
        return new DecimalFormat(DAY_OF_MONTH_DF_PATTERN).format(localDate.getDayOfMonth());
    }

    public boolean isSingleDigitDay() {
        return localDate.getDayOfMonth() < 10;
    }

    public boolean isInDay(final Instant startInstant, final Instant endInstant, final boolean allDayInstance) {

        // take out 5 milliseconds to avoid erratic behaviour with full day events (or those that end at 00:00)
        return toLocalDate(startInstant, allDayInstance).getMonthValue() <= localDate.getMonthValue()
            && toLocalDate(startInstant, allDayInstance).getDayOfMonth() <= localDate.getDayOfMonth()
            && toLocalDate(endInstant.minusMillis(5), allDayInstance).getMonthValue() >= localDate.getMonthValue()
            && toLocalDate(endInstant.minusMillis(5), allDayInstance).getDayOfMonth() >= localDate.getDayOfMonth();
    }

    // calendarProvider uses different set of timezones depending if event is allDay
    private static LocalDateTime toLocalDate(final Instant instant, final boolean allDayInstance) {

        if (allDayInstance) {
            return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        }

        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
