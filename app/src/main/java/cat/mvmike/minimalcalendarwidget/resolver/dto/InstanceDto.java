// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.resolver.dto;

import android.database.Cursor;
import android.provider.CalendarContract;

import java.time.Instant;

public final class InstanceDto {

    public static final String[] FIELDS = {
        CalendarContract.Instances.BEGIN,
        CalendarContract.Instances.END,
        CalendarContract.Instances.START_DAY,
        CalendarContract.Instances.END_DAY
    };

    private static final int MILLIS_IN_A_DAY = 86400000;

    private final Instant start;

    private final Instant end;

    private final boolean allDay;

    public InstanceDto(final Cursor instanceCursor) {
        start = Instant.ofEpochMilli(instanceCursor.getLong(0));
        end = Instant.ofEpochMilli(instanceCursor.getLong(1));
        allDay = computeAllDay(start, end, instanceCursor.getInt(2), instanceCursor.getInt(3));
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
