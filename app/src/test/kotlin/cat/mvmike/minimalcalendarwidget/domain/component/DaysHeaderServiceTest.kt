// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Cell
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
import org.junit.jupiter.params.provider.Arguments.of
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
            GraphicResolver.addToDaysHeaderRow(context, daysHeaderRowRv, any(), any())
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
                    context = context,
                    daysHeaderRowRemoteView = daysHeaderRowRv,
                    dayHeaderBackgroundColour = it.getCellHeader(widgetTheme).background,
                    cell = Cell(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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
        of(
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