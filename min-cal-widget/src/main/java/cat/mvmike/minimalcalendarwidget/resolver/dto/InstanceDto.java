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
        CalendarContract.Instances._ID,
        CalendarContract.Instances.CALENDAR_ID,
        CalendarContract.Instances.CALENDAR_DISPLAY_NAME,
        CalendarContract.Instances.TITLE,
        CalendarContract.Instances.DESCRIPTION,
        CalendarContract.Instances.BEGIN,
        CalendarContract.Instances.END,
        CalendarContract.Instances.CALENDAR_COLOR,
    };

    private final Date dateStart;

    private final Date dateEnd;

    public InstanceDto(final Cursor instanceCursor) {

        //this.id = instanceCursor.getString(0);
        //this.calendarId = instanceCursor.getString(1);
        //this.calendarDisplayName = instanceCursor.getString(2);
        //this.title = instanceCursor.getString(3);
        //this.description = instanceCursor.getString(4);
        this.dateStart = getDate(instanceCursor.getLong(5));
        this.dateEnd = getDate(instanceCursor.getLong(6));
        //this.color = instanceCursor.getString(7);
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
