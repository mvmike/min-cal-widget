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

private const val dayHeaderSaturdayDarkThemeBackground = 2131034148
private const val dayHeaderSundayDarkThemeBackground = 2131034152
private const val dayHeaderSaturdayLightThemeBackground = 2131034149
private const val dayHeaderSundayLightThemeBackground = 2131034153

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
            every { context.getString(it.dayOfWeek.getExpectedResourceId()) } returns it.expectedHeaderString
        }

        justRun { SystemResolver.addToDaysHeaderRow(context, daysHeaderRowRv, any(), any(), any(), any()) }
        justRun { SystemResolver.addToWidget(widgetRv, daysHeaderRowRv) }

        DrawDaysHeaderUseCase.execute(context, widgetRv, format)

        verifyWidgetTransparency()
        verifyFirstDayOfWeek()
        verifyWidgetTheme()
        verify { SystemResolver.createDaysHeaderRow(context) }
        expectedDayHeaders.forEach {
            val cellHeader = theme.getCellHeader(it.dayOfWeek)
            verify { context.getString(it.dayOfWeek.getExpectedResourceId()) }
            verifyGetDayHeaderCellBackground(it.cellBackground)
            verify {
                SystemResolver.addToDaysHeaderRow(
                    context = context,
                    daysHeaderRow = daysHeaderRowRv,
                    text = it.expectedHeaderString,
                    layoutId = cellHeader.layout,
                    viewId = 16908308,
                    dayHeaderBackgroundColour = it.cellBackground
                )
            }
        }
        verify { SystemResolver.addToWidget(widgetRv, daysHeaderRowRv) }
        confirmVerified(widgetRv, daysHeaderRowRv)
    }

    @Suppress("unused", "LongMethod")
    private fun startWeekDayAndThemeAndFormatWithExpectedOutput() = Stream.of(
        Arguments.of(
            MONDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "TUE"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRY"),
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayDarkThemeBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayDarkThemeBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(TUESDAY, "TUE"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRY"),
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayDarkThemeBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayDarkThemeBackground),
                DayHeaderTestProperties(MONDAY, "MON")
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRY"),
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayDarkThemeBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayDarkThemeBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "TUE")
            )
        ),
        Arguments.of(
            THURSDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRY"),
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayDarkThemeBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayDarkThemeBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "TUE"),
                DayHeaderTestProperties(WEDNESDAY, "WED")
            )
        ),
        Arguments.of(
            FRIDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(FRIDAY, "FRY"),
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayDarkThemeBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayDarkThemeBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "TUE"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU")
            )
        ),
        Arguments.of(
            SATURDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayDarkThemeBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayDarkThemeBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "TUE"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRY")
            )
        ),
        Arguments.of(
            SUNDAY, Theme.DARK, Format.STANDARD, listOf(
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayDarkThemeBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "TUE"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRY"),
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayDarkThemeBackground)
            )
        ),
        Arguments.of(
            MONDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "T"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", dayHeaderSaturdayDarkThemeBackground),
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayDarkThemeBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(TUESDAY, "T"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", dayHeaderSaturdayDarkThemeBackground),
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayDarkThemeBackground),
                DayHeaderTestProperties(MONDAY, "M")
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "T", dayHeaderSaturdayDarkThemeBackground),
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayDarkThemeBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "T")
            )
        ),
        Arguments.of(
            THURSDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", dayHeaderSaturdayDarkThemeBackground),
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayDarkThemeBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "T"),
                DayHeaderTestProperties(WEDNESDAY, "W")
            )
        ),
        Arguments.of(
            FRIDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", dayHeaderSaturdayDarkThemeBackground),
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayDarkThemeBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "T"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T")
            )
        ),
        Arguments.of(
            SATURDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(SATURDAY, "S", dayHeaderSaturdayDarkThemeBackground),
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayDarkThemeBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "T"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F")
            )
        ),
        Arguments.of(
            SUNDAY, Theme.DARK, Format.REDUCED, listOf(
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayDarkThemeBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "T"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", dayHeaderSaturdayDarkThemeBackground)
            )
        ),
        Arguments.of(
            MONDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "TUE"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRY"),
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayLightThemeBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayLightThemeBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(TUESDAY, "TUE"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRY"),
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayLightThemeBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayLightThemeBackground),
                DayHeaderTestProperties(MONDAY, "MON")
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRY"),
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayLightThemeBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayLightThemeBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "TUE")
            )
        ),
        Arguments.of(
            THURSDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRY"),
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayLightThemeBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayLightThemeBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "TUE"),
                DayHeaderTestProperties(WEDNESDAY, "WED")
            )
        ),
        Arguments.of(
            FRIDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(FRIDAY, "FRY"),
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayLightThemeBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayLightThemeBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "TUE"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU")
            )
        ),
        Arguments.of(
            SATURDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayLightThemeBackground),
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayLightThemeBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "TUE"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRY")
            )
        ),
        Arguments.of(
            SUNDAY, Theme.LIGHT, Format.STANDARD, listOf(
                DayHeaderTestProperties(SUNDAY, "SUN", dayHeaderSundayLightThemeBackground),
                DayHeaderTestProperties(MONDAY, "MON"),
                DayHeaderTestProperties(TUESDAY, "TUE"),
                DayHeaderTestProperties(WEDNESDAY, "WED"),
                DayHeaderTestProperties(THURSDAY, "THU"),
                DayHeaderTestProperties(FRIDAY, "FRY"),
                DayHeaderTestProperties(SATURDAY, "SAT", dayHeaderSaturdayLightThemeBackground)
            )
        ),
        Arguments.of(
            MONDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "T"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", dayHeaderSaturdayLightThemeBackground),
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayLightThemeBackground)
            )
        ),
        Arguments.of(
            TUESDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(TUESDAY, "T"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", dayHeaderSaturdayLightThemeBackground),
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayLightThemeBackground),
                DayHeaderTestProperties(MONDAY, "M")
            )
        ),
        Arguments.of(
            WEDNESDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "T", dayHeaderSaturdayLightThemeBackground),
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayLightThemeBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "T")
            )
        ),
        Arguments.of(
            THURSDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", dayHeaderSaturdayLightThemeBackground),
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayLightThemeBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "T"),
                DayHeaderTestProperties(WEDNESDAY, "W")
            )
        ),
        Arguments.of(
            FRIDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", dayHeaderSaturdayLightThemeBackground),
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayLightThemeBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "T"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T")
            )
        ),
        Arguments.of(
            SATURDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(SATURDAY, "S", dayHeaderSaturdayLightThemeBackground),
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayLightThemeBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "T"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F")
            )
        ),
        Arguments.of(
            SUNDAY, Theme.LIGHT, Format.REDUCED, listOf(
                DayHeaderTestProperties(SUNDAY, "S", dayHeaderSundayLightThemeBackground),
                DayHeaderTestProperties(MONDAY, "M"),
                DayHeaderTestProperties(TUESDAY, "T"),
                DayHeaderTestProperties(WEDNESDAY, "W"),
                DayHeaderTestProperties(THURSDAY, "T"),
                DayHeaderTestProperties(FRIDAY, "F"),
                DayHeaderTestProperties(SATURDAY, "S", dayHeaderSaturdayLightThemeBackground)
            )
        )
    )!!

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
