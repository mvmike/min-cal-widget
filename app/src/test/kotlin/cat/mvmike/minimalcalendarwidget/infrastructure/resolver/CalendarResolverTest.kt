// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.database.Cursor
import android.provider.CalendarContract
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.Instance
import cat.mvmike.minimalcalendarwidget.domain.Instance.AllDayInstance
import cat.mvmike.minimalcalendarwidget.domain.Instance.TimedInstance
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZonedDateTime

internal class CalendarResolverTest : BaseTest() {

    private val begin = 1657097518736
    private val end = 1659775918428

    private val cursor = mockk<Cursor>()

    private val validInstanceCursors = listOf(
        InstanceCursor(1, 10, 1657097518736, 1659775918428, null, 1, 0),
        InstanceCursor(2, 20, 1657065600000, 1657152000000, "UTC", 0, 1),
        InstanceCursor(3, 30, 1657097518738, 1659775918430, "CET", 2, 0),
        InstanceCursor(4, 40, 1657097518738, 1659775918430, "America/Los_Angeles", 17, 0)
    )

    @Test
    fun shouldReturnEmptySetWhenQueryCannotBeExecuted() {
        mockkStatic(CalendarContract.Instances::class)
        every {
            CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end)
        } throws RuntimeException("some exception when querying instances")

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
        every { cursor.moveToNext() } throws RuntimeException("some exception whilst moving cursor")
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
        every { cursor.getInt(0) } returnsMany listOf(validInstanceCursors[0].id, validInstanceCursors[1].id)
        every { cursor.getInt(1) } returnsMany listOf(validInstanceCursors[0].eventId, validInstanceCursors[1].eventId)
        every { cursor.getLong(2) } throws RuntimeException("some weird error") andThen validInstanceCursors[1].start
        every { cursor.getLong(3) } returns validInstanceCursors[1].end
        every { cursor.getString(4) } returns validInstanceCursors[1].zoneId
        every { cursor.getInt(5) } returns validInstanceCursors[1].status
        every { cursor.getInt(6) } returns validInstanceCursors[1].allDay
        justRun { cursor.close() }

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).hasSize(1)
        assertThat(result).contains(
            AllDayInstance(
                id = 2,
                eventId = 20,
                isDeclined = false,
                start = LocalDate.parse("2022-07-06"),
                end = LocalDate.parse("2022-07-06")
            )
        )
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
        every { cursor.moveToNext() } returnsMany validInstanceCursors.map { true }.plus(false)
        every { cursor.getInt(0) } returnsMany validInstanceCursors.map { it.id }
        every { cursor.getInt(1) } returnsMany validInstanceCursors.map { it.eventId }
        every { cursor.getLong(2) } returnsMany validInstanceCursors.map { it.start }
        every { cursor.getLong(3) } returnsMany validInstanceCursors.map { it.end }
        every { cursor.getString(4) } returnsMany validInstanceCursors.map { it.zoneId }
        every { cursor.getInt(5) } returnsMany validInstanceCursors.map { it.status }
        every { cursor.getInt(6) } returnsMany validInstanceCursors.map { it.allDay }
        justRun { cursor.close() }

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).containsExactlyInAnyOrder(
            TimedInstance(
                id = 1,
                eventId = 10,
                isDeclined = false,
                start = ZonedDateTime.parse("2022-07-06T11:51:58.736+03:00[Europe/Moscow]"),
                end = ZonedDateTime.parse("2022-08-06T11:51:58.427+03:00[Europe/Moscow]")
            ),
            AllDayInstance(
                id = 2,
                eventId = 20,
                isDeclined = false,
                start = LocalDate.parse("2022-07-06"),
                end = LocalDate.parse("2022-07-06")
            ),
            TimedInstance(
                id = 3,
                eventId = 30,
                isDeclined = true,
                start = ZonedDateTime.parse("2022-07-06T10:51:58.738+02:00[CET]"),
                end = ZonedDateTime.parse("2022-08-06T10:51:58.429+02:00[CET]")
            ),
            TimedInstance(
                id = 4,
                eventId = 40,
                isDeclined = false,
                start = ZonedDateTime.parse("2022-07-06T01:51:58.738-07:00[America/Los_Angeles]"),
                end = ZonedDateTime.parse("2022-08-06T01:51:58.429-07:00[America/Los_Angeles]")
            )
        )
        verify { context.contentResolver }
        verify { CalendarResolver.getInstances(context, begin, end) }
        verify(exactly = validInstanceCursors.size + 1) { cursor.moveToNext() }
        verify { cursor.close() }
    }

    private data class InstanceCursor(
        val id: Int,
        val eventId: Int,
        val start: Long,
        val end: Long,
        val zoneId: String?,
        val status: Int,
        val allDay: Int
    )
}