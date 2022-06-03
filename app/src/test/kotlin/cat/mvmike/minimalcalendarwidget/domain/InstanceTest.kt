// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.infrastructure.config.ClockConfig
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Random
import java.util.stream.Stream

internal class InstanceTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getTimeSpansAndIfTheyAreAllDayAndShouldBeInDay")
    fun isInDay(instantProperties: InstantTestProperties) {
        val instance = Instance(
            eventId = instantProperties.id(),
            start = instantProperties.start.toInstant(instantProperties.zoneOffset)!!,
            end = instantProperties.end.toInstant(instantProperties.zoneOffset)!!,
            zoneId = instantProperties.zoneOffset,
            isDeclined = false
        )

        val result = instance.isInDay(systemLocalDate)

        assertThat(result).isEqualTo(instantProperties.expectedIsInDay)
    }

    @Test
    fun getInstances_shouldReturnEmpty_whenCalendarIsNotPermitted() {
        mockIsReadCalendarPermitted(false)

        val instances = getInstances(context, LocalDate.MIN, LocalDate.MAX)

        assertThat(instances).isEmpty()
        verify { CalendarResolver.isReadCalendarPermitted(context) }
    }

    @ParameterizedTest
    @MethodSource("getSetsOfExpectedInstances")
    fun getInstances_shouldReturnAllInstancesBetweenLocalDates(expectedInstances: Set<Instance>) {
        mockGetSystemZoneId()
        mockIsReadCalendarPermitted(true)
        mockGetSystemLocalDate()

        val initLocalDate = systemLocalDate.minusDays(7)
        val endLocalDate = systemLocalDate.plusDays(7)

        val initEpochMillis = initLocalDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endEpochMillis = endLocalDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        every { CalendarResolver.getInstances(context, initEpochMillis, endEpochMillis) } returns expectedInstances

        val instances = getInstances(context, initLocalDate, endLocalDate)

        assertThat(instances).isEqualTo(expectedInstances)
        verify { ClockConfig.getSystemZoneId() }
        verify { CalendarResolver.isReadCalendarPermitted(context) }
        verify { CalendarResolver.getInstances(context, initEpochMillis, endEpochMillis) }
    }


    // calendarProvider uses UTC when allDay, systemOffset otherwise
    @Suppress("UnusedPrivateMember", "LongMethod")
    private fun getTimeSpansAndIfTheyAreAllDayAndShouldBeInDay(): Stream<InstantTestProperties> = Stream.of(
        //starting and ending before day
        InstantTestProperties(
            start = "2018-12-02T02:15:00Z",
            end = "2018-12-03T23:15:00Z",
            zoneOffset = systemZoneOffset,
            expectedIsInDay = false
        ),
        InstantTestProperties(
            start = "2018-12-02T00:00:00Z",
            end = "2018-12-03T00:00:00Z",
            zoneOffset = ZoneOffset.UTC,
            expectedIsInDay = false
        ),
        // starting before and ending in day
        InstantTestProperties(
            start = "2018-12-01T00:00:00Z",
            end = "2018-12-04T23:59:00Z",
            zoneOffset = systemZoneOffset,
            expectedIsInDay = true
        ),
        InstantTestProperties(
            start = "2018-12-01T00:00:00Z",
            end = "2018-12-05T00:00:00Z",
            zoneOffset = ZoneOffset.UTC,
            expectedIsInDay = true
        ),
        // starting before and ending after day
        InstantTestProperties(
            start = "2018-12-01T10:55:00Z",
            end = "2018-12-07T23:00:00Z",
            zoneOffset = systemZoneOffset,
            expectedIsInDay = true
        ),
        InstantTestProperties(
            start = "2018-12-01T00:00:00Z",
            end = "2018-12-07T00:00:00Z",
            zoneOffset = ZoneOffset.UTC,
            expectedIsInDay = true
        ),
        // starting in and ending in day
        InstantTestProperties(
            start = "2018-12-04T23:00:00Z",
            end = "2018-12-04T23:50:00Z",
            zoneOffset = systemZoneOffset,
            expectedIsInDay = true
        ),
        InstantTestProperties(
            start = "2018-12-04T00:00:00Z",
            end = "2018-12-05T00:00:00Z",
            zoneOffset = ZoneOffset.UTC,
            expectedIsInDay = true
        ),
        // starting in and ending after day
        InstantTestProperties(
            start = "2018-12-04T23:00:00Z",
            end = "2018-12-05T01:00:00Z",
            zoneOffset = systemZoneOffset,
            expectedIsInDay = true
        ),
        InstantTestProperties(
            start = "2018-12-04T00:00:00Z",
            end = "2018-12-06T00:00:00Z",
            zoneOffset = ZoneOffset.UTC,
            expectedIsInDay = true
        ),
        // starting after and ending after day
        InstantTestProperties(
            start = "2018-12-05T00:00:00Z",
            end = "2018-12-05T02:00:00Z",
            zoneOffset = systemZoneOffset,
            expectedIsInDay = false
        ),
        InstantTestProperties(
            start = "2018-12-05T00:00:00Z",
            end = "2018-12-06T00:00:00Z",
            zoneOffset = ZoneOffset.UTC,
            expectedIsInDay = false
        )
    )

    @Suppress("UnusedPrivateMember")
    private fun getSetsOfExpectedInstances(): Stream<Set<Instance>> = Stream.of(
        emptySet(),
        setOf(
            Instance(
                eventId = 1,
                start = "2018-11-29T23:00:00Z".toInstant(systemZoneOffset),
                end = "2018-12-01T23:50:00Z".toInstant(systemZoneOffset),
                zoneId = systemZoneOffset,
                isDeclined = false
            ),
            Instance(
                eventId = 2,
                start = "2018-12-01T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-02T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = 3,
                start = "2018-12-02T23:00:00Z".toInstant(systemZoneOffset),
                end = "2018-12-09T01:00:00Z".toInstant(systemZoneOffset),
                zoneId = systemZoneOffset,
                isDeclined = false
            ),
            Instance(
                eventId = 4,
                start = "2018-12-02T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            )
        )
    )

    internal data class InstantTestProperties(
        val start: String,
        val end: String,
        val zoneOffset: ZoneOffset,
        val expectedIsInDay: Boolean
    ) {
        private val random = Random()

        fun id() = random.nextInt()
    }

    private fun String.toInstant(zoneOffset: ZoneOffset) = LocalDateTime
        .parse(this, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        .toInstant(zoneOffset)
}
