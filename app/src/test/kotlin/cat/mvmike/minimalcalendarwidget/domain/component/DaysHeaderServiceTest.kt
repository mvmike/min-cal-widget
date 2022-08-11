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
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.darkThemeHeaderTextColour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.lightThemeHeaderTextColour
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
    @MethodSource("startWeekDayAndThemeAndFormatWithExpectedOutput")
    fun setDayHeaders_shouldAddViewBasedOnCurrentConfigAndFormat(
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
    private fun startWeekDayAndThemeAndFormatWithExpectedOutput() = Stream.of(
        Arguments.of(
            MONDAY, Theme.DARK, Format(), listOf(
                DayHeaderTestProperties(MONDAY, "MON", darkThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.DARK, Format(), listOf(
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", darkThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.DARK, Format(), listOf(
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", darkThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            THURSDAY, Theme.DARK, Format(), listOf(
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", darkThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            FRIDAY, Theme.DARK, Format(), listOf(
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", darkThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            SATURDAY, Theme.DARK, Format(), listOf(
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", darkThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            SUNDAY, Theme.DARK, Format(), listOf(
                DayHeaderTestProperties(SUNDAY, "SUN", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", darkThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", darkThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", darkThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", darkThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", darkThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground)
            )
        ),
        Arguments.of(
            MONDAY, Theme.DARK, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(MONDAY, "M", darkThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "D", darkThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "T", darkThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "F", darkThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "S", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.DARK, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(TUESDAY, "D", darkThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "T", darkThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "F", darkThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "S", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", darkThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.DARK, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "T", darkThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "F", darkThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "S", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", darkThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "D", darkThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            THURSDAY, Theme.DARK, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(THURSDAY, "T", darkThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "F", darkThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "S", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", darkThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "D", darkThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            FRIDAY, Theme.DARK, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(FRIDAY, "F", darkThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "S", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", darkThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "D", darkThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "T", darkThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            SATURDAY, Theme.DARK, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(SATURDAY, "S", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", darkThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "D", darkThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "T", darkThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "F", darkThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            SUNDAY, Theme.DARK, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(SUNDAY, "S", darkThemeHeaderTextColour, sundayInMonthDarkThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", darkThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "D", darkThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", darkThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "T", darkThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "F", darkThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "S", darkThemeHeaderTextColour, saturdayInMonthDarkThemeCellBackground)
            )
        ),
        Arguments.of(
            MONDAY, Theme.LIGHT, Format(), listOf(
                DayHeaderTestProperties(MONDAY, "MON", lightThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.LIGHT, Format(), listOf(
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", lightThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.LIGHT, Format(), listOf(
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", lightThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            THURSDAY, Theme.LIGHT, Format(), listOf(
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", lightThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            FRIDAY, Theme.LIGHT, Format(), listOf(
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", lightThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            SATURDAY, Theme.LIGHT, Format(), listOf(
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", lightThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            SUNDAY, Theme.LIGHT, Format(), listOf(
                DayHeaderTestProperties(SUNDAY, "SUN", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "MON", lightThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "DOO", lightThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "WED", lightThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "THU", lightThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "FRI", lightThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "SAT", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground)
            )
        ),
        Arguments.of(
            MONDAY, Theme.LIGHT, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(MONDAY, "M", lightThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "D", lightThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "T", lightThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "F", lightThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "S", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.LIGHT, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(TUESDAY, "D", lightThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "T", lightThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "F", lightThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "S", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", lightThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.LIGHT, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "T", lightThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "F", lightThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "S", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", lightThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "D", lightThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            THURSDAY, Theme.LIGHT, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(THURSDAY, "T", lightThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "F", lightThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "S", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", lightThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "D", lightThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            FRIDAY, Theme.LIGHT, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(FRIDAY, "F", lightThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "S", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", lightThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "D", lightThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "T", lightThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            SATURDAY, Theme.LIGHT, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(SATURDAY, "S", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(SUNDAY, "S", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", lightThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "D", lightThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "T", lightThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "F", lightThemeHeaderTextColour)
            )
        ),
        Arguments.of(
            SUNDAY, Theme.LIGHT, Format(dayHeaderLabelLength = 1), listOf(
                DayHeaderTestProperties(SUNDAY, "S", lightThemeHeaderTextColour, sundayInMonthLightThemeCellBackground),
                DayHeaderTestProperties(MONDAY, "M", lightThemeHeaderTextColour),
                DayHeaderTestProperties(TUESDAY, "D", lightThemeHeaderTextColour),
                DayHeaderTestProperties(WEDNESDAY, "W", lightThemeHeaderTextColour),
                DayHeaderTestProperties(THURSDAY, "T", lightThemeHeaderTextColour),
                DayHeaderTestProperties(FRIDAY, "F", lightThemeHeaderTextColour),
                DayHeaderTestProperties(SATURDAY, "S", lightThemeHeaderTextColour, saturdayInMonthLightThemeCellBackground)
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
