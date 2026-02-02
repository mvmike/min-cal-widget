// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.Cell
import cat.mvmike.minimalcalendarwidget.domain.component.DaysHeaderService.getRotatedDaysOfWeek
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme.DARK
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme.LIGHT
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
import org.assertj.core.api.Assertions.assertThat
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
    @MethodSource("getFirstDayOfWeekAndThemeAndThemeAndTextSizeWithExpectedOutput")
    fun draw_shouldAddViewBasedOnCurrentConfigAndTextSize(
        firstDayOfWeek: DayOfWeek,
        widgetTheme: Theme,
        transparency: Transparency,
        textSize: TextSize,
        showWeekNumber: Boolean,
        expectedDayHeaders: List<String>
    ) {
        mockkObject(ActionableView.RowHeader)
        mockShowWeekNumber(showWeekNumber)
        every { GraphicResolver.createDaysHeaderRow(context) } returns daysHeaderRowRv
        val rotatedWeekDays = getRotatedDaysOfWeek(firstDayOfWeek)
        rotatedWeekDays.forEach {
            mockTransparency(widgetTheme.getCellHeader(it).background, transparency, TransparencyRange.MODERATE)
            val resourceAndTranslation = it.getExpectedResourceIdAndTranslation()
            every { context.getString(resourceAndTranslation.first) } returns resourceAndTranslation.second
        }
        justRun { GraphicResolver.addToDaysHeaderRow(context, daysHeaderRowRv, any(), any()) }
        justRun { GraphicResolver.addToWidget(widgetRv, daysHeaderRowRv) }
        justRun { ActionableView.RowHeader.addListener(context, widgetRv) }

        DaysHeaderService.draw(context, widgetRv, firstDayOfWeek, widgetTheme, transparency, textSize)

        verifyShowWeekNumber()
        verify(exactly = 1) { GraphicResolver.createDaysHeaderRow(context) }
        rotatedWeekDays.forEach {
            verify { context.getString(it.getExpectedResourceIdAndTranslation().first) }
            verifyTransparency(widgetTheme.getCellHeader(it).background, transparency, TransparencyRange.MODERATE)
        }
        verifyOrder {
            if (showWeekNumber) {
                GraphicResolver.addToDaysHeaderRow(
                    context = context,
                    daysHeaderRowRemoteView = daysHeaderRowRv,
                    dayHeaderBackgroundColour = widgetTheme.getCellHeader(MONDAY).background,
                    cell = Cell(
                        text = " ",
                        colour = widgetTheme.getCellHeader(MONDAY).textColour,
                        relativeSize = textSize.relativeValue
                    )
                )
            }
            rotatedWeekDays.zip(expectedDayHeaders).forEach {
                GraphicResolver.addToDaysHeaderRow(
                    context = context,
                    daysHeaderRowRemoteView = daysHeaderRowRv,
                    dayHeaderBackgroundColour = widgetTheme.getCellHeader(it.first).background,
                    cell = Cell(
                        text = it.second,
                        colour = widgetTheme.getCellHeader(it.first).textColour,
                        relativeSize = textSize.relativeValue
                    )
                )
            }
        }
        verify(exactly = 1) { GraphicResolver.addToWidget(widgetRv, daysHeaderRowRv) }
        verify(exactly = 1) { ActionableView.RowHeader.addListener(context, widgetRv) }
        confirmVerified(widgetRv, daysHeaderRowRv)
    }

    @ParameterizedTest
    @MethodSource("getFirstDayOfWeekAndTExpectedRotatedDaysOfWeek")
    fun getRotatedDaysOfWeek_shouldReturnDaysOfWeekBasedOnFirstDayOfWeek(
        firstDayOfWeek: DayOfWeek,
        expectedRotatedDayOfWeek: List<DayOfWeek>
    ) {
        assertThat(getRotatedDaysOfWeek(firstDayOfWeek)).isEqualTo(expectedRotatedDayOfWeek)
    }

    private fun getFirstDayOfWeekAndThemeAndThemeAndTextSizeWithExpectedOutput() = listOf(
        of(MONDAY, DARK, Transparency(10), TextSize(40), false, listOf("MON", "DOO", "WED", "THU", "FRI", "SAT", "SUN")),
        of(TUESDAY, DARK, Transparency(20), TextSize(40), true, listOf("DOO", "WED", "THU", "FRI", "SAT", "SUN", "MON")),
        of(WEDNESDAY, DARK, Transparency(20), TextSize(40), false, listOf("WED", "THU", "FRI", "SAT", "SUN", "MON", "DOO")),
        of(THURSDAY, DARK, Transparency(30), TextSize(40), true, listOf("THU", "FRI", "SAT", "SUN", "MON", "DOO", "WED")),
        of(FRIDAY, DARK, Transparency(90), TextSize(40), false, listOf("FRI", "SAT", "SUN", "MON", "DOO", "WED", "THU")),
        of(SATURDAY, DARK, Transparency(15), TextSize(40), true, listOf("SAT", "SUN", "MON", "DOO", "WED", "THU", "FRI")),
        of(SUNDAY, DARK, Transparency(20), TextSize(40), false, listOf("SUN", "MON", "DOO", "WED", "THU", "FRI", "SAT")),
        of(MONDAY, DARK, Transparency(0), TextSize(15), true, listOf("M", "D", "W", "T", "F", "S", "S")),
        of(TUESDAY, DARK, Transparency(55), TextSize(15), false, listOf("D", "W", "T", "F", "S", "S", "M")),
        of(WEDNESDAY, DARK, Transparency(15), TextSize(15), true, listOf("W", "T", "F", "S", "S", "M", "D")),
        of(THURSDAY, DARK, Transparency(20), TextSize(15), false, listOf("T", "F", "S", "S", "M", "D", "W")),
        of(FRIDAY, DARK, Transparency(20), TextSize(15), true, listOf("F", "S", "S", "M", "D", "W", "T")),
        of(SATURDAY, DARK, Transparency(20), TextSize(15), false, listOf("S", "S", "M", "D", "W", "T", "F")),
        of(SUNDAY, DARK, Transparency(20), TextSize(15), true, listOf("S", "M", "D", "W", "T", "F", "S")),
        of(MONDAY, LIGHT, Transparency(80), TextSize(40), true, listOf("MON", "DOO", "WED", "THU", "FRI", "SAT", "SUN")),
        of(TUESDAY, LIGHT, Transparency(5), TextSize(40), false, listOf("DOO", "WED", "THU", "FRI", "SAT", "SUN", "MON")),
        of(WEDNESDAY, LIGHT, Transparency(20), TextSize(40), true, listOf("WED", "THU", "FRI", "SAT", "SUN", "MON", "DOO")),
        of(THURSDAY, LIGHT, Transparency(20), TextSize(40), false, listOf("THU", "FRI", "SAT", "SUN", "MON", "DOO", "WED")),
        of(FRIDAY, LIGHT, Transparency(20), TextSize(40), true, listOf("FRI", "SAT", "SUN", "MON", "DOO", "WED", "THU")),
        of(SATURDAY, LIGHT, Transparency(20), TextSize(40), false, listOf("SAT", "SUN", "MON", "DOO", "WED", "THU", "FRI")),
        of(SUNDAY, LIGHT, Transparency(20), TextSize(40), true, listOf("SUN", "MON", "DOO", "WED", "THU", "FRI", "SAT")),
        of(MONDAY, LIGHT, Transparency(20), TextSize(15), false, listOf("M", "D", "W", "T", "F", "S", "S")),
        of(TUESDAY, LIGHT, Transparency(20), TextSize(15), true, listOf("D", "W", "T", "F", "S", "S", "M")),
        of(WEDNESDAY, LIGHT, Transparency(15), TextSize(15), false, listOf("W", "T", "F", "S", "S", "M", "D")),
        of(THURSDAY, LIGHT, Transparency(100), TextSize(15), true, listOf("T", "F", "S", "S", "M", "D", "W")),
        of(FRIDAY, LIGHT, Transparency(0), TextSize(15), false, listOf("F", "S", "S", "M", "D", "W", "T")),
        of(SATURDAY, LIGHT, Transparency(20), TextSize(15), true, listOf("S", "S", "M", "D", "W", "T", "F")),
        of(SUNDAY, LIGHT, Transparency(20), TextSize(15), false, listOf("S", "M", "D", "W", "T", "F", "S"))
    )

    private fun getFirstDayOfWeekAndTExpectedRotatedDaysOfWeek() = listOf(
        of(MONDAY, listOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)),
        of(TUESDAY, listOf(TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, MONDAY)),
        of(WEDNESDAY, listOf(WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, MONDAY, TUESDAY)),
        of(THURSDAY, listOf(THURSDAY, FRIDAY, SATURDAY, SUNDAY, MONDAY, TUESDAY, WEDNESDAY)),
        of(FRIDAY, listOf(FRIDAY, SATURDAY, SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY)),
        of(SATURDAY, listOf(SATURDAY, SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)),
        of(SUNDAY, listOf(SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY))
    )

    private fun DayOfWeek.getExpectedResourceIdAndTranslation() = when (this) {
        MONDAY -> Pair(R.string.monday_abb, "MONDAY")
        TUESDAY -> Pair(R.string.tuesday_abb, "DOOMSDAY")
        WEDNESDAY -> Pair(R.string.wednesday_abb, "WEDNESDAY")
        THURSDAY -> Pair(R.string.thursday_abb, "THURSDAY")
        FRIDAY -> Pair(R.string.friday_abb, "FRIDAY")
        SATURDAY -> Pair(R.string.saturday_abb, "SATURDAY")
        SUNDAY -> Pair(R.string.sunday_abb, "SUNDAY")
    }
}