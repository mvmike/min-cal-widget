// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.database.Cursor
import android.provider.CalendarContract
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.Instance
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

internal class CalendarResolverTest : BaseTest() {

    private val begin = 1657097518736
    private val end = 1659775918428

    private val cursor = mockk<Cursor>()

    @Test
    fun shouldReturnEmptySetWhenQueryCannotBeExecuted() {
        mockkStatic(CalendarContract.Instances::class)
        every { CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end) } throws RuntimeException()

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).isEqualTo(emptySet<Instance>())
        verify { context.contentResolver }
        verify { CalendarResolver.getInstances(context, begin, end) }
    }

    @Test
    fun shouldSkipInvalidInstance() {
        mockkStatic(CalendarContract.Instances::class)
        every { CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end) } returns cursor
        every { cursor.moveToNext() } returns true andThen true andThen false
        every { cursor.getInt(0) } returns 1 andThen 2
        every { cursor.getLong(1) } throws RuntimeException("some weird error") andThen 1657097518737
        every { cursor.getLong(2) } returns 1659775918429
        every { cursor.getString(3) } returns "UTC"
        every { cursor.getInt(4) } returns 0
        justRun { cursor.close() }

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).containsExactlyInAnyOrder(
            Instance(
                eventId = 2,
                start = Instant.ofEpochMilli(1657097518737),
                end = Instant.ofEpochMilli(1659775918429),
                zoneId = ZoneId.of("UTC"),
                isDeclined = false
            )
        )
        verify { context.contentResolver }
        verify { CalendarResolver.getInstances(context, begin, end) }
    }

    @Test
    fun shouldFetchAllInstances() {
        mockkStatic(CalendarContract.Instances::class)
        every { CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end) } returns cursor
        every { cursor.moveToNext() } returns true andThen true andThen false
        every { cursor.getInt(0) } returns 1 andThen 2
        every { cursor.getLong(1) } returns 1657097518736 andThen 1657097518737
        every { cursor.getLong(2) } returns 1659775918428 andThen 1659775918429
        every { cursor.getString(3) } returns null andThen "UTC"
        every { cursor.getInt(4) } returns 1 andThen 0
        justRun { cursor.close() }

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).containsExactlyInAnyOrder(
            Instance(
                eventId = 1,
                start = Instant.ofEpochMilli(1657097518736),
                end = Instant.ofEpochMilli(1659775918428),
                zoneId = ZoneOffset.systemDefault(),
                isDeclined = false
            ),
            Instance(
                eventId = 2,
                start = Instant.ofEpochMilli(1657097518737),
                end = Instant.ofEpochMilli(1659775918429),
                zoneId = ZoneId.of("UTC"),
                isDeclined = false
            )
        )
        verify { context.contentResolver }
        verify { CalendarResolver.getInstances(context, begin, end) }
    }
}
