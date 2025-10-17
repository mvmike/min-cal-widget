// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.database.Cursor
import android.provider.CalendarContract
import android.provider.CalendarContract.Calendars.CONTENT_URI
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.Calendar
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

    private val instanceCursors = listOf(
        InstanceCursor(1, 10, 1657097518736, 1659775918428, null, 1, 0),
        InstanceCursor(2, 20, 1657065600000, 1657152000000, "UTC", 0, 1),
        InstanceCursor(3, 30, 1657097518738, 1659775918430, "CET", 2, 0),
        InstanceCursor(4, 40, 1657097518738, 1659775918430, "America/Los_Angeles", 17, 0)
    )

    private val calendarCursors = listOf(
        CalendarCursor(1, "First Account", "Calendar 1", 1, 1),
        CalendarCursor(2, "First Account", "Calendar 2", 0, 1),
        CalendarCursor(3, "Second Account", "Calendar A", 1, 0),
        CalendarCursor(4, "Third Account", "Calendar $", 1, 1)
    )

    @Test
    fun shouldReturnEmptySetWhenGetInstancesQueryCannotBeExecuted() {
        mockkStatic(CalendarContract.Instances::class)
        every {
            CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end)
        } throws RuntimeException("some exception when querying instances")

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).isEmpty()
        verify { context.contentResolver }
        verify { CalendarResolver.getInstances(context, begin, end) }
    }

    @Test
    fun shouldReturnEmptySetWhenGetCalendarsQueryCannotBeExecuted() {
        every { CalendarResolver.getCalendars(context) } answers { callOriginal() }
        every {
            context.contentResolver.query(CONTENT_URI, calendarQueryFields, null, null, "calendar_displayName DESC")
        } throws RuntimeException("some exception when querying calendars")

        val result = CalendarResolver.getCalendars(context)

        assertThat(result).isEmpty()
        verify { context.contentResolver }
        verify {
            context.contentResolver.query(
                CONTENT_URI,
                calendarQueryFields,
                null,
                null,
                "calendar_displayName DESC"
            )
        }
        verify { CalendarResolver.getCalendars(context) }
    }

    @Test
    fun shouldReturnEmptySetWhenMovingInstancesCursorThrowsException() {
        mockkStatic(CalendarContract.Instances::class)
        every {
            CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end)
        } returns cursor
        every { cursor.moveToNext() } throws RuntimeException("some exception whilst moving cursor")
        justRun { cursor.close() }

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).isEmpty()
        verify { context.contentResolver }
        verify { CalendarResolver.getInstances(context, begin, end) }
        verify { cursor.moveToNext() }
        verify { cursor.close() }
    }

    @Test
    fun shouldReturnEmptySetWhenMovingCalendarsCursorThrowsException() {
        every { CalendarResolver.getCalendars(context) } answers { callOriginal() }
        every {
            context.contentResolver.query(CONTENT_URI, calendarQueryFields, null, null, "calendar_displayName DESC")
        } returns cursor
        every { cursor.moveToNext() } throws RuntimeException("some exception whilst moving cursor")
        justRun { cursor.close() }

        val result = CalendarResolver.getCalendars(context)

        assertThat(result).isEmpty()
        verify { context.contentResolver }
        verify { CalendarResolver.getCalendars(context) }
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
        every { cursor.getInt(0) } returnsMany listOf(instanceCursors[0].id, instanceCursors[1].id)
        every { cursor.getInt(1) } returnsMany listOf(instanceCursors[0].calendarId, instanceCursors[1].calendarId)
        every { cursor.getLong(2) } throws RuntimeException("some weird error") andThen instanceCursors[1].start
        every { cursor.getLong(3) } returns instanceCursors[1].end
        every { cursor.getString(4) } returns instanceCursors[1].zoneId
        every { cursor.getInt(5) } returns instanceCursors[1].status
        every { cursor.getInt(6) } returns instanceCursors[1].allDay
        justRun { cursor.close() }

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).containsExactlyInAnyOrder(
            AllDayInstance(
                id = 2,
                calendarId = 20,
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
    fun shouldSkipInvalidCalendar() {
        every { CalendarResolver.getCalendars(context) } answers { callOriginal() }
        every {
            context.contentResolver.query(CONTENT_URI, calendarQueryFields, null, null, "calendar_displayName DESC")
        } returns cursor
        every { cursor.moveToNext() } returnsMany listOf(true, true, false)
        every { cursor.getInt(0) } returnsMany listOf(calendarCursors[0].id, calendarCursors[1].id)
        every { cursor.getString(1) } returnsMany listOf(calendarCursors[0].accountName, calendarCursors[1].accountName)
        every { cursor.getString(2) } throws RuntimeException("some weird error") andThen calendarCursors[1].displayName
        every { cursor.getInt(3) } returns calendarCursors[1].isPrimary
        every { cursor.getInt(4) } returns calendarCursors[1].isVisible
        justRun { cursor.close() }

        val result = CalendarResolver.getCalendars(context)

        assertThat(result).containsExactlyInAnyOrder(
            Calendar(
                id = 2,
                accountName = "First Account",
                displayName = "Calendar 2",
                isPrimary = false,
                isVisible = true
            )
        )
        verify { context.contentResolver }
        verify { CalendarResolver.getCalendars(context) }
        verify(exactly = 3) { cursor.moveToNext() }
        verify { cursor.close() }
    }

    @Test
    fun shouldFetchAllInstances() {
        mockkStatic(CalendarContract.Instances::class)
        every {
            CalendarContract.Instances.query(context.contentResolver, instanceQueryFields, begin, end)
        } returns cursor
        every { cursor.moveToNext() } returnsMany instanceCursors.map { true }.plus(false)
        every { cursor.getInt(0) } returnsMany instanceCursors.map { it.id }
        every { cursor.getInt(1) } returnsMany instanceCursors.map { it.calendarId }
        every { cursor.getLong(2) } returnsMany instanceCursors.map { it.start }
        every { cursor.getLong(3) } returnsMany instanceCursors.map { it.end }
        every { cursor.getString(4) } returnsMany instanceCursors.map { it.zoneId }
        every { cursor.getInt(5) } returnsMany instanceCursors.map { it.status }
        every { cursor.getInt(6) } returnsMany instanceCursors.map { it.allDay }
        justRun { cursor.close() }

        val result = CalendarResolver.getInstances(context, begin, end)

        assertThat(result).containsExactlyInAnyOrder(
            TimedInstance(
                id = 1,
                calendarId = 10,
                isDeclined = false,
                start = ZonedDateTime.parse("2022-07-06T11:51:58.736+03:00[Europe/Moscow]"),
                end = ZonedDateTime.parse("2022-08-06T11:51:58.427+03:00[Europe/Moscow]")
            ),
            AllDayInstance(
                id = 2,
                calendarId = 20,
                isDeclined = false,
                start = LocalDate.parse("2022-07-06"),
                end = LocalDate.parse("2022-07-06")
            ),
            TimedInstance(
                id = 3,
                calendarId = 30,
                isDeclined = true,
                start = ZonedDateTime.parse("2022-07-06T10:51:58.738+02:00[CET]"),
                end = ZonedDateTime.parse("2022-08-06T10:51:58.429+02:00[CET]")
            ),
            TimedInstance(
                id = 4,
                calendarId = 40,
                isDeclined = false,
                start = ZonedDateTime.parse("2022-07-06T01:51:58.738-07:00[America/Los_Angeles]"),
                end = ZonedDateTime.parse("2022-08-06T01:51:58.429-07:00[America/Los_Angeles]")
            )
        )
        verify { context.contentResolver }
        verify { CalendarResolver.getInstances(context, begin, end) }
        verify(exactly = instanceCursors.size + 1) { cursor.moveToNext() }
        verify { cursor.close() }
    }

    @Test
    fun shouldFetchAllCalendars() {
        every { CalendarResolver.getCalendars(context) } answers { callOriginal() }
        every {
            context.contentResolver.query(CONTENT_URI, calendarQueryFields, null, null, "calendar_displayName DESC")
        } returns cursor
        every { cursor.moveToNext() } returnsMany calendarCursors.map { true }.plus(false)
        every { cursor.getInt(0) } returnsMany calendarCursors.map { it.id }
        every { cursor.getString(1) } returnsMany calendarCursors.map { it.accountName }
        every { cursor.getString(2) } returnsMany calendarCursors.map { it.displayName }
        every { cursor.getInt(3) } returnsMany calendarCursors.map { it.isPrimary }
        every { cursor.getInt(4) } returnsMany calendarCursors.map { it.isVisible }
        justRun { cursor.close() }

        val result = CalendarResolver.getCalendars(context)

        assertThat(result).containsExactly(
            Calendar(
                id = 1,
                accountName = "First Account",
                displayName = "Calendar 1",
                isPrimary = true,
                isVisible = true
            ),
            Calendar(
                id = 2,
                accountName = "First Account",
                displayName = "Calendar 2",
                isPrimary = false,
                isVisible = true
            ),
            Calendar(
                id = 3,
                accountName = "Second Account",
                displayName = "Calendar A",
                isPrimary = true,
                isVisible = false
            ),
            Calendar(
                id = 4,
                accountName = "Third Account",
                displayName = "Calendar $",
                isPrimary = true,
                isVisible = true
            )
        )
        verify { context.contentResolver }
        verify { CalendarResolver.getCalendars(context) }
        verify(exactly = calendarCursors.size + 1) { cursor.moveToNext() }
        verify { cursor.close() }
    }

    private data class InstanceCursor(
        val id: Int,
        val calendarId: Int,
        val start: Long,
        val end: Long,
        val zoneId: String?,
        val status: Int,
        val allDay: Int
    )

    private data class CalendarCursor(
        val id: Int,
        val accountName: String,
        val displayName: String,
        val isPrimary: Int,
        val isVisible: Int
    )
}