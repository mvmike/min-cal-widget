// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.entry

import cat.mvmike.minimalcalendarwidget.BaseTest
import io.mockk.confirmVerified
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
import java.util.*
import java.util.stream.Stream

internal class InstanceTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getTimeSpansAndIfTheyAreAllDayAndShouldBeInDay")
    fun isInDay(instantProperties: InstantTestProperties) {
        val instance = Instance(
            eventId = instantProperties.id(),
            start = instantProperties.startInstant(),
            end = instantProperties.endInstant(),
            zoneId = instantProperties.zoneOffset
        )

        val result = instance.isInDay(systemLocalDate)

        assertThat(result).isEqualTo(instantProperties.expectedIsInDay)
    }

    @Test
    fun getInstances_shouldReturnEmpty_whenCalendarIsNotPermitted() {
        mockIsReadCalendarPermitted(false)

        val instances = getInstances(context, LocalDate.MIN, LocalDate.MAX)

        assertThat(instances).isEmpty()
        verify { systemResolver.isReadCalendarPermitted(context) }
        confirmVerified(systemResolver)
    }

    @Test
    fun getInstances_shouldReturnEmptyIfEmptyResponse() {
        mockGetSystemZoneId()
        mockIsReadCalendarPermitted(true)
        mockGetSystemLocalDate()

        val initLocalDate = systemLocalDate.minusDays(7)
        val endLocalDate = systemLocalDate.plusDays(7)
        val initEpochMillis = initLocalDate.toStartOfDayInEpochMilli()
        val endEpochMillis = endLocalDate.toStartOfDayInEpochMilli()
        every { systemResolver.getInstances(context, initEpochMillis, endEpochMillis)} returns HashSet()

        val instances = getInstances(context, initLocalDate, endLocalDate)

        assertThat(instances).isEmpty()
        verify(exactly = 4) { systemResolver.getSystemZoneId() }
        verify { systemResolver.isReadCalendarPermitted(context) }
        verify { systemResolver.getInstances(context, initEpochMillis, endEpochMillis) }
        confirmVerified(systemResolver)
    }

    @Test
    fun getInstances_shouldReturnAllInstancesBetweenLocalDates() {
        val expectedInstances = setOf(
            Instance(
                eventId = 1,
                start = "2018-11-29T23:00:00Z".toInstant(systemZoneOffset),
                end = "2018-12-01T23:50:00Z".toInstant(systemZoneOffset),
                zoneId = systemZoneOffset
            ),
            Instance(
                eventId = 2,
                start = "2018-12-01T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-02T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC
            ),
            Instance(
                eventId = 3,
                start = "2018-12-02T23:00:00Z".toInstant(systemZoneOffset),
                end = "2018-12-09T01:00:00Z".toInstant(systemZoneOffset),
                zoneId = systemZoneOffset
            ),
            Instance(
                eventId = 4,
                start = "2018-12-02T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC
            )
        )

        mockGetSystemZoneId()
        mockIsReadCalendarPermitted(true)
        mockGetSystemLocalDate()

        val initLocalDate = systemLocalDate.minusDays(7)
        val endLocalDate = systemLocalDate.plusDays(7)
        val initEpochMillis = initLocalDate.toStartOfDayInEpochMilli()
        val endEpochMillis = endLocalDate.toStartOfDayInEpochMilli()
        every { systemResolver.getInstances(context, initEpochMillis, endEpochMillis) } returns expectedInstances

        val instances = getInstances(context, initLocalDate, endLocalDate)

        assertThat(instances).isEqualTo(expectedInstances)
        verify(exactly = 4) { systemResolver.getSystemZoneId() }
        verify { systemResolver.isReadCalendarPermitted(context) }
        verify { systemResolver.getInstances(context, initEpochMillis, endEpochMillis) }
        confirmVerified(systemResolver)
    }

    companion object {

        @JvmStatic
        @Suppress("unused", "LongMethod")
        // calendarProvider uses UTC when allDay, systemOffset otherwise
        fun getTimeSpansAndIfTheyAreAllDayAndShouldBeInDay(): Stream<InstantTestProperties> = Stream.of(
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

        private fun String.toInstant(zoneOffset: ZoneOffset) = LocalDateTime
            .parse(this, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            .toInstant(zoneOffset)
    }

    internal data class InstantTestProperties(
        private val start: String,
        private val end: String,
        val zoneOffset: ZoneOffset,
        val expectedIsInDay: Boolean
    ) {
        private val random = Random()

        fun id() = random.nextInt()

        fun startInstant() = start.toInstant(zoneOffset)!!

        fun endInstant() = end.toInstant(zoneOffset)!!
    }
}
