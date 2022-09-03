// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.cellViewId
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.darkThemeMainTextColour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.lightThemeMainTextColour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.saturdayInMonthDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.saturdayInMonthLightThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.sundayInMonthDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.sundayInMonthLightThemeCellBackground
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
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

internal class DaysHeaderServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    private val daysHeaderRowRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getStartWeekDayAndThemeAndFormatWithExpectedOutput")
    fun draw_shouldAddViewBasedOnCurrentConfigAndFormat(
        startWeekDay: DayOfWeek,
        theme: Theme,
        format: Format,
        expectedDayHeaders: List<DayHeaderTestProperties>
    ) {
        every { GraphicResolver.createDaysHeaderRow(context) } returns daysHeaderRowRv

        mockSharedPreferences()
        mockWidgetTransparency(Transparency(20))
        mockFirstDayOfWeek(startWeekDay)
        mockWidgetTheme(theme)
        expectedDayHeaders.forEach {
            mockGetDayHeaderCellBackground(it.cellBackground)
            val resourceAndTranslation = it.dayOfWeek.getExpectedResourceIdAndTranslation()
            every { context.getString(resourceAndTranslation.first) } returns resourceAndTranslation.second
        }

        justRun { GraphicResolver.addToDaysHeaderRow(context, daysHeaderRowRv, any(), any(), any(), any(), any(), any()) }
        justRun { GraphicResolver.addToWidget(widgetRv, daysHeaderRowRv) }

        DaysHeaderService.draw(context, widgetRv, format)

        verifyWidgetTransparency()
        verifyFirstDayOfWeek()
        verifyWidgetTheme()
        verify(exactly = 1) { GraphicResolver.createDaysHeaderRow(context) }
        expectedDayHeaders.forEach {
            verify { context.getString(it.dayOfWeek.getExpectedResourceIdAndTranslation().first) }
            verifyGetDayHeaderCellBackground(it.cellBackground)
        }
        verifyOrder {
            expectedDayHeaders.forEach {
                GraphicResolver.addToDaysHeaderRow(
                    context = context,
                    daysHeaderRow = daysHeaderRowRv,
                    text = it.expectedHeaderText,
                    textColour = it.expectedHeaderTextColour,
                    layoutId = theme.getCellHeader(it.dayOfWeek).layout,
                    viewId = cellViewId,
                    dayHeaderBackgroundColour = it.cellBackground,
                    textRelativeSize = format.headerTextRelativeSize
                )
            }
        }
        verify(exactly = 1) { GraphicResolver.addToWidget(widgetRv, daysHeaderRowRv) }
        confirmVerified(widgetRv, daysHeaderRowRv)
    }

    @Suppress("UnusedPrivateMember", "LongMethod")
    private fun getStartWeekDayAndThemeAndFormatWithExpectedOutput() = Stream.of(
        Arguments.of(
            MONDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(MONDAY, "MON", darkThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", darkThemeMainTextColour)
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", darkThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeMainTextColour)
            )
        ),
        Arguments.of(
            THURSDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", darkThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeMainTextColour)
            )
        ),
        Arguments.of(
            FRIDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", darkThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeMainTextColour)
            )
        ),
        Arguments.of(
            SATURDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", darkThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeMainTextColour)
            )
        ),
        Arguments.of(
            SUNDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", darkThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground)
            )
        ),
        Arguments.of(
            MONDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(MONDAY, "M", darkThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "D", darkThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "T", darkThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "F", darkThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "S", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(TUESDAY, "D", darkThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "T", darkThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "F", darkThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "S", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", darkThemeMainTextColour)
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "T", darkThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "F", darkThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "S", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", darkThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "D", darkThemeMainTextColour)
            )
        ),
        Arguments.of(
            THURSDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(THURSDAY, "T", darkThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "F", darkThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "S", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", darkThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "D", darkThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeMainTextColour)
            )
        ),
        Arguments.of(
            FRIDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(FRIDAY, "F", darkThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "S", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", darkThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "D", darkThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "T", darkThemeMainTextColour)
            )
        ),
        Arguments.of(
            SATURDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(SATURDAY, "S", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", darkThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "D", darkThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "T", darkThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "F", darkThemeMainTextColour)
            )
        ),
        Arguments.of(
            SUNDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(SUNDAY, "S", darkThemeMainTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", darkThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "D", darkThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "T", darkThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "F", darkThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "S", darkThemeMainTextColour, saturdayInMonthDarkThemeCellBackground)
            )
        ),
        Arguments.of(
            MONDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(MONDAY, "MON", lightThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", lightThemeMainTextColour)
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", lightThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeMainTextColour)
            )
        ),
        Arguments.of(
            THURSDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", lightThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeMainTextColour)
            )
        ),
        Arguments.of(
            FRIDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", lightThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeMainTextColour)
            )
        ),
        Arguments.of(
            SATURDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", lightThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeMainTextColour)
            )
        ),
        Arguments.of(
            SUNDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", lightThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground)
            )
        ),
        Arguments.of(
            MONDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(MONDAY, "M", lightThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "D", lightThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "T", lightThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "F", lightThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "S", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(TUESDAY, "D", lightThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "T", lightThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "F", lightThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "S", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", lightThemeMainTextColour)
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "T", lightThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "F", lightThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "S", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", lightThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "D", lightThemeMainTextColour)
            )
        ),
        Arguments.of(
            THURSDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(THURSDAY, "T", lightThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "F", lightThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "S", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", lightThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "D", lightThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeMainTextColour)
            )
        ),
        Arguments.of(
            FRIDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(FRIDAY, "F", lightThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "S", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", lightThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "D", lightThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "T", lightThemeMainTextColour)
            )
        ),
        Arguments.of(
            SATURDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(SATURDAY, "S", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", lightThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "D", lightThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "T", lightThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "F", lightThemeMainTextColour)
            )
        ),
        Arguments.of(
            SUNDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(SUNDAY, "S", lightThemeMainTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", lightThemeMainTextColour),
                DayHeaderTestProperties(TUESDAY, "D", lightThemeMainTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeMainTextColour),
                DayHeaderTestProperties(THURSDAY, "T", lightThemeMainTextColour),
                DayHeaderTestProperties(FRIDAY, "F", lightThemeMainTextColour),
                DayHeaderTestProperties(SATURDAY, "S", lightThemeMainTextColour, saturdayInMonthLightThemeCellBackground)
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
            every { GraphicResolver.getColourAsString(context, dayHeaderCellBackground) } returns stringColour
            every { GraphicResolver.parseColour("#40${stringColour.takeLast(6)}") } returns dayHeaderCellBackground
        }

    private fun verifyGetDayHeaderCellBackground(dayHeaderCellBackground: Int?) =
        dayHeaderCellBackground?.let {
            val stringColour = "transparentBackground$dayHeaderCellBackground"
            verify { GraphicResolver.getColourAsString(context, dayHeaderCellBackground) }
            verify { GraphicResolver.parseColour("#40${stringColour.takeLast(6)}") }
        }

    internal data class DayHeaderTestProperties(
        val dayOfWeek: DayOfWeek,
        val expectedHeaderText: String,
        val expectedHeaderTextColour: Int,
        val cellBackground: Int? = null
    )
}
