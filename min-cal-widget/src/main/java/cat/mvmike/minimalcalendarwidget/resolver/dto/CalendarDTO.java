// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.resolver.dto;

import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

public final class CalendarDTO {

    public static final Uri CALENDAR_URI = CalendarContract.Calendars.CONTENT_URI;

    public static final String[] FIELDS = {CalendarContract.Calendars._ID, CalendarContract.Calendars.NAME,
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.CALENDAR_COLOR, CalendarContract.Calendars.VISIBLE};

    private final String id;

    private final String name;

    private final String displayName;

    private final String color;

    private final boolean selected;

    public CalendarDTO(final Cursor calendarCursor) {

        this.id = calendarCursor.getString(0);
        this.name = calendarCursor.getString(1);
        this.displayName = calendarCursor.getString(2);
        this.color = calendarCursor.getString(3);
        this.selected = !calendarCursor.getString(4).equals("0");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public boolean isSelected() {
        return selected;
    }
}
