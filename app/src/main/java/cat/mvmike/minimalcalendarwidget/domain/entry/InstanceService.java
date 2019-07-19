// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.entry;

import android.content.Context;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver;
import cat.mvmike.minimalcalendarwidget.domain.entry.dto.InstanceDto;

final class InstanceService {

    private static final int CALENDAR_DAYS_SPAN = 45;

    static Set<InstanceDto> readAllInstances(final Context context) {

        LocalDate current = SystemResolver.get().getSystemLocalDate();

        return SystemResolver.get().getInstances(
            context,
            toStartOfDayInEpochMilli(current.minus(CALENDAR_DAYS_SPAN, ChronoUnit.DAYS)),
            toStartOfDayInEpochMilli(current.plus(CALENDAR_DAYS_SPAN, ChronoUnit.DAYS))
        );
    }

    static long toStartOfDayInEpochMilli(final LocalDate localDate) {
        return (localDate.atStartOfDay(ZoneId.systemDefault())).toInstant().toEpochMilli();
    }
}
