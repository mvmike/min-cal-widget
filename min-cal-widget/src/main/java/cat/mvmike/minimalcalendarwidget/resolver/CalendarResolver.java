// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.resolver;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import cat.mvmike.minimalcalendarwidget.resolver.dto.InstanceDto;

public abstract class CalendarResolver {

    private static final int CALENDAR_DAYS_SPAN = 45;

    public static Set<InstanceDto> readAllInstances(final ContentResolver contextResolver, final Calendar cal) {

        Calendar[] safeDateSpan = CalendarResolver.getSafeDateSpan(cal);
        Uri instancesUri = getInstancesUri(safeDateSpan[0], safeDateSpan[1]);
        Cursor instanceCursor = contextResolver.query(instancesUri, InstanceDto.FIELDS, null, null, null);

        if (instanceCursor == null || instanceCursor.getCount() == 0) {
            return null;
        }

        Set<InstanceDto> instances = new HashSet<>();
        while (instanceCursor.moveToNext()) {
            instances.add(new InstanceDto(instanceCursor));
        }

        instanceCursor.close();
        return instances;
    }

    private static Calendar[] getSafeDateSpan(final Calendar current) {

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(current.getTime());
        startDate.add(Calendar.DATE, -CALENDAR_DAYS_SPAN);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(current.getTime());
        endDate.add(Calendar.DATE, +CALENDAR_DAYS_SPAN);

        return new Calendar[]{startDate, endDate};
    }

    private static Uri getInstancesUri(final Calendar startTime, final Calendar endTime) {

        Uri.Builder instancesUriBuilder = InstanceDto.INSTANCES_URI.buildUpon();

        ContentUris.appendId(instancesUriBuilder, startTime.getTimeInMillis());
        ContentUris.appendId(instancesUriBuilder, endTime.getTimeInMillis());

        return instancesUriBuilder.build();
    }
}
