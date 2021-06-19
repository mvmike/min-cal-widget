// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.time.DayOfWeek.*
import java.util.stream.Stream

internal class DrawDaysHeaderUseCaseTest : BaseTest() {

    private val widgetRv: RemoteViews = mockk()

    private val daysHeaderRowRv: RemoteViews = mockk()

    @ParameterizedTest
    @MethodSource("combinationOfStartWeekDayAndThemeConfig")
    fun setDayHeaders_shouldAddViewBasedOnCurrentDayAndConfig(startWeekDay: DayOfWeek, theme: Theme) {
        every {systemResolver.createDaysHeaderRow(context)} returns daysHeaderRowRv
        mockSharedPreferences()
        mockFirstDayOfWeek(startWeekDay)
        mockCalendarTheme(theme)

        val rotatedWeekDays = getRotatedWeekDays(startWeekDay)
        rotatedWeekDays.forEach {
            every {context.getString(it.getExpectedResourceId())} returns it.getExpectedAbbreviatedString()
        }
        justRun { systemResolver.addToDaysHeaderRow(context, daysHeaderRowRv, any(), any()) }
        justRun { systemResolver.addToWidget(widgetRv, daysHeaderRowRv) }

        DrawDaysHeaderUseCase.execute(context, widgetRv)

        verify { systemResolver.createDaysHeaderRow(context) }
        rotatedWeekDays.forEach {
            verify {
                systemResolver.addToDaysHeaderRow(
                    context = context,
                    daysHeaderRow = daysHeaderRowRv,
                    text = it.getExpectedAbbreviatedString(),
                    layoutId = theme.getCellHeader(it)
                )
            }
        }
        verify { systemResolver.addToWidget(widgetRv, daysHeaderRowRv) }
        confirmVerified(systemResolver)
    }

    companion object {

        @JvmStatic
        @Suppress("unused", "LongMethod")
        fun combinationOfStartWeekDayAndThemeConfig(): Stream<Arguments> {
            val weekdays = DayOfWeek.values()
            val themes = Theme.values()

            return weekdays
                .map { weekday -> weekday to themes }
                .map { weekDayAndThemes ->
                    weekDayAndThemes.second.map {
                        Arguments.of(weekDayAndThemes.first, it)
                    }
                }
                .flatten()
                .stream()
        }
    }

    private fun getRotatedWeekDays(startDayOfWeek: DayOfWeek) =
        when (startDayOfWeek) {
            MONDAY -> arrayOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)
            TUESDAY -> arrayOf(TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, MONDAY)
            WEDNESDAY -> arrayOf(WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, MONDAY, TUESDAY)
            THURSDAY -> arrayOf(THURSDAY, FRIDAY, SATURDAY, SUNDAY, MONDAY, TUESDAY, WEDNESDAY)
            FRIDAY -> arrayOf(FRIDAY, SATURDAY, SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY)
            SATURDAY -> arrayOf(SATURDAY, SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)
            SUNDAY -> arrayOf(SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY)
        }

    private fun DayOfWeek.getExpectedResourceId() =
        when (this) {
            MONDAY -> R.string.monday_abb
            TUESDAY -> R.string.tuesday_abb
            WEDNESDAY -> R.string.wednesday_abb
            THURSDAY -> R.string.thursday_abb
            FRIDAY -> R.string.friday_abb
            SATURDAY -> R.string.saturday_abb
            SUNDAY -> R.string.sunday_abb
        }

    private fun DayOfWeek.getExpectedAbbreviatedString() =
        when (this) {
            MONDAY -> "MON"
            TUESDAY -> "TUE"
            WEDNESDAY -> "WED"
            THURSDAY -> "THU"
            FRIDAY -> "FRY"
            SATURDAY -> "SAT"
            SUNDAY -> "SUN"
        }
}
