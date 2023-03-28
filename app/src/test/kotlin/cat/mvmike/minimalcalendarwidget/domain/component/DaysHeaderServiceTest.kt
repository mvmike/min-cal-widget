// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
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
        mockkObject(ActionableView.RowHeader)

        every { GraphicResolver.createDaysHeaderRow(context) } returns daysHeaderRowRv

        mockSharedPreferences()
        mockWidgetTransparency(Transparency(20))
        mockFirstDayOfWeek(startWeekDay)
        mockWidgetTheme(theme)
        expectedDayHeaders.forEach {
            mockGetDayHeaderCellBackground(it.getCellHeader(theme).background)
            val resourceAndTranslation = it.dayOfWeek.getExpectedResourceIdAndTranslation()
            every { context.getString(resourceAndTranslation.first) } returns resourceAndTranslation.second
        }

        justRun {
            GraphicResolver.addToDaysHeaderRow(context, daysHeaderRowRv, any(), any(), any(), any(), any(), any())
        }
        justRun { GraphicResolver.addToWidget(widgetRv, daysHeaderRowRv) }
        justRun { ActionableView.RowHeader.addListener(context, widgetRv) }

        DaysHeaderService.draw(context, widgetRv, format)

        verifyWidgetTransparency()
        verifyFirstDayOfWeek()
        verifyWidgetTheme()
        verify(exactly = 1) { GraphicResolver.createDaysHeaderRow(context) }
        expectedDayHeaders.forEach {
            verify { context.getString(it.dayOfWeek.getExpectedResourceIdAndTranslation().first) }
            verifyGetDayHeaderCellBackground(it.getCellHeader(theme).background)
        }
        verifyOrder {
            expectedDayHeaders.forEach {
                GraphicResolver.addToDaysHeaderRow(
                    context = context,
                    daysHeaderRowRemoteView = daysHeaderRowRv,
                    text = it.expectedHeaderText,
                    textColour = it.getCellHeader(theme).textColour,
                    layoutId = it.getCellHeader(theme).layout,
                    viewId = it.getCellHeader(theme).id,
                    dayHeaderBackgroundColour = it.getCellHeader(theme).background,
                    textRelativeSize = format.headerTextRelativeSize
                )
            }
        }
        verify(exactly = 1) { GraphicResolver.addToWidget(widgetRv, daysHeaderRowRv) }
        verify(exactly = 1) { ActionableView.RowHeader.addListener(context, widgetRv) }
        confirmVerified(widgetRv, daysHeaderRowRv)
    }

    private fun getStartWeekDayAndThemeAndFormatWithExpectedOutput() = Stream.of(
        Arguments.of(
            MONDAY,
            Theme.DARK,
            Format(220),
            listOf(
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT"),
                DayHeaderTestProperties(SUNDAY, "SUN")
            )
        ),
        Arguments.of(
            TUESDAY,
            Theme.DARK,
            Format(220),
            listOf(
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT"),
                DayHeaderTestProperties(SUNDAY, "SUN"),
                DayHeaderTestProperties(MONDAY, "MON")
            )
        ),
        Arguments.of(
            WEDNESDAY,
            Theme.DARK,
            Format(220),
            listOf(
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT"),
                DayHeaderTestProperties(SUNDAY, "SUN"),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO")
            )
        ),
        Arguments.of(
            THURSDAY,
            Theme.DARK,
            Format(220),
            listOf(
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT"),
                DayHeaderTestProperties(SUNDAY, "SUN"),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED")
            )
        ),
        Arguments.of(
            FRIDAY,
            Theme.DARK,
            Format(220),
            listOf(
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT"),
                DayHeaderTestProperties(SUNDAY, "SUN"),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU")
            )
        ),
        Arguments.of(
            SATURDAY,
            Theme.DARK,
            Format(220),
            listOf(
                DayHeaderTestProperties(SATURDAY, "SAT"),
                DayHeaderTestProperties(SUNDAY, "SUN"),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI")
            )
        ),
        Arguments.of(
            SUNDAY,
            Theme.DARK,
            Format(220),
            listOf(
                DayHeaderTestProperties(SUNDAY, "SUN"),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT")
            )
        ),
        Arguments.of(
            MONDAY,
            Theme.DARK,
            Format(150),
            listOf(
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S"),
                DayHeaderTestProperties(SUNDAY, "S")
            )
        ),
        Arguments.of(
            TUESDAY,
            Theme.DARK,
            Format(150),
            listOf(
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S"),
                DayHeaderTestProperties(SUNDAY, "S"),
                DayHeaderTestProperties(MONDAY, "M")
            )
        ),
        Arguments.of(
            WEDNESDAY,
            Theme.DARK,
            Format(150),
            listOf(
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S"),
                DayHeaderTestProperties(SUNDAY, "S"),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D")
            )
        ),
        Arguments.of(
            THURSDAY,
            Theme.DARK,
            Format(150),
            listOf(
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S"),
                DayHeaderTestProperties(SUNDAY, "S"),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W")
            )
        ),
        Arguments.of(
            FRIDAY,
            Theme.DARK,
            Format(150),
            listOf(
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S"),
                DayHeaderTestProperties(SUNDAY, "S"),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T")
            )
        ),
        Arguments.of(
            SATURDAY,
            Theme.DARK,
            Format(150),
            listOf(
                DayHeaderTestProperties(SATURDAY, "S"),
                DayHeaderTestProperties(SUNDAY, "S"),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F")
            )
        ),
        Arguments.of(
            SUNDAY,
            Theme.DARK,
            Format(150),
            listOf(
                DayHeaderTestProperties(SUNDAY, "S"),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S")
            )
        ),
        Arguments.of(
            MONDAY,
            Theme.LIGHT,
            Format(220),
            listOf(
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT"),
                DayHeaderTestProperties(SUNDAY, "SUN")
            )
        ),
        Arguments.of(
            TUESDAY,
            Theme.LIGHT,
            Format(220),
            listOf(
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT"),
                DayHeaderTestProperties(SUNDAY, "SUN"),
                DayHeaderTestProperties(MONDAY, "MON")
            )
        ),
        Arguments.of(
            WEDNESDAY,
            Theme.LIGHT,
            Format(220),
            listOf(
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT"),
                DayHeaderTestProperties(SUNDAY, "SUN"),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO")
            )
        ),
        Arguments.of(
            THURSDAY,
            Theme.LIGHT,
            Format(220),
            listOf(
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT"),
                DayHeaderTestProperties(SUNDAY, "SUN"),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED")
            )
        ),
        Arguments.of(
            FRIDAY,
            Theme.LIGHT,
            Format(220),
            listOf(
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT"),
                DayHeaderTestProperties(SUNDAY, "SUN"),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU")
            )
        ),
        Arguments.of(
            SATURDAY,
            Theme.LIGHT,
            Format(220),
            listOf(
                DayHeaderTestProperties(SATURDAY, "SAT"),
                DayHeaderTestProperties(SUNDAY, "SUN"),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI")
            )
        ),
        Arguments.of(
            SUNDAY,
            Theme.LIGHT,
            Format(220),
            listOf(
                DayHeaderTestProperties(SUNDAY, "SUN"),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "DOO"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRI"),
                DayHeaderTestProperties(SATURDAY, "SAT")
            )
        ),
        Arguments.of(
            MONDAY,
            Theme.LIGHT,
            Format(150),
            listOf(
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S"),
                DayHeaderTestProperties(SUNDAY, "S")
            )
        ),
        Arguments.of(
            TUESDAY,
            Theme.LIGHT,
            Format(150),
            listOf(
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S"),
                DayHeaderTestProperties(SUNDAY, "S"),
                DayHeaderTestProperties(MONDAY, "M")
            )
        ),
        Arguments.of(
            WEDNESDAY,
            Theme.LIGHT,
            Format(150),
            listOf(
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S"),
                DayHeaderTestProperties(SUNDAY, "S"),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D")
            )
        ),
        Arguments.of(
            THURSDAY,
            Theme.LIGHT,
            Format(150),
            listOf(
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S"),
                DayHeaderTestProperties(SUNDAY, "S"),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W")
            )
        ),
        Arguments.of(
            FRIDAY,
            Theme.LIGHT,
            Format(150),
            listOf(
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S"),
                DayHeaderTestProperties(SUNDAY, "S"),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T")
            )
        ),
        Arguments.of(
            SATURDAY,
            Theme.LIGHT,
            Format(150),
            listOf(
                DayHeaderTestProperties(SATURDAY, "S"),
                DayHeaderTestProperties(SUNDAY, "S"),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F")
            )
        ),
        Arguments.of(
            SUNDAY,
            Theme.LIGHT,
            Format(150),
            listOf(
                DayHeaderTestProperties(SUNDAY, "S"),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "D"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S")
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
        val expectedHeaderText: String
    ) {
        fun getCellHeader(theme: Theme) = theme.getCellHeader(dayOfWeek)
    }
}
