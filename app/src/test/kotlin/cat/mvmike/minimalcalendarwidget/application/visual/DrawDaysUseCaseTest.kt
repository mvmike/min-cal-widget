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
        every { systemResolver.getInstances(context, initEpochMillis, endEpochMillis) } returns getSystemInstances()

        mockSharedPreferences()
        mockWidgetTransparency(Transparency(20))
        mockFirstDayOfWeek(DayOfWeek.MONDAY)
        mockCalendarTheme(Theme.BLACK)
        mockInstancesSymbolSet(SymbolSet.MINIMAL)
        mockInstancesColour(Colour.CYAN)

        every { systemResolver.createDaysRow(context) } returns rowRv

        every { systemResolver.getColour(context, instancesColourTodayId) } returns instancesColourTodayId
        every { systemResolver.getColour(context, instancesColourId) } returns instancesColourId

        val expectedBackground = 55
        getDrawDaysUseCaseTestProperties()
            .map { it.dayBackgroundColour }
            .filter { it != null }
            .distinct()
            .forEach {
                every { systemResolver.getColourAsString(context, it!!) } returns dayCellTransparentBackground
            }
        every { systemResolver.parseColour(dayCellModerateTransparentBackgroundInHex) } returns expectedBackground
        every { systemResolver.parseColour(dayCellLowTransparentBackgroundInHex) } returns expectedBackground

        justRun { systemResolver.addToDaysRow(context, rowRv, any(), any(), any(), any(), any(), any(), any(), any()) }
        justRun { systemResolver.addToWidget(widgetRv, rowRv) }

        DrawDaysUseCase.execute(context, widgetRv)

        verify { systemResolver.getSystemLocalDate() }
        verify { systemResolver.isReadCalendarPermitted(context) }
        verify { systemResolver.getSystemZoneId() }
        verify { systemResolver.getInstances(context, initEpochMillis, endEpochMillis) }

        verifyWidgetTransparency()
        verifyFirstDayOfWeek()
        verifyCalendarTheme()
        verifyInstancesSymbolSet()
        verifyInstancesColour()

        verify(exactly = 6) { systemResolver.createDaysRow(context) }

        getDrawDaysUseCaseTestProperties().forEach { dayUseCaseTest ->

            verify {
                systemResolver.getColour(
                    context, when {
                        dayUseCaseTest.isToday -> instancesColourTodayId
                        else -> instancesColourId
                    }
                )
            }
            dayUseCaseTest.dayBackgroundColour?.let {
                verify {
                    systemResolver.getColourAsString(context, dayUseCaseTest.dayBackgroundColour)
                    when (dayUseCaseTest.dayBackgroundColour){
                        dayCellSaturdayInMonthBackground,
                        dayCellSundayInMonthBackground -> systemResolver.parseColour(dayCellModerateTransparentBackgroundInHex)
                        dayCellWeekdayInMonthBackground -> systemResolver.parseColour(dayCellLowTransparentBackgroundInHex)
                        else -> { }
                    }

                }
            }

            verifyOrder {
                systemResolver.addToDaysRow(
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
        verify(exactly = 6) { systemResolver.addToWidget(widgetRv, rowRv) }
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

        private const val instancesColourTodayId = 2131034184
        private const val instancesColourId = 2131034181

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
                    end = "2018-12-02T11:20:00Z".toInstant(systemZoneOffset),
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
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 26 ·"),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 27  "),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 28 ·"),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 29 ·"),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 30  "),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427357, spanText = " 01 ·", dayBackgroundColour = dayCellSaturdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427359, spanText = " 02 ·", dayBackgroundColour = dayCellSundayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 03 ·", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427362, spanText = " 04 ·", dayBackgroundColour = dayCellTodayBackground,
                isToday = true, instancesColour = instancesColourTodayId),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 05  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 06 ∴", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 07 ·", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427357, spanText = " 08  ", dayBackgroundColour = dayCellSaturdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427359, spanText = " 09  ", dayBackgroundColour = dayCellSundayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 10 ∷", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 11 ·", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 12  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 13  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 14  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427357, spanText = " 15  ", dayBackgroundColour = dayCellSaturdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427359, spanText = " 16  ", dayBackgroundColour = dayCellSundayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 17  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 18  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 19  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 20  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 21  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427357, spanText = " 22  ", dayBackgroundColour = dayCellSaturdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427359, spanText = " 23  ", dayBackgroundColour = dayCellSundayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 24  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 25  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 26  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 27 ·", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 28  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427357, spanText = " 29  ", dayBackgroundColour = dayCellSaturdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427359, spanText = " 30 ◇", dayBackgroundColour = dayCellSundayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 31  ", dayBackgroundColour = dayCellWeekdayInMonthBackground),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 01 ·"),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 02 ·"),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 03  "),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 04  "),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 05 ◈"),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 06  ")
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
            )
        )!!
    }
}
