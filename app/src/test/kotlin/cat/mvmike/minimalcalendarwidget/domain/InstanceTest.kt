// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.Instance.AllDayInstance
import cat.mvmike.minimalcalendarwidget.domain.Instance.TimedInstance
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.ZonedDateTime

internal class InstanceTest : BaseTest() {

    @ParameterizedTest
    @MethodSource(
        value = [
            "getInstancesStartingAndEndingBeforeSystemLocalDate",
            "getInstancesStartingBeforeAndEndingInSystemLocalDate",
            "getInstancesStartingBeforeAndEndingAfterSystemLocalDate",
            "getInstancesStartingInAndEndingInSystemLocalDate",
            "getInstancesStartingInAndEndingAfterSystemLocalDate",
            "getInstancesStartingAfterAndEndingAfterSystemLocalDate"
        ]
    )
    fun isInDay(
        instance: Instance,
        expectedIsInDay: Boolean
    ) {
        val result = instance.isInDay(systemLocalDate, systemZoneId)

        assertThat(result).isEqualTo(expectedIsInDay)
    }

    @Test
    fun getInstances_shouldReturnEmpty_whenCalendarIsNotPermitted() {
        mockIsReadCalendarPermitted(false)

        val instances = getInstances(context, LocalDate.MIN, LocalDate.MAX)

        assertThat(instances).isEmpty()
        verifyIsReadCalendarPermitted()
    }

    @ParameterizedTest
    @MethodSource("getSetsOfExpectedInstances")
    fun getInstances_shouldReturnAllInstancesBetweenLocalDates(expectedInstances: Set<Instance>) {
        mockGetSystemZoneId()
        mockIsReadCalendarPermitted(true)
        mockGetSystemLocalDate()

        val initLocalDate = systemLocalDate.minusDays(7)
        val endLocalDate = systemLocalDate.plusDays(7)

        val initEpochMillis = initLocalDate.atStartOfDay(systemZoneId).toInstant().toEpochMilli()
        val endEpochMillis = endLocalDate.atStartOfDay(systemZoneId).toInstant().toEpochMilli()
        every { CalendarResolver.getInstances(context, initEpochMillis, endEpochMillis) } returns expectedInstances

        val instances = getInstances(context, initLocalDate, endLocalDate)

        assertThat(instances).isEqualTo(expectedInstances)
        verifyGetSystemZoneId()
        verifyIsReadCalendarPermitted()
        verify { CalendarResolver.getInstances(context, initEpochMillis, endEpochMillis) }
    }

    private fun getInstancesStartingAndEndingBeforeSystemLocalDate() = listOf(
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-02T02:15:00+03:00"),
            end = ZonedDateTime.parse("2018-12-03T23:15:00+03:00")
        ),
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-02T02:15:00-08:00"),
            end = ZonedDateTime.parse("2018-12-03T11:30:00-08:00")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = LocalDate.parse("2018-12-02"),
            end = LocalDate.parse("2018-12-03")
        )
    ).map { of(it, false) }

    private fun getInstancesStartingBeforeAndEndingInSystemLocalDate() = listOf(
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-01T00:00:00+03:00"),
            end = ZonedDateTime.parse("2018-12-04T23:59:00+03:00")
        ),
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-02T02:15:00-08:00"),
            end = ZonedDateTime.parse("2018-12-03T13:30:00-08:00")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = LocalDate.parse("2018-12-01"),
            end = LocalDate.parse("2018-12-05")
        )
    ).map { of(it, true) }

    private fun getInstancesStartingBeforeAndEndingAfterSystemLocalDate() = listOf(
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-01T10:55:00+03:00"),
            end = ZonedDateTime.parse("2018-12-07T23:00:00+03:00")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = LocalDate.parse("2018-12-01"),
            end = LocalDate.parse("2018-12-07")
        )
    ).map { of(it, true) }

    private fun getInstancesStartingInAndEndingInSystemLocalDate() = listOf(
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-04T23:00:00+03:00"),
            end = ZonedDateTime.parse("2018-12-04T23:50:00+03:00")
        ),
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-03T13:30:00-08:00"),
            end = ZonedDateTime.parse("2018-12-04T10:30:00-08:00")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = LocalDate.parse("2018-12-04"),
            end = LocalDate.parse("2018-12-05")
        )
    ).map { of(it, true) }

    private fun getInstancesStartingInAndEndingAfterSystemLocalDate() = listOf(
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-04T23:00:00+03:00"),
            end = ZonedDateTime.parse("2018-12-05T01:00:00+03:00")
        ),
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-04T12:30:00-08:00"),
            end = ZonedDateTime.parse("2018-12-04T16:30:00-08:00")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = LocalDate.parse("2018-12-04"),
            end = LocalDate.parse("2018-12-06")
        )
    ).map { of(it, true) }

    private fun getInstancesStartingAfterAndEndingAfterSystemLocalDate() = listOf(
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-05T00:00:00+03:00"),
            end = ZonedDateTime.parse("2018-12-05T02:00:00+03:00")
        ),
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-04T13:30:00-08:00"),
            end = ZonedDateTime.parse("2018-12-04T16:30:00-08:00")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = LocalDate.parse("2018-12-05"),
            end = LocalDate.parse("2018-12-06")
        )
    ).map { of(it, false) }

    private fun getSetsOfExpectedInstances() = listOf(
        emptySet(),
        setOf(
            TimedInstance(
                eventId = random.nextInt(),
                isDeclined = false,
                start = ZonedDateTime.parse("2018-11-29T23:00:00+03:00"),
                end = ZonedDateTime.parse("2018-12-01T23:50:00+03:00")
            ),
            AllDayInstance(
                eventId = random.nextInt(),
                isDeclined = false,
                start = LocalDate.parse("2018-12-01"),
                end = LocalDate.parse("2018-12-02")
            ),
            TimedInstance(
                eventId = random.nextInt(),
                isDeclined = true,
                start = ZonedDateTime.parse("2018-12-02T23:00:00+03:00"),
                end = ZonedDateTime.parse("2018-12-09T01:00:00+03:00")
            ),
            AllDayInstance(
                eventId = random.nextInt(),
                isDeclined = false,
                start = LocalDate.parse("2018-12-02"),
                end = LocalDate.parse("2018-12-06")
            )
        )
    )
}