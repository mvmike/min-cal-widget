// Copyright (c) 2019, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.entry.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DayStatusTest {

    public static final DayStatus TODAY_WEEKDAY = new DayStatus(LocalDate.of(2018, 12, 4), 2018, 12, 338);

    public static final DayStatus TODAY_SATURDAY = new DayStatus(LocalDate.of(2018, 12, 8), 2018, 12, 342);

    public static final DayStatus TODAY_SUNDAY = new DayStatus(LocalDate.of(2018, 12, 9), 2018, 12, 343);

    public static final DayStatus IN_MONTH_WEEKDAY = new DayStatus(LocalDate.of(2018, 12, 14), 2018, 12, 338);

    public static final DayStatus IN_MONTH_SATURDAY = new DayStatus(LocalDate.of(2018, 12, 15), 2018, 12, 338);

    public static final DayStatus IN_MONTH_SUNDAY = new DayStatus(LocalDate.of(2018, 12, 16), 2018, 12, 338);

    public static final DayStatus NOT_IN_MONTH_WEEKDAY = new DayStatus(LocalDate.of(2018, 11, 23), 2018, 12, 338);

    public static final DayStatus NOT_IN_MONTH_SATURDAY = new DayStatus(LocalDate.of(2018, 11, 24), 2018, 12, 338);

    public static final DayStatus NOT_IN_MONTH_SUNDAY = new DayStatus(LocalDate.of(2018, 11, 25), 2018, 12, 338);

    @ParameterizedTest
    @MethodSource("getCombinationOfDayStatuses")
    void dayStatus_shouldSetInternalProperties(final DayStatus dayStatus, final boolean inMonth, final boolean isToday, final DayOfWeek dayOfWeek) {

        assertEquals(inMonth, dayStatus.isInMonth());
        assertEquals(isToday, dayStatus.isToday());
        assertEquals(dayOfWeek, dayStatus.getDayOfWeek());
    }

    private static Stream<Arguments> getCombinationOfDayStatuses() {

        return Stream.of(
            Arguments.of(TODAY_WEEKDAY, true, true, DayOfWeek.TUESDAY),
            Arguments.of(TODAY_SATURDAY, true, true, DayOfWeek.SATURDAY),
            Arguments.of(TODAY_SUNDAY, true, true, DayOfWeek.SUNDAY),

            Arguments.of(IN_MONTH_WEEKDAY, true, false, DayOfWeek.FRIDAY),
            Arguments.of(IN_MONTH_SATURDAY, true, false, DayOfWeek.SATURDAY),
            Arguments.of(IN_MONTH_SUNDAY, true, false, DayOfWeek.SUNDAY),

            Arguments.of(NOT_IN_MONTH_WEEKDAY, false, false, DayOfWeek.FRIDAY),
            Arguments.of(NOT_IN_MONTH_SATURDAY, false, false, DayOfWeek.SATURDAY),
            Arguments.of(NOT_IN_MONTH_SUNDAY, false, false, DayOfWeek.SUNDAY)
        );
    }
}
