// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.entry;

import android.provider.CalendarContract;

import java.time.Instant;


public final class Instance {

    public static final String[] FIELDS = {
        CalendarContract.Instances.BEGIN,
        CalendarContract.Instances.END,
        CalendarContract.Instances.START_DAY,
        CalendarContract.Instances.END_DAY
    };

    private static final int MILLIS_IN_A_DAY = 86_400_000;

    private final Instant start;

    private final Instant end;

    private final boolean allDay;

    public Instance(final long epochMilliStart, final long epochMilliEnd, final int julianStartDay, final int julianEndDate) {
        this.start = Instant.ofEpochMilli(epochMilliStart);
        this.end = Instant.ofEpochMilli(epochMilliEnd);
        this.allDay = computeAllDay(start, end, julianStartDay, julianEndDate);
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public boolean isAllDay() {
        return allDay;
    }

    private static boolean computeAllDay(final Instant start, final Instant end, final int julianStartDay, final int julianEndDate) {
        return ((end.toEpochMilli() - start.toEpochMilli()) % MILLIS_IN_A_DAY == 0)
            && ((end.toEpochMilli() - start.toEpochMilli()) / MILLIS_IN_A_DAY == (julianEndDate - julianStartDay) + 1);
    }
}
