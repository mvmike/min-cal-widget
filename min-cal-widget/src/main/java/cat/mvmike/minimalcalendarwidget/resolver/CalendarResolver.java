// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.resolver;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.CalendarContract;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import cat.mvmike.minimalcalendarwidget.resolver.dto.InstanceDto;

public final class CalendarResolver {

    private static final int CALENDAR_DAYS_SPAN = 45;

    public static Set<InstanceDto> readAllInstances(final ContentResolver contextResolver) {

        Long[] safeDateSpan = CalendarResolver.getSafeDateSpan();
        Cursor instanceCursor = CalendarContract.Instances.query(contextResolver, InstanceDto.FIELDS, safeDateSpan[0], safeDateSpan[1]);

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

    private static Long[] getSafeDateSpan() {

        LocalDate current = LocalDate.now();
        return new Long[]{
            toStartOfDayInEpochMilli(current.minus(CALENDAR_DAYS_SPAN, ChronoUnit.DAYS)),
            toStartOfDayInEpochMilli(current.plus(CALENDAR_DAYS_SPAN, ChronoUnit.DAYS))
        };
    }

    private static long toStartOfDayInEpochMilli(final LocalDate localDate) {
        return (localDate.atStartOfDay(ZoneId.systemDefault())).toInstant().toEpochMilli();
    }
}
