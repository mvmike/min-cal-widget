// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.resolver.dto;

import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class InstanceDto {

    public static final Uri INSTANCES_URI = CalendarContract.Instances.CONTENT_URI;

    public static final String[] FIELDS = {
        CalendarContract.Instances.BEGIN,
        CalendarContract.Instances.END
    };

    private final Date dateStart;

    private final Date dateEnd;

    public InstanceDto(final Cursor instanceCursor) {
        this.dateStart = getDate(instanceCursor.getLong(0));
        this.dateEnd = getDate(instanceCursor.getLong(1));
    }

    private static Date getDate(final long milliSeconds) {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(milliSeconds - offsetInMillis);
        return calendar.getTime();
    }

    public Date getDateStart() {
        return dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

}
