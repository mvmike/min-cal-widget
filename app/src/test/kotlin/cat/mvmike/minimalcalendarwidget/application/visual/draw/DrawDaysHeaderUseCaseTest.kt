// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual.draw

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.saturdayInMonthDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.saturdayInMonthLightThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.sundayInMonthDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.sundayInMonthLightThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.cellViewId
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY
import java.util.stream.Stream

internal class DrawDaysHeaderUseCaseTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    private val daysHeaderRowRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("startWeekDayAndThemeAndFormatWithExpectedOutput")
    fun setDayHeaders_shouldAddViewBasedOnCurrentConfigAndFormat(
        startWeekDay: DayOfWeek,
        theme: Theme,
        format: Format,
        expectedDayHeaders: List<DayHeaderTestProperties>
    ) {
        every { SystemResolver.createDaysHeaderRow(context) } returns daysHeaderRowRv

        mockSharedPreferences()
        mockWidgetTransparency(Transparency(20))
        mockFirstDayOfWeek(startWeekDay)
        mockWidgetTheme(theme)
        expectedDayHeaders.forEach {
            mockGetDayHeaderCellBackground(it.cellBackground)
            val resourceAndTranslation = it.dayOfWeek.getExpectedResourceIdAndTranslation()
            every { context.getString(resourceAndTranslation.first) } returns resourceAndTranslation.second
        }

        justRun { SystemResolver.addToDaysHeaderRow(context, daysHeaderRowRv, any(), any(), any(), any()) }
        justRun { SystemResolver.addToWidget(widgetRv, daysHeaderRowRv) }

        DrawDaysHeaderUseCase.execute(context, widgetRv, format)

        verifyWidgetTransparency()
        verifyFirstDayOfWeek()
        verifyWidgetTheme()
        verify(exactly = 1) { SystemResolver.createDaysHeaderRow(context) }
        expectedDayHeaders.forEach {
            verify { context.getString(it.dayOfWeek.getExpectedResourceIdAndTranslation().first) }
            verifyGetDayHeaderCellBackground(it.cellBackground)
        }
        verifyOrder {
            expectedDayHeaders.forEach {
                SystemResolver.addToDaysHeaderRow(
                    context = context,
                    daysHeaderRow = daysHeaderRowRv,
                    text = it.expectedHeaderString,
                    layoutId = theme.getCellHeader(it.dayOfWeek).layout,
                    viewId = cellViewId,
                    dayHeaderBackgroundColour = it.cellBackground
                )
            }
        }
        verify(exactly = 1) { SystemResolver.addToWidget(widgetRv, daysHeaderRowRv) }
        confirmVerified(widgetRv, daysHeaderRowRv)
    }

    @Suppress("unused", "LongMethod")
    private fun startWeekDayAndThemeAndFormatWithExpectedOutput() = Stream.of(
        Arguments.of(
            MONDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthDarkThemeCellBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON")
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO")
            )
        ),
        Arguments.of(
            THURSDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED")
            )
        ),
        Arguments.of(
            FRIDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU")
            )
        ),
        Arguments.of(
            SATURDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI")
            )
        ),
        Arguments.of(
            SUNDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthDarkThemeCellBackground)
            )
        ),
        Arguments.of(
            MONDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthDarkThemeCellBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M")
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D")
            )
        ),
        Arguments.of(
            THURSDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W")
            )
        ),
        Arguments.of(
            FRIDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T")
            )
        ),
        Arguments.of(
            SATURDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F")
            )
        ),
        Arguments.of(
            SUNDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthDarkThemeCellBackground)
            )
        ),
        Arguments.of(
            MONDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthLightThemeCellBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON")
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO")
            )
        ),
        Arguments.of(
            THURSDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED")
            )
        ),
        Arguments.of(
            FRIDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU")
            )
        ),
        Arguments.of(
            SATURDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI")
            )
        ),
        Arguments.of(
            SUNDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(SUNDAY, "SUN", sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT", saturdayInMonthLightThemeCellBackground)
            )
        ),
        Arguments.of(
            MONDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthLightThemeCellBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M")
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D")
            )
        ),
        Arguments.of(
            THURSDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W")
            )
        ),
        Arguments.of(
            FRIDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T")
            )
        ),
        Arguments.of(
            SATURDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F")
            )
        ),
        Arguments.of(
            SUNDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(SUNDAY, "S", sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", saturdayInMonthLightThemeCellBackground)
            )
        )
    )!!

    private fun DayOfWeek.getExpectedResourceIdAndTranslation() =
        when (this) {
            MONDAY -> Pair(R.string.monday_abb, "MONDAY")
            TUESDAY -> Pair(R.string.tuesday_abb, "DOOMSDAY")
            WEDNESDAY -> Pair(R.string.wednesday_abb, "WEDNESDAY")
            THURSDAY -> Pair(R.string.thursday_abb, "THURSDAY")
            FRIDAY -> Pair(R.string.friday_abb, "FRIDAY")
            SATURDAY -> Pair(R.string.saturday_abb, "SATURDAY")
            SUNDAY -> Pair(R.string.sunday_abb, "SUNDAY")
        }

    private fun mockGetDayHeaderCellBackground(dayHeaderCellBackground: Int?) =
        dayHeaderCellBackground?.let {
            val stringColour = "transparentBackground$dayHeaderCellBackground"
            every { SystemResolver.getColourAsString(context, dayHeaderCellBackground) } returns stringColour
            every { SystemResolver.parseColour("#40${stringColour.takeLast(6)}") } returns dayHeaderCellBackground
        }

    private fun verifyGetDayHeaderCellBackground(dayHeaderCellBackground: Int?) =
        dayHeaderCellBackground?.let {
            val stringColour = "transparentBackground$dayHeaderCellBackground"
            verify { SystemResolver.getColourAsString(context, dayHeaderCellBackground) }
            verify { SystemResolver.parseColour("#40${stringColour.takeLast(6)}") }
        }

    internal data class DayHeaderTestProperties(
        val dayOfWeek: DayOfWeek,
        val expectedHeaderString: String,
        val cellBackground: Int? = null
    )
}
