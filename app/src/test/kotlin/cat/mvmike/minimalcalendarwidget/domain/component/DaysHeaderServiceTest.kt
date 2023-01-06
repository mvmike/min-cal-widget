// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.CELL_VIEW_ID
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.DARK_THEME_MAIN_TEXT_COLOUR
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.LIGHT_THEME_MAIN_TEXT_COLOUR
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
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
                    viewId = CELL_VIEW_ID,
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
                DayHeaderTestProperties(MONDAY, "MON", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "DOO", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "WED", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "THU", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "FRI", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "SAT", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "SUN", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(TUESDAY, "DOO", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "WED", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "THU", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "FRI", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "SAT", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "SUN", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "MON", DARK_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(WEDNESDAY, "WED", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "THU", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "FRI", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "SAT", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "SUN", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "MON", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "DOO", DARK_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            THURSDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(THURSDAY, "THU", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "FRI", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "SAT", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "SUN", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "MON", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "DOO", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "WED", DARK_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            FRIDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(FRIDAY, "FRI", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "SAT", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "SUN", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "MON", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "DOO", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "WED", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "THU", DARK_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            SATURDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(SATURDAY, "SAT", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "SUN", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "MON", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "DOO", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "WED", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "THU", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "FRI", DARK_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            SUNDAY, Theme.DARK, Format(220), listOf(
                DayHeaderTestProperties(SUNDAY, "SUN", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "MON", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "DOO", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "WED", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "THU", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "FRI", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "SAT", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND)
            )
        ),
        Arguments.of(
            MONDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(MONDAY, "M", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "D", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "W", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "T", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "F", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(TUESDAY, "D", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "W", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "T", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "F", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "M", DARK_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(WEDNESDAY, "W", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "T", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "F", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "M", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "D", DARK_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            THURSDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(THURSDAY, "T", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "F", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "M", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "D", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "W", DARK_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            FRIDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(FRIDAY, "F", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "M", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "D", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "W", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "T", DARK_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            SATURDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(SATURDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "M", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "D", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "W", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "T", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "F", DARK_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            SUNDAY, Theme.DARK, Format(150), listOf(
                DayHeaderTestProperties(SUNDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "M", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "D", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "W", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "T", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "F", DARK_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "S", DARK_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_DARK_THEME_CELL_BACKGROUND)
            )
        ),
        Arguments.of(
            MONDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(MONDAY, "MON", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "DOO", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "WED", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "THU", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "FRI", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "SAT", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "SUN", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(TUESDAY, "DOO", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "WED", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "THU", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "FRI", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "SAT", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "SUN", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "MON", LIGHT_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(WEDNESDAY, "WED", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "THU", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "FRI", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "SAT", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "SUN", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "MON", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "DOO", LIGHT_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            THURSDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(THURSDAY, "THU", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "FRI", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "SAT", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "SUN", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "MON", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "DOO", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "WED", LIGHT_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            FRIDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(FRIDAY, "FRI", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "SAT", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "SUN", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "MON", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "DOO", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "WED", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "THU", LIGHT_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            SATURDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(SATURDAY, "SAT", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "SUN", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "MON", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "DOO", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "WED", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "THU", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "FRI", LIGHT_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            SUNDAY, Theme.LIGHT, Format(220), listOf(
                DayHeaderTestProperties(SUNDAY, "SUN", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "MON", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "DOO", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "WED", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "THU", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "FRI", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "SAT", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
            )
        ),
        Arguments.of(
            MONDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(MONDAY, "M", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "D", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "W", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "T", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "F", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(TUESDAY, "D", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "W", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "T", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "F", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "M", LIGHT_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(WEDNESDAY, "W", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "T", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "F", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "M", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "D", LIGHT_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            THURSDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(THURSDAY, "T", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "F", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "M", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "D", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "W", LIGHT_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            FRIDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(FRIDAY, "F", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "M", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "D", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "W", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "T", LIGHT_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            SATURDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(SATURDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(SUNDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "M", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "D", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "W", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "T", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "F", LIGHT_THEME_MAIN_TEXT_COLOUR)
            )
        ),
        Arguments.of(
            SUNDAY, Theme.LIGHT, Format(150), listOf(
                DayHeaderTestProperties(SUNDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SUNDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND),
                DayHeaderTestProperties(MONDAY, "M", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(TUESDAY, "D", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(WEDNESDAY, "W", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(THURSDAY, "T", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(FRIDAY, "F", LIGHT_THEME_MAIN_TEXT_COLOUR),
                DayHeaderTestProperties(SATURDAY, "S", LIGHT_THEME_MAIN_TEXT_COLOUR, SATURDAY_IN_MONTH_LIGHT_THEME_CELL_BACKGROUND)
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
