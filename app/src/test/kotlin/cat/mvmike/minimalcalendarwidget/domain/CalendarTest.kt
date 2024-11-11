// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate

internal class CalendarTest : BaseTest() {

    @Test
    fun getCalendars_shouldReturnEmpty_whenReadCalendarIsNotPermitted() {
        mockIsReadCalendarPermitted(false)

        val instances = getInstances(context, LocalDate.MIN, LocalDate.MAX)

        assertThat(instances).isEmpty()
        verifyIsReadCalendarPermitted()
    }

    @ParameterizedTest
    @MethodSource("getListsOfExpectedCalendars")
    fun getCalendars_shouldReturnAllCalendars(expectedCalendars: List<Calendar>) {
        mockIsReadCalendarPermitted(true)
        every { CalendarResolver.getCalendars(context) } returns expectedCalendars

        val calendars = getCalendars(context)

        assertThat(calendars).isEqualTo(expectedCalendars)
        verifyIsReadCalendarPermitted()
        verify { CalendarResolver.getCalendars(context) }
    }

    private fun getListsOfExpectedCalendars() = listOf(
        emptyList(),
        listOf(
            Calendar(
                id = random.nextInt(),
                accountName = "First Account",
                displayName = "Calendar ${random.nextInt()}",
                isPrimary = true,
                isVisible = true
            ),
            Calendar(
                id = random.nextInt(),
                accountName = "First Account",
                displayName = "Calendar ${random.nextInt()}",
                isPrimary = false,
                isVisible = true
            ),
            Calendar(
                id = random.nextInt(),
                accountName = "First Account",
                displayName = "Calendar ${random.nextInt()}",
                isPrimary = false,
                isVisible = false
            ),
            Calendar(
                id = random.nextInt(),
                accountName = "Second Account",
                displayName = "Calendar ${random.nextInt()}",
                isPrimary = true,
                isVisible = true
            )
        )
    )
}