// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.CellContent
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
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

internal class DaysHeaderServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    private val daysHeaderRowRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getStartWeekDayAndThemeAndTextSizeWithExpectedOutput")
    fun draw_shouldAddViewBasedOnCurrentConfigAndTextSize(
        firstDayOfWeek: DayOfWeek,
        widgetTheme: Theme,
        transparency: Transparency,
        textSize: TextSize,
        expectedDayHeaders: List<DayHeaderTestProperties>
    ) {
        mockkObject(ActionableView.RowHeader)

        every { GraphicResolver.createDaysHeaderRow(context) } returns daysHeaderRowRv

        expectedDayHeaders.forEach {
            mockTransparency(it.getCellHeader(widgetTheme).background, transparency, TransparencyRange.MODERATE)
            val resourceAndTranslation = it.dayOfWeek.getExpectedResourceIdAndTranslation()
            every { context.getString(resourceAndTranslation.first) } returns resourceAndTranslation.second
        }

        justRun {
            GraphicResolver.addToDaysHeaderRow(any(), context, daysHeaderRowRv, any(), any(), any())
        }
        justRun { GraphicResolver.addToWidget(widgetRv, daysHeaderRowRv) }
        justRun { ActionableView.RowHeader.addListener(context, widgetRv) }

        DaysHeaderService.draw(context, widgetRv, firstDayOfWeek, widgetTheme, transparency, textSize)

        verify(exactly = 1) { GraphicResolver.createDaysHeaderRow(context) }
        expectedDayHeaders.forEach {
            verify { context.getString(it.dayOfWeek.getExpectedResourceIdAndTranslation().first) }
            verifyTransparency(it.getCellHeader(widgetTheme).background, transparency, TransparencyRange.MODERATE)
        }
        verifyOrder {
            expectedDayHeaders.forEach {
                GraphicResolver.addToDaysHeaderRow(
                    layoutId = it.getCellHeader(widgetTheme).layout,
                    context = context,
                    daysHeaderRowRemoteView = daysHeaderRowRv,
                    viewId = it.getCellHeader(widgetTheme).id,
                    dayHeaderBackgroundColour = it.getCellHeader(widgetTheme).background,
                    cellContent = CellContent(
                        text = it.expectedHeaderText,
                        colour = it.getCellHeader(widgetTheme).textColour,
                        relativeSize = textSize.relativeValue
                    )
                )
            }
        }
        verify(exactly = 1) { GraphicResolver.addToWidget(widgetRv, daysHeaderRowRv) }
        verify(exactly = 1) { ActionableView.RowHeader.addListener(context, widgetRv) }
        confirmVerified(widgetRv, daysHeaderRowRv)
    }

    private fun getStartWeekDayAndThemeAndTextSizeWithExpectedOutput() = listOf(
        Arguments.of(
            MONDAY,
            Theme.DARK,
            Transparency(10),
            TextSize(40),
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
            Transparency(20),
            TextSize(40),
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
            Transparency(20),
            TextSize(40),
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
            Transparency(30),
            TextSize(40),
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
            Transparency(90),
            TextSize(40),
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
            Transparency(15),
            TextSize(40),
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
            Transparency(20),
            TextSize(40),
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
            Transparency(0),
            TextSize(15),
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
            Transparency(55),
            TextSize(15),
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
            Transparency(15),
            TextSize(15),
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
            Transparency(20),
            TextSize(15),
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
            Transparency(20),
            TextSize(15),
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
            Transparency(20),
            TextSize(15),
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
            Transparency(20),
            TextSize(15),
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
            Transparency(80),
            TextSize(40),
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
            Transparency(5),
            TextSize(40),
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
            Transparency(20),
            TextSize(40),
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
            Transparency(20),
            TextSize(40),
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
            Transparency(20),
            TextSize(40),
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
            Transparency(20),
            TextSize(40),
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
            Transparency(20),
            TextSize(40),
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
            Transparency(20),
            TextSize(15),
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
            Transparency(20),
            TextSize(15),
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
            Transparency(15),
            TextSize(15),
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
            Transparency(100),
            TextSize(15),
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
            Transparency(0),
            TextSize(15),
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
            Transparency(20),
            TextSize(15),
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
            Transparency(20),
            TextSize(15),
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
    )

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

    internal data class DayHeaderTestProperties(
        val dayOfWeek: DayOfWeek,
        val expectedHeaderText: String
    ) {
        fun getCellHeader(theme: Theme) = theme.getCellHeader(dayOfWeek)
    }
}