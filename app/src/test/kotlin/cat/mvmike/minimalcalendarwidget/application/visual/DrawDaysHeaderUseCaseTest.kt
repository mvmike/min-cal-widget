// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import java.time.DayOfWeek
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class DrawDaysHeaderUseCaseTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    private val daysHeaderRowRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("combinationOfStartWeekDayAndThemeConfig")
    fun setDayHeaders_shouldAddViewBasedOnCurrentDayAndConfig(testProperties: DrawDaysHeaderUseCaseTestProperties) {
        every { systemResolver.createDaysHeaderRow(context) } returns daysHeaderRowRv
        mockSharedPreferences()
        mockWidgetTransparency(Transparency(20))
        mockFirstDayOfWeek(testProperties.startWeekDay)
        mockCalendarTheme(testProperties.theme)

        every { systemResolver.getColourAsString(context, testProperties.dayHeaderSaturdayCellBackground) } returns dayHeaderCellSaturdayTransparentBackground
        every { systemResolver.parseColour(dayHeaderCellSaturdayTransparentBackgroundInHex) } returns dayHeaderCellSaturdayBackground

        every { systemResolver.getColourAsString(context, testProperties.dayHeaderSundayCellBackground) } returns dayHeaderCellSundayTransparentBackground
        every { systemResolver.parseColour(dayHeaderCellSundayTransparentBackgroundInHex) } returns dayHeaderCellSundayBackground

        val rotatedWeekDays = getRotatedWeekDays(testProperties.startWeekDay)
        rotatedWeekDays.forEach {
            every { context.getString(it.getExpectedResourceId()) } returns it.getExpectedAbbreviatedString()
        }
        justRun { systemResolver.addToDaysHeaderRow(context, daysHeaderRowRv, any(), any(), any(), any()) }
        justRun { systemResolver.addToWidget(widgetRv, daysHeaderRowRv) }

        DrawDaysHeaderUseCase.execute(context, widgetRv)

        verifyWidgetTransparency()
        verifyFirstDayOfWeek()
        verifyCalendarTheme()
        verify { systemResolver.createDaysHeaderRow(context) }
        rotatedWeekDays.forEach {
            verify { context.getString(it.getExpectedResourceId()) }
            val cellHeader = testProperties.theme.getCellHeader(it)
            verify {
                when (it) {
                    SATURDAY -> {
                        systemResolver.getColourAsString(context, testProperties.dayHeaderSaturdayCellBackground)
                        systemResolver.parseColour(dayHeaderCellSaturdayTransparentBackgroundInHex)
                    }
                    SUNDAY -> {
                        systemResolver.getColourAsString(context, testProperties.dayHeaderSundayCellBackground)
                        systemResolver.parseColour(dayHeaderCellSundayTransparentBackgroundInHex)
                    }
                    else -> { }
                }
                systemResolver.addToDaysHeaderRow(
                    context = context,
                    daysHeaderRow = daysHeaderRowRv,
                    text = it.getExpectedAbbreviatedString(),
                    layoutId = cellHeader.layout,
                    viewId = 16908308,
                    dayHeaderBackgroundColour = when (it) {
                        SATURDAY -> dayHeaderCellSaturdayBackground
                        SUNDAY -> dayHeaderCellSundayBackground
                        else -> null
                    }
                )
            }
        }
        verify { systemResolver.addToWidget(widgetRv, daysHeaderRowRv) }
        confirmVerified(widgetRv, daysHeaderRowRv)
    }

    companion object {

        private const val dayHeaderCellSaturdayTransparentBackground = "transparentBackgroundSaturday"
        private const val dayHeaderCellSundayTransparentBackground = "transparentBackgroundSunday"
        private const val dayHeaderCellSaturdayTransparentBackgroundInHex = "#40turday"
        private const val dayHeaderCellSundayTransparentBackgroundInHex = "#40Sunday"

        private const val dayHeaderCellSaturdayBackground = 65132545
        private const val dayHeaderCellSundayBackground = 65132546

        @JvmStatic
        @Suppress("unused")
        fun combinationOfStartWeekDayAndThemeConfig() = Stream.of(
            DrawDaysHeaderUseCaseTestProperties(MONDAY, Theme.BLACK, 2131034148, 2131034152),
            DrawDaysHeaderUseCaseTestProperties(TUESDAY, Theme.BLACK, 2131034148, 2131034152),
            DrawDaysHeaderUseCaseTestProperties(WEDNESDAY, Theme.BLACK, 2131034148, 2131034152),
            DrawDaysHeaderUseCaseTestProperties(THURSDAY, Theme.BLACK, 2131034148, 2131034152),
            DrawDaysHeaderUseCaseTestProperties(FRIDAY, Theme.BLACK, 2131034148, 2131034152),
            DrawDaysHeaderUseCaseTestProperties(SATURDAY, Theme.BLACK, 2131034148, 2131034152),
            DrawDaysHeaderUseCaseTestProperties(SUNDAY, Theme.BLACK, 2131034148, 2131034152),
            DrawDaysHeaderUseCaseTestProperties(MONDAY, Theme.WHITE, 2131034151, 2131034155),
            DrawDaysHeaderUseCaseTestProperties(TUESDAY, Theme.WHITE, 2131034151, 2131034155),
            DrawDaysHeaderUseCaseTestProperties(WEDNESDAY, Theme.WHITE, 2131034151, 2131034155),
            DrawDaysHeaderUseCaseTestProperties(THURSDAY, Theme.WHITE, 2131034151, 2131034155),
            DrawDaysHeaderUseCaseTestProperties(FRIDAY, Theme.WHITE, 2131034151, 2131034155),
            DrawDaysHeaderUseCaseTestProperties(SATURDAY, Theme.WHITE, 2131034151, 2131034155),
            DrawDaysHeaderUseCaseTestProperties(SUNDAY, Theme.WHITE, 2131034151, 2131034155),
        )!!

        internal data class DrawDaysHeaderUseCaseTestProperties(
            val startWeekDay: DayOfWeek,
            val theme: Theme,
            val dayHeaderSaturdayCellBackground: Int,
            val dayHeaderSundayCellBackground: Int
        )
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
