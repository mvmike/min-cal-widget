// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.entry.Instance
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Random
import java.util.stream.Stream
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class DrawDaysUseCaseTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    private val rowRv = mockk<RemoteViews>()

    @Test
    @SuppressWarnings("LongMethod")
    fun setDays_shouldReturnSafeDateSpanOfSystemTimeZoneInstances() {
        mockGetSystemLocalDate()
        mockIsReadCalendarPermitted(true)

        val initLocalDate = systemLocalDate.minusDays(45)
        val endLocalDate = systemLocalDate.plusDays(45)
        val initEpochMillis = initLocalDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endEpochMillis = endLocalDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        mockGetSystemZoneId()
        every { SystemResolver.getInstances(context, initEpochMillis, endEpochMillis) } returns getSystemInstances()

        mockSharedPreferences()
        mockWidgetTransparency(Transparency(20))
        mockFirstDayOfWeek(DayOfWeek.MONDAY)
        mockWidgetTheme(Theme.DARK)
        mockInstancesSymbolSet(SymbolSet.MINIMAL)
        mockInstancesColour(Colour.CYAN)

        every { SystemResolver.createDaysRow(context) } returns rowRv

        every { SystemResolver.getColour(context, instancesColourTodayId) } returns instancesColourTodayId
        every { SystemResolver.getColour(context, instancesColourId) } returns instancesColourId

        val expectedBackground = Random().nextInt()
        listOf(
            dayCellTodayBackground,
            dayCellWeekdayInMonthBackground,
            dayCellSaturdayInMonthBackground,
            dayCellSundayInMonthBackground,
        ).forEach {
            every { SystemResolver.getColourAsString(context, it) } returns dayCellTransparentBackground
        }
        every { SystemResolver.parseColour(dayCellModerateTransparentBackgroundInHex) } returns expectedBackground
        every { SystemResolver.parseColour(dayCellLowTransparentBackgroundInHex) } returns expectedBackground

        justRun { SystemResolver.addToDaysRow(context, rowRv, any(), any(), any(), any(), any(), any(), any(), any()) }
        justRun { SystemResolver.addToWidget(widgetRv, rowRv) }

        DrawDaysUseCase.execute(context, widgetRv)

        verify { SystemResolver.getSystemLocalDate() }
        verify { SystemResolver.isReadCalendarPermitted(context) }
        verify { SystemResolver.getSystemZoneId() }
        verify { SystemResolver.getInstances(context, initEpochMillis, endEpochMillis) }

        verifyWidgetTransparency()
        verifyFirstDayOfWeek()
        verifyWidgetTheme()
        verifyInstancesSymbolSet()
        verifyInstancesColour()

        verify(exactly = 6) { SystemResolver.createDaysRow(context) }

        getDrawDaysUseCaseTestProperties().forEach { dayUseCaseTest ->

            verify {
                SystemResolver.getColour(
                    context, when {
                        dayUseCaseTest.isToday -> instancesColourTodayId
                        else -> instancesColourId
                    }
                )
            }
            dayUseCaseTest.dayBackgroundColour?.let {
                verify {
                    SystemResolver.getColourAsString(context, dayUseCaseTest.dayBackgroundColour)
                    when (dayUseCaseTest.dayBackgroundColour) {
                        dayCellSaturdayInMonthBackground,
                        dayCellSundayInMonthBackground -> SystemResolver.parseColour(dayCellModerateTransparentBackgroundInHex)
                        dayCellWeekdayInMonthBackground -> SystemResolver.parseColour(dayCellLowTransparentBackgroundInHex)
                        else -> { }
                    }

                }
            }

            verifyOrder {
                SystemResolver.addToDaysRow(
                    context = context,
                    weekRow = rowRv,
                    dayLayout = dayUseCaseTest.dayLayout,
                    viewId = 16908308,
                    dayBackgroundColour = dayUseCaseTest.dayBackgroundColour?.let { expectedBackground },
                    spanText = dayUseCaseTest.spanText,
                    isToday = dayUseCaseTest.isToday,
                    isSingleDigitDay = dayUseCaseTest.isSingleDigitDay(),
                    symbolRelativeSize = dayUseCaseTest.symbolRelativeSize,
                    instancesColour = dayUseCaseTest.instancesColour
                )
            }
        }
        verify(exactly = 6) { SystemResolver.addToWidget(widgetRv, rowRv) }
        confirmVerified(widgetRv, rowRv)
    }

    @ParameterizedTest
    @MethodSource("getSystemLocalDateAndFirstDayOfWeekWithExpectedInitialLocalDate")
    fun getInitialLocalDate_shouldReturnWidgetInitialDate(
        systemLocalDate: LocalDate,
        firstDayOfWeek: DayOfWeek,
        expectedInitialLocalDate: LocalDate
    ) {
        val result = DrawDaysUseCase.getInitialLocalDate(systemLocalDate, firstDayOfWeek)

        Assertions.assertThat(result).isEqualTo(expectedInitialLocalDate)
    }

    companion object {

        private const val dayLayoutNotInMonth = 2131427358
        private const val dayLayoutToday = 2131427364
        private const val dayLayoutWeekdayInMonth = 2131427363
        private const val dayLayoutSaturdayInMonth = 2131427359
        private const val dayLayoutSundayInMonth = 2131427361

        private const val instancesColourTodayId = 2131034185
        private const val instancesColourId = 2131034182

        private const val dayCellTodayBackground = 2131034158
        private const val dayCellWeekdayInMonthBackground = 2131034156
        private const val dayCellSaturdayInMonthBackground = 2131034148
        private const val dayCellSundayInMonthBackground = 2131034152

        private const val dayCellTransparentBackground = "transparentBackground"
        private const val dayCellModerateTransparentBackgroundInHex = "#40ground"
        private const val dayCellLowTransparentBackgroundInHex = "#18ground"

        @Suppress("LongMethod")
        private fun getSystemInstances(): Set<Instance> {
            val random = Random()
            return setOf(
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-11-26T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-11-27T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-11-28T00:00:00Z".toInstant(systemZoneOffset),
                    end = "2018-11-29T09:00:00Z".toInstant(systemZoneOffset),
                    zoneId = systemZoneOffset
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-03T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-04T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-04T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-07T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-06T02:00:00Z".toInstant(systemZoneOffset),
                    end = "2018-12-07T04:00:00Z".toInstant(systemZoneOffset),
                    zoneId = systemZoneOffset
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-07T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-10T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-11T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-10T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-11T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-10T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-11T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-10T12:00:00Z".toInstant(systemZoneOffset),
                    end = "2018-12-11T13:00:00Z".toInstant(systemZoneOffset),
                    zoneId = systemZoneOffset
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-27T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-28T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-30T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-31T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-30T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-31T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-30T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-31T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-30T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-31T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2018-12-30T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2018-12-31T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2019-01-01T05:00:00Z".toInstant(systemZoneOffset),
                    end = "2019-12-02T11:20:00Z".toInstant(systemZoneOffset),
                    zoneId = systemZoneOffset
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2019-01-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2019-01-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2019-01-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2019-01-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2019-01-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2019-01-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2019-01-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2019-01-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2019-01-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2019-01-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                ),
                Instance(
                    eventId = random.nextInt(),
                    start = "2019-01-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                    end = "2019-01-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                    zoneId = ZoneOffset.UTC
                )
            )
        }

        private fun getDrawDaysUseCaseTestProperties() = Stream.of(
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutNotInMonth, spanText = " 26 ·"),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutNotInMonth, spanText = " 27  "),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutNotInMonth, spanText = " 28 ·"),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutNotInMonth, spanText = " 29 ·"),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutNotInMonth, spanText = " 30  "),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutSaturdayInMonth, spanText = " 01  ", dayBackgroundColour = dayCellSaturdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutSundayInMonth, spanText = " 02  ", dayBackgroundColour = dayCellSundayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 03 ·", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(
                dayLayout = dayLayoutToday, spanText = " 04 ·", dayBackgroundColour = dayCellTodayBackground,
                isToday = true, instancesColour = instancesColourTodayId
            ),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 05  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 06 ∴", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 07 ·", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutSaturdayInMonth, spanText = " 08  ", dayBackgroundColour = dayCellSaturdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutSundayInMonth, spanText = " 09  ", dayBackgroundColour = dayCellSundayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 10 ∷", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 11 ·", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 12  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 13  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 14  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutSaturdayInMonth, spanText = " 15  ", dayBackgroundColour = dayCellSaturdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutSundayInMonth, spanText = " 16  ", dayBackgroundColour = dayCellSundayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 17  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 18  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 19  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 20  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 21  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutSaturdayInMonth, spanText = " 22  ", dayBackgroundColour = dayCellSaturdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutSundayInMonth, spanText = " 23  ", dayBackgroundColour = dayCellSundayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 24  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 25  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 26  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 27 ·", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 28  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutSaturdayInMonth, spanText = " 29  ", dayBackgroundColour = dayCellSaturdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutSundayInMonth, spanText = " 30 ◇", dayBackgroundColour = dayCellSundayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutWeekdayInMonth, spanText = " 31  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutNotInMonth, spanText = " 01 ·"),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutNotInMonth, spanText = " 02 ·"),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutNotInMonth, spanText = " 03 ·"),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutNotInMonth, spanText = " 04 ·"),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutNotInMonth, spanText = " 05 ◈"),
            DrawDaysUseCaseTestProperties(dayLayout = dayLayoutNotInMonth, spanText = " 06 ·")
        )

        internal data class DrawDaysUseCaseTestProperties(
            val dayLayout: Int,
            val spanText: String,
            val dayBackgroundColour: Int? = null,
            val isToday: Boolean = false,
            val symbolRelativeSize: Float = 1.1f,
            val instancesColour: Int = instancesColourId
        ) {
            fun isSingleDigitDay() = spanText.startsWith(" 0")
        }

        private fun String.toInstant(zoneOffset: ZoneOffset) = LocalDateTime
            .parse(this, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            .toInstant(zoneOffset)

        @JvmStatic
        @Suppress("unused", "LongMethod")
        fun getSystemLocalDateAndFirstDayOfWeekWithExpectedInitialLocalDate() = Stream.of(
            Arguments.of(
                LocalDate.of(2018, 1, 26),
                DayOfWeek.MONDAY,
                LocalDate.of(2018, 1, 1)
            ),
            Arguments.of(
                LocalDate.of(2018, 1, 26),
                DayOfWeek.TUESDAY,
                LocalDate.of(2017, 12, 26)
            ),
            Arguments.of(
                LocalDate.of(2018, 1, 26),
                DayOfWeek.WEDNESDAY,
                LocalDate.of(2017, 12, 27)
            ),
            Arguments.of(
                LocalDate.of(2018, 1, 26),
                DayOfWeek.THURSDAY,
                LocalDate.of(2017, 12, 28)
            ),
            Arguments.of(
                LocalDate.of(2018, 1, 26),
                DayOfWeek.FRIDAY,
                LocalDate.of(2017, 12, 29)
            ),
            Arguments.of(
                LocalDate.of(2018, 1, 26),
                DayOfWeek.SATURDAY,
                LocalDate.of(2017, 12, 30)
            ),
            Arguments.of(
                LocalDate.of(2018, 1, 26),
                DayOfWeek.SUNDAY,
                LocalDate.of(2017, 12, 31)
            ),
            Arguments.of(
                LocalDate.of(2005, 2, 19),
                DayOfWeek.WEDNESDAY,
                LocalDate.of(2005, 1, 26)
            ),
            Arguments.of(
                LocalDate.of(2027, 3, 5),
                DayOfWeek.SUNDAY,
                LocalDate.of(2027, 2, 28)
            ),
            Arguments.of(
                LocalDate.of(2099, 4, 30),
                DayOfWeek.MONDAY,
                LocalDate.of(2099, 3, 30)
            ),
            Arguments.of(
                LocalDate.of(2000, 5, 1),
                DayOfWeek.SATURDAY,
                LocalDate.of(2000, 4, 29)
            ),
            Arguments.of(
                LocalDate.of(1998, 6, 2),
                DayOfWeek.WEDNESDAY,
                LocalDate.of(1998, 5, 27)
            ),
            Arguments.of(
                LocalDate.of(1992, 7, 7),
                DayOfWeek.TUESDAY,
                LocalDate.of(1992, 6, 30)
            ),
            Arguments.of(
                LocalDate.of(2018, 8, 1),
                DayOfWeek.FRIDAY,
                LocalDate.of(2018, 7, 27)
            ),
            Arguments.of(
                LocalDate.of(1987, 9, 12),
                DayOfWeek.FRIDAY,
                LocalDate.of(1987, 8, 28)
            ),
            Arguments.of(
                LocalDate.of(2017, 10, 1),
                DayOfWeek.THURSDAY,
                LocalDate.of(2017, 9, 28)
            ),
            Arguments.of(
                LocalDate.of(1000, 11, 12),
                DayOfWeek.SATURDAY,
                LocalDate.of(1000, 11, 1)
            ),
            Arguments.of(
                LocalDate.of(1994, 12, 13),
                DayOfWeek.THURSDAY,
                LocalDate.of(1994, 12, 1)
            ),
            Arguments.of(
                LocalDate.of(2021, 2, 13),
                DayOfWeek.MONDAY,
                LocalDate.of(2021, 2, 1)
            ),
            Arguments.of(
                LocalDate.of(2021, 3, 13),
                DayOfWeek.MONDAY,
                LocalDate.of(2021, 3, 1)
            )
        )!!
    }
}
