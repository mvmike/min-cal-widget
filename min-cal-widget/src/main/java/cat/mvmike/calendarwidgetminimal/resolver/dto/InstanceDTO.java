// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.calendarwidgetminimal.resolver.dto;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

public final class InstanceDTO {

    public static final Uri INSTANCES_URI = CalendarContract.Instances.CONTENT_URI;

    public static final String[] FIELDS = {CalendarContract.Instances._ID, CalendarContract.Instances.CALENDAR_ID,
        CalendarContract.Instances.TITLE, CalendarContract.Instances.DESCRIPTION, CalendarContract.Instances.BEGIN,
        CalendarContract.Instances.END, CalendarContract.Instances.CALENDAR_COLOR};

    private String id;

    private String calendarId;

    private String title;

    private String description;

    private Date dateStart;

    private Date dateEnd;

    private String color;

    public InstanceDTO(final Cursor instanceCursor) {

        this.id = instanceCursor.getString(0);
        this.calendarId = instanceCursor.getString(1);
        this.title = instanceCursor.getString(2);
        this.description = instanceCursor.getString(3);
        this.dateStart = getDate(instanceCursor.getLong(4));
        this.dateEnd = getDate(instanceCursor.getLong(5));
        this.color = instanceCursor.getString(6);
    }

    private static Date getDate(final long milliSeconds) {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(milliSeconds - offsetInMillis);
        return calendar.getTime();
    }

    public String getId() {
        return id;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public String getColor() {
        return color;
    }
}
