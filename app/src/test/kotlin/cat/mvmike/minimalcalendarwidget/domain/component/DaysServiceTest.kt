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
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.inMonthDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.inMonthDarkThemeCellLayout
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.saturdayDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.saturdayInMonthDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.sundayDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.sundayInMonthDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.todayDarkThemeCellBackground
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.todayDarkThemeCellLayout
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Random
import java.util.stream.Stream

private const val instancesColourTodayId = 2131034188
private const val instancesColourId = 2131034185

private const val dayCellTransparentBackground = "transparentBackground"
private const val dayCellModerateTransparentBackgroundInHex = "#40ground"
private const val dayCellLowTransparentBackgroundInHex = "#18ground"

internal class DaysServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    private val rowRv = mockk<RemoteViews>()

    @ParameterizedTest
    @EnumSource(value = Format::class)
    @SuppressWarnings("LongMethod")
    fun setDays_shouldReturnSafeDateSpanOfSystemTimeZoneInstances(format: Format) {
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

        justRun { GraphicResolver.addToDaysRow(context, rowRv, any(), any(), any(), any(), any(), any(), any(), any()) }
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
                    cellText = dayUseCaseTest.spanText,
                    dayOfMonthInBold = dayUseCaseTest.isToday,
                    instancesColour = dayUseCaseTest.instancesColour,
                    instancesRelativeSize = dayUseCaseTest.symbolRelativeSize,
                    dayBackgroundColour = dayUseCaseTest.dayBackgroundColour?.let { expectedBackground },
                    generalRelativeSize = format.dayCellValueRelativeSize
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

    @Suppress("unused")
    private fun getDrawDaysUseCaseTestProperties() = Stream.of(
        DrawDaysUseCaseTestProperties(dayLayout = darkThemeCellLayout, spanText = " 26 ·"),
        DrawDaysUseCaseTestProperties(dayLayout = darkThemeCellLayout, spanText = " 27  "),
        DrawDaysUseCaseTestProperties(dayLayout = darkThemeCellLayout, spanText = " 28 ·"),
        DrawDaysUseCaseTestProperties(dayLayout = darkThemeCellLayout, spanText = " 29 ·"),
        DrawDaysUseCaseTestProperties(dayLayout = darkThemeCellLayout, spanText = " 30  "),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = "  1  ", dayBackgroundColour = saturdayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = "  2  ", dayBackgroundColour = sundayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = "  3 ·", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(
            dayLayout = todayDarkThemeCellLayout, spanText = "  4 ·", dayBackgroundColour = todayDarkThemeCellBackground,
            isToday = true, instancesColour = instancesColourTodayId
        ),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = "  5  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = "  6 ∴", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = "  7 ·", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = "  8  ", dayBackgroundColour = saturdayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = "  9  ", dayBackgroundColour = sundayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 10 ∷", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 11 ·", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 12  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 13  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 14  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 15  ", dayBackgroundColour = saturdayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 16  ", dayBackgroundColour = sundayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 17  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 18  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 19  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 20  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 21  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 22  ", dayBackgroundColour = saturdayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 23  ", dayBackgroundColour = sundayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 24  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 25  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 26  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 27 ·", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 28  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 29  ", dayBackgroundColour = saturdayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 30 ◇", dayBackgroundColour = sundayInMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = inMonthDarkThemeCellLayout, spanText = " 31  ", dayBackgroundColour = inMonthDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = darkThemeCellLayout, spanText = "  1 ·"),
        DrawDaysUseCaseTestProperties(dayLayout = darkThemeCellLayout, spanText = "  2 ·"),
        DrawDaysUseCaseTestProperties(dayLayout = darkThemeCellLayout, spanText = "  3 ·"),
        DrawDaysUseCaseTestProperties(dayLayout = darkThemeCellLayout, spanText = "  4 ·"),
        DrawDaysUseCaseTestProperties(dayLayout = darkThemeCellLayout, spanText = "  5 ◈", dayBackgroundColour = saturdayDarkThemeCellBackground),
        DrawDaysUseCaseTestProperties(dayLayout = darkThemeCellLayout, spanText = "  6 ·", dayBackgroundColour = sundayDarkThemeCellBackground)
    )

    private fun String.toInstant(zoneOffset: ZoneOffset) = LocalDateTime
        .parse(this, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        .toInstant(zoneOffset)


    @Suppress("unused")
    private fun getSystemLocalDateAndFirstDayOfWeekWithExpectedCurrentWeekFocusedInitialLocalDate() = Stream.of(
        Arguments.of(
            LocalDate.of(2022, 2, 24),
            DayOfWeek.MONDAY,
            LocalDate.of(2022, 2, 14)
        ),
        Arguments.of(
            LocalDate.of(2022, 2, 27),
            DayOfWeek.MONDAY,
            LocalDate.of(2022, 2, 14)
        ),
        Arguments.of(
            LocalDate.of(2022, 2, 28),
            DayOfWeek.MONDAY,
            LocalDate.of(2022, 2, 21)
        ),
        Arguments.of(
            LocalDate.of(2022, 2, 28),
            DayOfWeek.TUESDAY,
            LocalDate.of(2022, 2, 15)
        ),
        Arguments.of(
            LocalDate.of(2022, 3, 1),
            DayOfWeek.TUESDAY,
            LocalDate.of(2022, 2, 22)
        ),
        Arguments.of(
            LocalDate.of(2022, 1, 1),
            DayOfWeek.WEDNESDAY,
            LocalDate.of(2021, 12, 22)
        ),
        Arguments.of(
            LocalDate.of(2022, 1, 1),
            DayOfWeek.SATURDAY,
            LocalDate.of(2021, 12, 25)
        ),
        Arguments.of(
            LocalDate.of(2022, 2, 20),
            DayOfWeek.SUNDAY,
            LocalDate.of(2022, 2, 13)
        ),
        Arguments.of(
            LocalDate.of(2022, 2, 25),
            DayOfWeek.SUNDAY,
            LocalDate.of(2022, 2, 13)
        ),
        Arguments.of(
            LocalDate.of(2022, 2, 26),
            DayOfWeek.SUNDAY,
            LocalDate.of(2022, 2, 13)
        ),
        Arguments.of(
            LocalDate.of(2022, 2, 27),
            DayOfWeek.SUNDAY,
            LocalDate.of(2022, 2, 20)
        )
    )

    @Suppress("unused", "LongMethod")
    private fun getSystemLocalDateAndFirstDayOfWeekWithExpectedNaturalMonthInitialLocalDate() = Stream.of(
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

    @Suppress("unused", "LongMethod")
    private fun getLocalDateAndIncludeDeclinedEventsWithExpectedNumberOfInstances() = Stream.of(
        Arguments.of(LocalDate.of(2018, 11, 26), false, 1),
        Arguments.of(LocalDate.of(2018, 11, 27), false, 0),
        Arguments.of(LocalDate.of(2018, 11, 28), false, 1),
        Arguments.of(LocalDate.of(2018, 11, 29), false, 1),
        Arguments.of(LocalDate.of(2018, 11, 30), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 1), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 2), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 3), false, 1),
        Arguments.of(LocalDate.of(2018, 12, 4), false, 1),
        Arguments.of(LocalDate.of(2018, 12, 5), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 6), false, 3),
        Arguments.of(LocalDate.of(2018, 12, 7), false, 1),
        Arguments.of(LocalDate.of(2018, 12, 8), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 9), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 10), false, 4),
        Arguments.of(LocalDate.of(2018, 12, 11), false, 1),
        Arguments.of(LocalDate.of(2018, 12, 12), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 13), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 14), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 15), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 16), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 17), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 18), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 19), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 20), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 21), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 22), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 23), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 24), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 25), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 26), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 27), false, 1),
        Arguments.of(LocalDate.of(2018, 12, 28), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 29), false, 0),
        Arguments.of(LocalDate.of(2018, 12, 30), false, 5),
        Arguments.of(LocalDate.of(2018, 12, 31), false, 0),
        Arguments.of(LocalDate.of(2019, 1, 1), false, 1),
        Arguments.of(LocalDate.of(2019, 1, 2), false, 1),
        Arguments.of(LocalDate.of(2019, 1, 3), false, 1),
        Arguments.of(LocalDate.of(2019, 1, 4), false, 1),
        Arguments.of(LocalDate.of(2019, 1, 5), false, 7),
        Arguments.of(LocalDate.of(2019, 1, 6), false, 1),
        Arguments.of(LocalDate.of(2018, 11, 26), true, 1),
        Arguments.of(LocalDate.of(2018, 11, 27), true, 0),
        Arguments.of(LocalDate.of(2018, 11, 28), true, 1),
        Arguments.of(LocalDate.of(2018, 11, 29), true, 1),
        Arguments.of(LocalDate.of(2018, 11, 30), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 1), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 2), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 3), true, 1),
        Arguments.of(LocalDate.of(2018, 12, 4), true, 1),
        Arguments.of(LocalDate.of(2018, 12, 5), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 6), true, 3),
        Arguments.of(LocalDate.of(2018, 12, 7), true, 1),
        Arguments.of(LocalDate.of(2018, 12, 8), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 9), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 10), true, 4),
        Arguments.of(LocalDate.of(2018, 12, 11), true, 1),
        Arguments.of(LocalDate.of(2018, 12, 12), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 13), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 14), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 15), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 16), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 17), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 18), true, 1),
        Arguments.of(LocalDate.of(2018, 12, 19), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 20), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 21), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 22), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 23), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 24), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 25), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 26), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 27), true, 1),
        Arguments.of(LocalDate.of(2018, 12, 28), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 29), true, 0),
        Arguments.of(LocalDate.of(2018, 12, 30), true, 5),
        Arguments.of(LocalDate.of(2018, 12, 31), true, 0),
        Arguments.of(LocalDate.of(2019, 1, 1), true, 1),
        Arguments.of(LocalDate.of(2019, 1, 2), true, 2),
        Arguments.of(LocalDate.of(2019, 1, 3), true, 2),
        Arguments.of(LocalDate.of(2019, 1, 4), true, 2),
        Arguments.of(LocalDate.of(2019, 1, 5), true, 8),
        Arguments.of(LocalDate.of(2019, 1, 6), true, 2)
    )!!

    internal data class DrawDaysUseCaseTestProperties(
        val dayLayout: Int,
        val spanText: String,
        val dayBackgroundColour: Int? = null,
        val isToday: Boolean = false,
        val symbolRelativeSize: Float = 1f,
        val instancesColour: Int = instancesColourId
    ) {
        fun isSingleDigitDay() = spanText.startsWith(" 0")
    }
}
