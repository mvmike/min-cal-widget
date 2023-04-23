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

    private val validInstanceCursors = listOf(
        InstanceCursor(
            eventId = 1,
            start = 1657097518736,
            end = 1659775918428,
            zoneId = null,
            status = 1
        ),
        InstanceCursor(
            eventId = 2,
            start = 1657097518737,
            end = 1659775918429,
            zoneId = "UTC",
            status = 0
        ),
        InstanceCursor(
            eventId = 3,
            start = 1657097518738,
            end = 1659775918430,
            zoneId = "CET",
            status = 2
        )
    )

    @Test
    fun shouldReturnEmptySetWhenQueryCannotBeExecuted() {
        mockkStatic(CalendarContract.Instances::class)
        every {
            CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end)
        } throws RuntimeException()

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).isEqualTo(emptySet<Instance>())
        verify { context.contentResolver }
        verify { CalendarResolver.getInstances(context, begin, end) }
    }

    @Test
    fun shouldReturnEmptySetWhenMovingCursorThrowsException() {
        mockkStatic(CalendarContract.Instances::class)
        every {
            CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end)
        } returns cursor
        every { cursor.moveToNext() } throws RuntimeException()
        justRun { cursor.close() }

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).isEqualTo(emptySet<Instance>())
        verify { context.contentResolver }
        verify { CalendarResolver.getInstances(context, begin, end) }
        verify { cursor.moveToNext() }
        verify { cursor.close() }
    }

    @Test
    fun shouldSkipInvalidInstance() {
        mockkStatic(CalendarContract.Instances::class)
        every {
            CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end)
        } returns cursor
        every { cursor.moveToNext() } returnsMany listOf(true, true, false)
        every { cursor.getInt(0) } returnsMany listOf(validInstanceCursors[0].eventId, validInstanceCursors[1].eventId)
        every { cursor.getLong(1) } throws RuntimeException("some weird error") andThen validInstanceCursors[1].start
        every { cursor.getLong(2) } returns validInstanceCursors[1].end
        every { cursor.getString(3) } returns validInstanceCursors[1].zoneId
        every { cursor.getInt(4) } returns validInstanceCursors[1].status
        justRun { cursor.close() }

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).containsExactlyInAnyOrder(validInstanceCursors[1].toInstance())
        verify { context.contentResolver }
        verify { CalendarResolver.getInstances(context, begin, end) }
        verify(exactly = 3) { cursor.moveToNext() }
        verify { cursor.close() }
    }

    @Test
    fun shouldFetchAllInstances() {
        mockkStatic(CalendarContract.Instances::class)
        every {
            CalendarContract.Instances.query(
                context.contentResolver,
                instanceQueryFields,
                begin,
                end
            )
        } returns cursor
        every { cursor.moveToNext() } returnsMany listOf(true, true, true, false)
        every { cursor.getInt(0) } returnsMany validInstanceCursors.map { it.eventId }
        every { cursor.getLong(1) } returnsMany validInstanceCursors.map { it.start }
        every { cursor.getLong(2) } returnsMany validInstanceCursors.map { it.end }
        every { cursor.getString(3) } returnsMany validInstanceCursors.map { it.zoneId }
        every { cursor.getInt(4) } returnsMany validInstanceCursors.map { it.status }
        justRun { cursor.close() }

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).containsExactlyInAnyOrder(
            validInstanceCursors[0].toInstance(),
            validInstanceCursors[1].toInstance(),
            validInstanceCursors[2].toInstance()
        )
        verify { context.contentResolver }
        verify { CalendarResolver.getInstances(context, begin, end) }
        verify(exactly = validInstanceCursors.size + 1) { cursor.moveToNext() }
        verify { cursor.close() }
    }

    private data class InstanceCursor(
        val eventId: Int,
        val start: Long,
        val end: Long,
        val zoneId: String?,
        val status: Int
    ) {
        fun toInstance() = Instance(
            eventId = eventId,
            start = Instant.ofEpochMilli(start),
            end = Instant.ofEpochMilli(end),
            zoneId = zoneId?.let { ZoneId.of(it) } ?: ZoneOffset.systemDefault(),
            isDeclined = status == 2
        )
    }
}