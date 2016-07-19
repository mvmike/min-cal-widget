// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.calendarwidgetminimal.util;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

import cat.mvmike.calendarwidgetminimal.dto.resolver.CalendarDTO;
import cat.mvmike.calendarwidgetminimal.dto.resolver.InstanceDTO;

public abstract class CalendarResolver {

    public static Calendar[] getSafeDateSpan(final Calendar current) {

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(current.getTime());
        startDate.add(Calendar.DATE, -45);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(current.getTime());
        endDate.add(Calendar.DATE, +45);

        return new Calendar[] {startDate, endDate};
    }

    public static Set<CalendarDTO> readAllCalendars(final ContentResolver contextResolver) {

        Cursor calendarCursor = contextResolver.query(CalendarDTO.CALENDAR_URI, CalendarDTO.FIELDS, null, null, null);

        if (calendarCursor == null || calendarCursor.getCount() == 0)
            return null;

        Set<CalendarDTO> calendars = new HashSet<>();
        while (calendarCursor.moveToNext())
            calendars.add(new CalendarDTO(calendarCursor));

        calendarCursor.close();
        return calendars;
    }

    public static Set<InstanceDTO> readAllInstances(final ContentResolver contextResolver, final Calendar startTime,
        final Calendar endTime) {

        Uri instancesUri = getInstancesUri(startTime, endTime);
        Cursor instanceCursor = contextResolver.query(instancesUri, InstanceDTO.FIELDS, null, null, null);

        if (instanceCursor == null || instanceCursor.getCount() == 0)
            return null;

        Set<InstanceDTO> instances = new HashSet<>();
        while (instanceCursor.moveToNext())
            instances.add(new InstanceDTO(instanceCursor));

        instanceCursor.close();
        return instances;
    }

    private static Uri getInstancesUri(final Calendar startTime, final Calendar endTime) {

        Uri.Builder instancesUriBuilder = InstanceDTO.INSTANCES_URI.buildUpon();

        ContentUris.appendId(instancesUriBuilder, startTime.getTimeInMillis());
        ContentUris.appendId(instancesUriBuilder, endTime.getTimeInMillis());

        return instancesUriBuilder.build();
    }
}
