// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.entry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cat.mvmike.minimalcalendarwidget.BaseTest;

import static cat.mvmike.minimalcalendarwidget.domain.entry.InstanceService.getInstancesWithTimeout;
import static cat.mvmike.minimalcalendarwidget.domain.entry.InstanceService.readAllInstances;
import static cat.mvmike.minimalcalendarwidget.domain.entry.InstanceService.toStartOfDayInEpochMilli;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

final class InstanceServiceTest extends BaseTest {

    @Test
    void getInstancesWithTimeout_shouldReturnEmptyIfNotPermission() {
        when(systemResolver.isReadCalendarPermitted(context)).thenReturn(false);

        Optional<Set<Instance>> instances = getInstancesWithTimeout(context, 1000, TimeUnit.MILLISECONDS);
        assertTrue(instances.isPresent());
        assertTrue(instances.get().isEmpty());
    }

    @Test
    void getInstancesWithTimeout_shouldReturnEmptyIfTimedOut() {
        when(systemResolver.isReadCalendarPermitted(context)).thenReturn(true);

        Optional<Set<Instance>> instances = getInstancesWithTimeout(context, 0, TimeUnit.MILLISECONDS);
        assertFalse(instances.isPresent());
    }

    @Test
    void getInstancesWithTimeout_shouldReadAllInstances() {
        Set<Instance> expectedInstances = Stream.of(
            new Instance(1543190400000L, 1543276800000L, 0, 0), // 11/26 all day
            new Instance(1543881600000L, 1543968000000L, 0, 0) // 12/4 all day
        ).collect(Collectors.toCollection(HashSet::new));

        when(systemResolver.isReadCalendarPermitted(context)).thenReturn(true);
        when(systemResolver.getSystemLocalDate()).thenReturn(LocalDate.of(2018, 12, 4));
        when(systemResolver.getInstances(context, 1539982800000L, 1547758800000L)).thenReturn(expectedInstances);

        Optional<Set<Instance>> instances = getInstancesWithTimeout(context, 200, TimeUnit.MILLISECONDS);
        assertTrue(instances.isPresent());
        assertEquals(expectedInstances, instances.get());
    }

    @ParameterizedTest
    @MethodSource("getInstancesBetweenInstants")
    void readAllInstances_shouldFetchAllInstancesBetweenInstances(final Set<Instance> expectedInstances) {

        when(systemResolver.isReadCalendarPermitted(context)).thenReturn(true);
        when(systemResolver.getSystemLocalDate()).thenReturn(LocalDate.of(2018, 12, 4));
        when(systemResolver.getInstances(context, 1539982800000L, 1547758800000L)).thenReturn(expectedInstances);

        assertEquals(expectedInstances, readAllInstances(context));
    }

    @ParameterizedTest
    @MethodSource("getLocalDateAndBeginningOfSystemTimezoneInMillis")
    void toStartOfDayInEpochMilli_shouldReturnStartOfDayOfSystemTimeZone(LocalDate localDate, long millis) {
        assertEquals(millis, toStartOfDayInEpochMilli(localDate));
    }

    private static Stream<Arguments> getInstancesBetweenInstants() {
        return Stream.of(
            Arguments.of(Stream.of(
                new Instance(1543190400000L, 1543276800000L, 0, 0), // 11/26 all day
                new Instance(1543881600000L, 1543968000000L, 0, 0) // 12/4 all day
            ).collect(Collectors.toCollection(HashSet::new))),
            Arguments.of(Stream.of(
                new Instance(1546300800000L, 1546387200000L, 0, 0), // 01/01 all day
                new Instance(1546387200000L, 1546473600000L, 0, 0), // 01/02 all day
                new Instance(1546646400000L, 1546732800000L, 0, 0) // 01/05 all day
            ).collect(Collectors.toCollection(HashSet::new))),
            Arguments.of(Stream.of(
                new Instance(1546646400000L, 1546732800000L, 0, 0) // 01/05 all day
            ).collect(Collectors.toCollection(HashSet::new))),
            Arguments.of(new HashSet<>()),
            Arguments.of(Stream.of(
                new Instance(1543190400000L, 1543276800000L, 0, 0), // 11/26 all day
                new Instance(1543363200000L, 1543449600000L, 0, 0), // 11/28 all day
                new Instance(1543449600000L, 1543536000000L, 0, 0), // 11/29 all day
                new Instance(1543795200000L, 1543967999000L, 0, 0), // 12/3 all day
                new Instance(1543881600000L, 1543968000000L, 0, 0) // 12/4 all day
            ).collect(Collectors.toCollection(HashSet::new)))
        );
    }

    private static Stream<Arguments> getLocalDateAndBeginningOfSystemTimezoneInMillis() {
        return Stream.of(
            Arguments.of("2016-01-01", 1451595600000L),
            Arguments.of("2018-10-11", 1539205200000L),
            Arguments.of("2018-12-04", 1543870800000L),
            Arguments.of("2019-06-06", 1559768400000L),
            Arguments.of("3000-12-31", 32535118800000L)
        );
    }
}
