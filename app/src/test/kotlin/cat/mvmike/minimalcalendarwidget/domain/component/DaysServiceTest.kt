// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.Day
import cat.mvmike.minimalcalendarwidget.domain.Format
import cat.mvmike.minimalcalendarwidget.domain.Instance
import cat.mvmike.minimalcalendarwidget.domain.component.DaysService.getNumberOfInstances
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.cellViewId
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.darkThemeCellLayout
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.darkThemeCellTextColour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.darkThemeInMonthCellTextColour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.inMonthDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.saturdayDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.saturdayInMonthDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.sundayDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.sundayInMonthDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.todayDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.infrastructure.config.ClockConfig
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDate.of
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Random
import java.util.stream.Stream

private const val instancesColourTodayId = 2131034186
private const val instancesColourId = 2131034183

private const val dayCellTransparentBackground = "transparentBackground"
private const val dayCellModerateTransparentBackgroundInHex = "#40ground"
private const val dayCellLowTransparentBackgroundInHex = "#18ground"

internal class DaysServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    private val rowRv = mockk<RemoteViews>()

    @Test
    @SuppressWarnings("LongMethod")
    fun setDays_shouldReturnSafeDateSpanOfSystemTimeZoneInstances() {
        val format = Format()
        mockGetSystemLocalDate()
        mockIsReadCalendarPermitted(true)

        val initLocalDate = systemLocalDate.minusDays(45)
        val endLocalDate = systemLocalDate.plusDays(45)
        val initEpochMillis = initLocalDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endEpochMillis = endLocalDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        mockGetSystemZoneId()
        every { CalendarResolver.getInstances(context, initEpochMillis, endEpochMillis) } returns getSystemInstances()

        mockSharedPreferences()
        mockWidgetShowDeclinedEvents()
        mockWidgetTransparency(Transparency(20))
        mockFirstDayOfWeek(DayOfWeek.MONDAY)
        mockFocusOnCurrentWeek(false)
        mockWidgetTheme(Theme.DARK)
        mockInstancesSymbolSet(SymbolSet.MINIMAL)
        mockInstancesColour(Colour.CYAN)

        every { GraphicResolver.createDaysRow(context) } returns rowRv

        every { GraphicResolver.getColour(context, instancesColourTodayId) } returns instancesColourTodayId
        every { GraphicResolver.getColour(context, instancesColourId) } returns instancesColourId

        val expectedBackground = Random().nextInt()
        listOf(
            todayDarkThemeCellBackground,
            inMonthDarkThemeCellBackground,
            saturdayInMonthDarkThemeCellBackground,
            sundayInMonthDarkThemeCellBackground,
            saturdayDarkThemeCellBackground,
            sundayDarkThemeCellBackground
        ).forEach {
            every { GraphicResolver.getColourAsString(context, it) } returns dayCellTransparentBackground
        }
        every { GraphicResolver.parseColour(dayCellModerateTransparentBackgroundInHex) } returns expectedBackground
        every { GraphicResolver.parseColour(dayCellLowTransparentBackgroundInHex) } returns expectedBackground

        justRun { GraphicResolver.addToDaysRow(context, rowRv, any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        justRun { GraphicResolver.addToWidget(widgetRv, rowRv) }

        DaysService.draw(context, widgetRv, format)

        verify { ClockConfig.getSystemLocalDate() }
        verify { CalendarResolver.isReadCalendarPermitted(context) }
        verify { ClockConfig.getSystemZoneId() }
        verify { CalendarResolver.getInstances(context, initEpochMillis, endEpochMillis) }

        verifyWidgetShowDeclinedEvents()
        verifyWidgetTransparency()
        verifyFirstDayOfWeek()
        verifyFocusOnCurrentWeek()
        verifyWidgetTheme()
        verifyInstancesSymbolSet()
        verifyInstancesColour()

        verify(exactly = 6) { GraphicResolver.createDaysRow(context) }

        getDrawDaysUseCaseTestProperties().forEach { dayUseCaseTest ->

            verify {
                GraphicResolver.getColour(
                    context, when {
                        dayUseCaseTest.isToday -> instancesColourTodayId
                        else -> instancesColourId
                    }
                )
            }
            dayUseCaseTest.dayBackgroundColour?.let {
                verify {
                    GraphicResolver.getColourAsString(context, dayUseCaseTest.dayBackgroundColour)
                    when (dayUseCaseTest.dayBackgroundColour) {
                        saturdayInMonthDarkThemeCellBackground,
                        sundayInMonthDarkThemeCellBackground -> GraphicResolver.parseColour(dayCellModerateTransparentBackgroundInHex)
                        inMonthDarkThemeCellBackground -> GraphicResolver.parseColour(dayCellLowTransparentBackgroundInHex)
                        else -> {}
                    }

                }
            }

            verifyOrder {
                GraphicResolver.addToDaysRow(
                    context = context,
                    weekRow = rowRv,
                    dayLayout = dayUseCaseTest.dayLayout,
                    viewId = cellViewId,
                    text = dayUseCaseTest.text,
                    textColour = dayUseCaseTest.textColour,
                    dayOfMonthInBold = dayUseCaseTest.isToday,
                    instancesColour = dayUseCaseTest.instancesColour,
                    instancesRelativeSize = dayUseCaseTest.symbolRelativeSize,
                    dayBackgroundColour = dayUseCaseTest.dayBackgroundColour?.let { expectedBackground },
                    textRelativeSize = format.dayCellTextRelativeSize
                )
            }
        }
        verify(exactly = 6) { GraphicResolver.addToWidget(widgetRv, rowRv) }
        confirmVerified(widgetRv, rowRv)
    }

    @ParameterizedTest
    @MethodSource("getSystemLocalDateAndFirstDayOfWeekWithExpectedCurrentWeekFocusedInitialLocalDate")
    fun getCurrentWeekFocusedInitialLocalDate_shouldReturnWidgetInitialDate(
        systemLocalDate: LocalDate,
        firstDayOfWeek: DayOfWeek,
        expectedInitialLocalDate: LocalDate
    ) {
        val result = DaysService.getFocusedOnCurrentWeekInitialLocalDate(systemLocalDate, firstDayOfWeek)

        assertThat(result).isEqualTo(expectedInitialLocalDate)
    }

    @ParameterizedTest
    @MethodSource("getSystemLocalDateAndFirstDayOfWeekWithExpectedNaturalMonthInitialLocalDate")
    fun getNaturalMonthInitialLocalDate_shouldReturnWidgetInitialDate(
        systemLocalDate: LocalDate,
        firstDayOfWeek: DayOfWeek,
        expectedInitialLocalDate: LocalDate
    ) {
        val result = DaysService.getNaturalMonthInitialLocalDate(systemLocalDate, firstDayOfWeek)

        assertThat(result).isEqualTo(expectedInitialLocalDate)
    }

    @ParameterizedTest
    @MethodSource("getLocalDateAndIncludeDeclinedEventsWithExpectedNumberOfInstances")
    fun getNumberOfInstances_shouldReturnEventsInDayAndConsideringDeclinedEvents(
        localDate: LocalDate,
        includeDeclinedEvents: Boolean,
        expectedNumberOfInstances: Int
    ) {
        val result = Day(localDate).getNumberOfInstances(getSystemInstances(), includeDeclinedEvents)

        assertThat(result).isEqualTo(expectedNumberOfInstances)
    }

    @Suppress("LongMethod")
    private fun getSystemInstances(): Set<Instance> {
        val random = Random()
        return setOf(
            Instance(
                eventId = random.nextInt(),
                start = "2018-11-26T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-11-27T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-11-28T00:00:00Z".toInstant(systemZoneOffset),
                end = "2018-11-29T09:00:00Z".toInstant(systemZoneOffset),
                zoneId = systemZoneOffset,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-03T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-04T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-04T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-07T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-06T02:00:00Z".toInstant(systemZoneOffset),
                end = "2018-12-07T04:00:00Z".toInstant(systemZoneOffset),
                zoneId = systemZoneOffset,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-07T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-10T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-11T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-10T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-11T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-10T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-11T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-10T12:00:00Z".toInstant(systemZoneOffset),
                end = "2018-12-11T13:00:00Z".toInstant(systemZoneOffset),
                zoneId = systemZoneOffset,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-18T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-19T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = true
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-27T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-28T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-30T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-31T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-30T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-31T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-30T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-31T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-30T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-31T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2018-12-30T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2018-12-31T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2019-01-01T05:00:00Z".toInstant(systemZoneOffset),
                end = "2019-12-02T11:20:00Z".toInstant(systemZoneOffset),
                zoneId = systemZoneOffset,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2019-01-02T05:00:00Z".toInstant(systemZoneOffset),
                end = "2019-10-02T11:20:00Z".toInstant(systemZoneOffset),
                zoneId = systemZoneOffset,
                isDeclined = true
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2019-01-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2019-01-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2019-01-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2019-01-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2019-01-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2019-01-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2019-01-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2019-01-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2019-01-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2019-01-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2019-01-05T00:00:00Z".toInstant(ZoneOffset.UTC),
                end = "2019-01-06T00:00:00Z".toInstant(ZoneOffset.UTC),
                zoneId = ZoneOffset.UTC,
                isDeclined = false
            )
        )
    }

    @Suppress("UnusedPrivateMember")
    private fun getDrawDaysUseCaseTestProperties() = Stream.of(
        DrawDaysUseCaseTestProperties(" 26 ·", darkThemeCellTextColour),
        DrawDaysUseCaseTestProperties(" 27  ", darkThemeCellTextColour),
        DrawDaysUseCaseTestProperties(" 28 ·", darkThemeCellTextColour),
        DrawDaysUseCaseTestProperties(" 29 ·", darkThemeCellTextColour),
        DrawDaysUseCaseTestProperties(" 30  ", darkThemeCellTextColour),
        DrawDaysUseCaseTestProperties("  1  ", darkThemeInMonthCellTextColour, saturdayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties("  2  ", darkThemeInMonthCellTextColour, sundayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties("  3 ·", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties("  4 ·", darkThemeInMonthCellTextColour, todayDarkThemeCellBackground,
            isToday = true, instancesColour = instancesColourTodayId
        ),
        DrawDaysUseCaseTestProperties("  5  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties("  6 ∴", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties("  7 ·", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties("  8  ", darkThemeInMonthCellTextColour, saturdayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties("  9  ", darkThemeInMonthCellTextColour, sundayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 10 ∷", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 11 ·", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 12  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 13  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 14  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 15  ", darkThemeInMonthCellTextColour, saturdayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 16  ", darkThemeInMonthCellTextColour, sundayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 17  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 18  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 19  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 20  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 21  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 22  ", darkThemeInMonthCellTextColour, saturdayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 23  ", darkThemeInMonthCellTextColour, sundayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 24  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 25  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 26  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 27 ·", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 28  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 29  ", darkThemeInMonthCellTextColour, saturdayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 30 ◇", darkThemeInMonthCellTextColour, sundayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(" 31  ", darkThemeInMonthCellTextColour, inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties("  1 ·", darkThemeCellTextColour),
        DrawDaysUseCaseTestProperties("  2 ·", darkThemeCellTextColour),
        DrawDaysUseCaseTestProperties("  3 ·", darkThemeCellTextColour),
        DrawDaysUseCaseTestProperties("  4 ·", darkThemeCellTextColour),
        DrawDaysUseCaseTestProperties("  5 ◈", darkThemeCellTextColour, saturdayDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties("  6 ·", darkThemeCellTextColour, sundayDarkThemeCellBackground)
    )

    private fun String.toInstant(zoneOffset: ZoneOffset) = LocalDateTime
        .parse(this, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        .toInstant(zoneOffset)

    @Suppress("UnusedPrivateMember")
    private fun getSystemLocalDateAndFirstDayOfWeekWithExpectedCurrentWeekFocusedInitialLocalDate() = Stream.of(
        Arguments.of(of(2022, 2, 24), DayOfWeek.MONDAY, of(2022, 2, 14)),
        Arguments.of(of(2022, 2, 27), DayOfWeek.MONDAY, of(2022, 2, 14)),
        Arguments.of(of(2022, 2, 28), DayOfWeek.MONDAY, of(2022, 2, 21)),
        Arguments.of(of(2022, 2, 28), DayOfWeek.TUESDAY, of(2022, 2, 15)),
        Arguments.of(of(2022, 3, 1), DayOfWeek.TUESDAY, of(2022, 2, 22)),
        Arguments.of(of(2022, 1, 1), DayOfWeek.WEDNESDAY, of(2021, 12, 22)),
        Arguments.of(of(2022, 1, 1), DayOfWeek.SATURDAY, of(2021, 12, 25)),
        Arguments.of(of(2022, 2, 20), DayOfWeek.SUNDAY, of(2022, 2, 13)),
        Arguments.of(of(2022, 2, 25), DayOfWeek.SUNDAY, of(2022, 2, 13)),
        Arguments.of(of(2022, 2, 26), DayOfWeek.SUNDAY, of(2022, 2, 13)),
        Arguments.of(of(2022, 2, 27), DayOfWeek.SUNDAY, of(2022, 2, 20))
    )

    @Suppress("UnusedPrivateMember")
    private fun getSystemLocalDateAndFirstDayOfWeekWithExpectedNaturalMonthInitialLocalDate() = Stream.of(
        Arguments.of(of(2018, 1, 26), DayOfWeek.MONDAY, of(2018, 1, 1)),
        Arguments.of(of(2018, 1, 26), DayOfWeek.TUESDAY, of(2017, 12, 26)),
        Arguments.of(of(2018, 1, 26), DayOfWeek.WEDNESDAY, of(2017, 12, 27)),
        Arguments.of(of(2018, 1, 26), DayOfWeek.THURSDAY, of(2017, 12, 28)),
        Arguments.of(of(2018, 1, 26), DayOfWeek.FRIDAY, of(2017, 12, 29)),
        Arguments.of(of(2018, 1, 26), DayOfWeek.SATURDAY, of(2017, 12, 30)),
        Arguments.of(of(2018, 1, 26), DayOfWeek.SUNDAY, of(2017, 12, 31)),
        Arguments.of(of(2005, 2, 19), DayOfWeek.WEDNESDAY, of(2005, 1, 26)),
        Arguments.of(of(2027, 3, 5), DayOfWeek.SUNDAY, of(2027, 2, 28)),
        Arguments.of(of(2099, 4, 30), DayOfWeek.MONDAY, of(2099, 3, 30)),
        Arguments.of(of(2000, 5, 1), DayOfWeek.SATURDAY, of(2000, 4, 29)),
        Arguments.of(of(1998, 6, 2), DayOfWeek.WEDNESDAY, of(1998, 5, 27)),
        Arguments.of(of(1992, 7, 7), DayOfWeek.TUESDAY, of(1992, 6, 30)),
        Arguments.of(of(2018, 8, 1), DayOfWeek.FRIDAY, of(2018, 7, 27)),
        Arguments.of(of(1987, 9, 12), DayOfWeek.FRIDAY, of(1987, 8, 28)),
        Arguments.of(of(2017, 10, 1), DayOfWeek.THURSDAY, of(2017, 9, 28)),
        Arguments.of(of(1000, 11, 12), DayOfWeek.SATURDAY, of(1000, 11, 1)),
        Arguments.of(of(1994, 12, 13), DayOfWeek.THURSDAY, of(1994, 12, 1)),
        Arguments.of(of(2021, 2, 13), DayOfWeek.MONDAY, of(2021, 2, 1)),
        Arguments.of(of(2021, 3, 13), DayOfWeek.MONDAY, of(2021, 3, 1))
    )!!

    @Suppress("UnusedPrivateMember", "LongMethod")
    private fun getLocalDateAndIncludeDeclinedEventsWithExpectedNumberOfInstances() = Stream.of(
        Arguments.of(of(2018, 11, 26), false, 1),
        Arguments.of(of(2018, 11, 27), false, 0),
        Arguments.of(of(2018, 11, 28), false, 1),
        Arguments.of(of(2018, 11, 29), false, 1),
        Arguments.of(of(2018, 11, 30), false, 0),
        Arguments.of(of(2018, 12, 1), false, 0),
        Arguments.of(of(2018, 12, 2), false, 0),
        Arguments.of(of(2018, 12, 3), false, 1),
        Arguments.of(of(2018, 12, 4), false, 1),
        Arguments.of(of(2018, 12, 5), false, 0),
        Arguments.of(of(2018, 12, 6), false, 3),
        Arguments.of(of(2018, 12, 7), false, 1),
        Arguments.of(of(2018, 12, 8), false, 0),
        Arguments.of(of(2018, 12, 9), false, 0),
        Arguments.of(of(2018, 12, 10), false, 4),
        Arguments.of(of(2018, 12, 11), false, 1),
        Arguments.of(of(2018, 12, 12), false, 0),
        Arguments.of(of(2018, 12, 13), false, 0),
        Arguments.of(of(2018, 12, 14), false, 0),
        Arguments.of(of(2018, 12, 15), false, 0),
        Arguments.of(of(2018, 12, 16), false, 0),
        Arguments.of(of(2018, 12, 17), false, 0),
        Arguments.of(of(2018, 12, 18), false, 0),
        Arguments.of(of(2018, 12, 19), false, 0),
        Arguments.of(of(2018, 12, 20), false, 0),
        Arguments.of(of(2018, 12, 21), false, 0),
        Arguments.of(of(2018, 12, 22), false, 0),
        Arguments.of(of(2018, 12, 23), false, 0),
        Arguments.of(of(2018, 12, 24), false, 0),
        Arguments.of(of(2018, 12, 25), false, 0),
        Arguments.of(of(2018, 12, 26), false, 0),
        Arguments.of(of(2018, 12, 27), false, 1),
        Arguments.of(of(2018, 12, 28), false, 0),
        Arguments.of(of(2018, 12, 29), false, 0),
        Arguments.of(of(2018, 12, 30), false, 5),
        Arguments.of(of(2018, 12, 31), false, 0),
        Arguments.of(of(2019, 1, 1), false, 1),
        Arguments.of(of(2019, 1, 2), false, 1),
        Arguments.of(of(2019, 1, 3), false, 1),
        Arguments.of(of(2019, 1, 4), false, 1),
        Arguments.of(of(2019, 1, 5), false, 7),
        Arguments.of(of(2019, 1, 6), false, 1),
        Arguments.of(of(2018, 11, 26), true, 1),
        Arguments.of(of(2018, 11, 27), true, 0),
        Arguments.of(of(2018, 11, 28), true, 1),
        Arguments.of(of(2018, 11, 29), true, 1),
        Arguments.of(of(2018, 11, 30), true, 0),
        Arguments.of(of(2018, 12, 1), true, 0),
        Arguments.of(of(2018, 12, 2), true, 0),
        Arguments.of(of(2018, 12, 3), true, 1),
        Arguments.of(of(2018, 12, 4), true, 1),
        Arguments.of(of(2018, 12, 5), true, 0),
        Arguments.of(of(2018, 12, 6), true, 3),
        Arguments.of(of(2018, 12, 7), true, 1),
        Arguments.of(of(2018, 12, 8), true, 0),
        Arguments.of(of(2018, 12, 9), true, 0),
        Arguments.of(of(2018, 12, 10), true, 4),
        Arguments.of(of(2018, 12, 11), true, 1),
        Arguments.of(of(2018, 12, 12), true, 0),
        Arguments.of(of(2018, 12, 13), true, 0),
        Arguments.of(of(2018, 12, 14), true, 0),
        Arguments.of(of(2018, 12, 15), true, 0),
        Arguments.of(of(2018, 12, 16), true, 0),
        Arguments.of(of(2018, 12, 17), true, 0),
        Arguments.of(of(2018, 12, 18), true, 1),
        Arguments.of(of(2018, 12, 19), true, 0),
        Arguments.of(of(2018, 12, 20), true, 0),
        Arguments.of(of(2018, 12, 21), true, 0),
        Arguments.of(of(2018, 12, 22), true, 0),
        Arguments.of(of(2018, 12, 23), true, 0),
        Arguments.of(of(2018, 12, 24), true, 0),
        Arguments.of(of(2018, 12, 25), true, 0),
        Arguments.of(of(2018, 12, 26), true, 0),
        Arguments.of(of(2018, 12, 27), true, 1),
        Arguments.of(of(2018, 12, 28), true, 0),
        Arguments.of(of(2018, 12, 29), true, 0),
        Arguments.of(of(2018, 12, 30), true, 5),
        Arguments.of(of(2018, 12, 31), true, 0),
        Arguments.of(of(2019, 1, 1), true, 1),
        Arguments.of(of(2019, 1, 2), true, 2),
        Arguments.of(of(2019, 1, 3), true, 2),
        Arguments.of(of(2019, 1, 4), true, 2),
        Arguments.of(of(2019, 1, 5), true, 8),
        Arguments.of(of(2019, 1, 6), true, 2)
    )!!

    internal data class DrawDaysUseCaseTestProperties(
        val text: String,
        val textColour: Int,
        val dayBackgroundColour: Int? = null,
        val dayLayout: Int = darkThemeCellLayout,
        val isToday: Boolean = false,
        val symbolRelativeSize: Float = 1f,
        val instancesColour: Int = instancesColourId
    )
}
