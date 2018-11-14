// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.resolver.dto;

import android.database.Cursor;
import android.provider.CalendarContract;

import java.time.Instant;

public final class InstanceDto {

    public static final String[] FIELDS = {
        CalendarContract.Instances.BEGIN,
        CalendarContract.Instances.END
    };

    private final Instant start;

    private final Instant end;

    public InstanceDto(final Cursor instanceCursor) {
        this.start = Instant.ofEpochMilli(instanceCursor.getLong(0));
        this.end = Instant.ofEpochMilli(instanceCursor.getLong(1));
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

}
