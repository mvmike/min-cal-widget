// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual.draw

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
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
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.Collections

internal class DrawDaysHeaderUseCaseTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    private val daysHeaderRowRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("combinationOfStartWeekDayThemeConfigAndFormat")
    fun setDayHeaders_shouldAddViewBasedOnCurrentDayAndConfig(
        startWeekDay: DayOfWeek,
        theme: Theme,
        format: Format,
        dayHeaderSaturdayCellBackground: Int,
        dayHeaderSundayCellBackground: Int
    ) {
        every { SystemResolver.createDaysHeaderRow(context) } returns daysHeaderRowRv

        mockSharedPreferences()
        mockWidgetTransparency(Transparency(20))
        mockFirstDayOfWeek(startWeekDay)
        mockWidgetTheme(theme)
        mockGetDayHeaderCellSaturdayBackground(dayHeaderSaturdayCellBackground)
        mockGetDayHeaderCellSundayBackground(dayHeaderSundayCellBackground)
        mockGetWeekDaysAbbreviatedStrings()

        justRun { SystemResolver.addToDaysHeaderRow(context, daysHeaderRowRv, any(), any(), any(), any()) }
        justRun { SystemResolver.addToWidget(widgetRv, daysHeaderRowRv) }

        DrawDaysHeaderUseCase.execute(context, widgetRv, format)

        verifyWidgetTransparency()
        verifyFirstDayOfWeek()
        verifyWidgetTheme()
        verify { SystemResolver.createDaysHeaderRow(context) }
        getRotatedWeekDays(startWeekDay).forEach {
            verify { context.getString(it.getExpectedResourceId()) }
            val cellHeader = theme.getCellHeader(it)
            verify {
                when (it) {
                    SATURDAY -> verifyGetDayHeaderCellSaturdayBackground(dayHeaderSaturdayCellBackground)
                    SUNDAY -> verifyGetDayHeaderCellSundayBackground(dayHeaderSundayCellBackground)
                    else -> {}
                }
                SystemResolver.addToDaysHeaderRow(
                    context = context,
                    daysHeaderRow = daysHeaderRowRv,
                    text = it.getExpectedAbbreviatedString(format),
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
        verify { SystemResolver.addToWidget(widgetRv, daysHeaderRowRv) }
        confirmVerified(widgetRv, daysHeaderRowRv)
    }

    companion object {

        private const val dayHeaderCellSaturdayTransparentBackgroundInHex = "#40turday"
        private const val dayHeaderCellSundayTransparentBackgroundInHex = "#40Sunday"

        private const val dayHeaderCellSaturdayBackground = 65132545
        private const val dayHeaderCellSundayBackground = 65132546

        private const val dayHeaderSaturdayDarkThemeBackground = 2131034148
        private const val dayHeaderSundayDarkThemeBackground = 2131034152
        private const val dayHeaderSaturdayLightThemeBackground = 2131034149
        private const val dayHeaderSundayLightThemeBackground = 2131034153

        @JvmStatic
        @Suppress("unused")
        fun combinationOfStartWeekDayThemeConfigAndFormat() = Stream.of(
            Arguments.of(MONDAY, Theme.DARK, Format.STANDARD, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(TUESDAY, Theme.DARK, Format.REDUCED, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(WEDNESDAY, Theme.DARK, Format.STANDARD, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(THURSDAY, Theme.DARK, Format.REDUCED, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(FRIDAY, Theme.DARK, Format.STANDARD, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(SATURDAY, Theme.DARK, Format.REDUCED, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(SUNDAY, Theme.DARK, Format.STANDARD, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(MONDAY, Theme.DARK, Format.REDUCED, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(TUESDAY, Theme.DARK, Format.STANDARD, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(WEDNESDAY, Theme.DARK, Format.REDUCED, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(THURSDAY, Theme.DARK, Format.STANDARD, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(FRIDAY, Theme.DARK, Format.REDUCED, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(SATURDAY, Theme.DARK, Format.STANDARD, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(SUNDAY, Theme.DARK, Format.REDUCED, dayHeaderSaturdayDarkThemeBackground, dayHeaderSundayDarkThemeBackground),
            Arguments.of(MONDAY, Theme.LIGHT, Format.STANDARD, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(TUESDAY, Theme.LIGHT, Format.REDUCED, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(WEDNESDAY, Theme.LIGHT, Format.STANDARD, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(THURSDAY, Theme.LIGHT, Format.REDUCED, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(FRIDAY, Theme.LIGHT, Format.STANDARD, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(SATURDAY, Theme.LIGHT, Format.REDUCED, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(SUNDAY, Theme.LIGHT, Format.STANDARD, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(MONDAY, Theme.LIGHT, Format.REDUCED, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(TUESDAY, Theme.LIGHT, Format.STANDARD, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(WEDNESDAY, Theme.LIGHT, Format.REDUCED, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(THURSDAY, Theme.LIGHT, Format.STANDARD, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(FRIDAY, Theme.LIGHT, Format.REDUCED, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(SATURDAY, Theme.LIGHT, Format.STANDARD, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground),
            Arguments.of(SUNDAY, Theme.LIGHT, Format.REDUCED, dayHeaderSaturdayLightThemeBackground, dayHeaderSundayLightThemeBackground)
        )!!
    }

    private fun getRotatedWeekDays(startDayOfWeek: DayOfWeek): List<DayOfWeek> {
        val daysOfWeek = DayOfWeek.values().asList()
        Collections.rotate(daysOfWeek, -startDayOfWeek.ordinal)
        return daysOfWeek
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

    private fun DayOfWeek.getExpectedAbbreviatedString() = when (this) {
        MONDAY -> "MON"
        TUESDAY -> "TUE"
        WEDNESDAY -> "WED"
        THURSDAY -> "THU"
        FRIDAY -> "FRY"
        SATURDAY -> "SAT"
        SUNDAY -> "SUN"
    }

    private fun DayOfWeek.getExpectedAbbreviatedString(format: Format) = when (format) {
        Format.STANDARD -> this.getExpectedAbbreviatedString()
        Format.REDUCED -> this.getExpectedAbbreviatedString().take(1)
    }

    private fun mockGetDayHeaderCellSaturdayBackground(dayHeaderSaturdayCellBackground: Int) {
        every { SystemResolver.getColourAsString(context, dayHeaderSaturdayCellBackground) } returns "transparentBackgroundSaturday"
        every { SystemResolver.parseColour(dayHeaderCellSaturdayTransparentBackgroundInHex) } returns dayHeaderCellSaturdayBackground
    }

    private fun mockGetDayHeaderCellSundayBackground(dayHeaderSundayCellBackground: Int) {
        every { SystemResolver.getColourAsString(context, dayHeaderSundayCellBackground) } returns "transparentBackgroundSunday"
        every { SystemResolver.parseColour(dayHeaderCellSundayTransparentBackgroundInHex) } returns dayHeaderCellSundayBackground
    }

    private fun verifyGetDayHeaderCellSaturdayBackground(dayHeaderSaturdayCellBackground: Int) {
        SystemResolver.getColourAsString(context, dayHeaderSaturdayCellBackground)
        SystemResolver.parseColour(dayHeaderCellSaturdayTransparentBackgroundInHex)
    }

    private fun verifyGetDayHeaderCellSundayBackground(dayHeaderSundayCellBackground: Int) {
        SystemResolver.getColourAsString(context, dayHeaderSundayCellBackground)
        SystemResolver.parseColour(dayHeaderCellSundayTransparentBackgroundInHex)
    }

    private fun mockGetWeekDaysAbbreviatedStrings() = DayOfWeek.values().forEach {
        every { context.getString(it.getExpectedResourceId()) } returns it.getExpectedAbbreviatedString()
    }
}
