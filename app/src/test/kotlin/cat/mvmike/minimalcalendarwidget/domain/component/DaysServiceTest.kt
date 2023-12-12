// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.text.Layout
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.Day
import cat.mvmike.minimalcalendarwidget.domain.Instance
import cat.mvmike.minimalcalendarwidget.domain.atStartOfDayInMillis
import cat.mvmike.minimalcalendarwidget.domain.component.DaysService.getNumberOfInstances
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Cell
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDate.of
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Random

internal class DaysServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    private val weekRv = mockk<RemoteViews>()

    private val dayRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getDaysDrawInputVariablesAndExpectedOutput")
    fun draw_shouldReturnSafeDateSpanOfSystemTimeZoneInstances(testProperties: DrawDaysUseCaseTestProperties) {
        val instancesSymbolRemoteView = when (testProperties.shouldIncludeInstancesSymbolRemoteView) {
            true -> dayRv
            else -> null
        }

        mockkObject(ActionableView.CellDay)
        mockGetSystemLocalDate(testProperties.systemLocalDate)
        mockIsReadCalendarPermitted(true)

        val initEpochMillis = testProperties.expectedFirstDay.atStartOfDayInMillis(zoneId)
        val endEpochMillis = testProperties.expectedFirstDay.plusDays(42).atStartOfDayInMillis(zoneId)
        mockGetSystemZoneId()
        every {
            CalendarResolver.getInstances(context, initEpochMillis, endEpochMillis)
        } returns testProperties.systemInstances

        mockFocusOnCurrentWeek(testProperties.focusOnCurrentWeek)
        mockInstancesSymbolSet(testProperties.instancesSymbolSet)
        mockInstancesColour(testProperties.instancesColour)
        mockShowDeclinedEvents(testProperties.showDeclinedEvents)

        every { GraphicResolver.createDaysRow(context) } returns weekRv

        val expectedBackground = Random().nextInt()
        every { GraphicResolver.getColourAsString(context, any()) } returns "transparentBackground"
        every { GraphicResolver.parseColour(any()) } returns expectedBackground

        every { GraphicResolver.createDayLayout(context) } returns dayRv
        justRun {
            GraphicResolver.addToDaysRow(
                context = context,
                weekRowRemoteView = weekRv,
                backgroundColour = any(),
                cells = any()
            )
        }
        justRun {
            ActionableView.CellDay.addListener(
                context,
                arrayOf(dayRv, instancesSymbolRemoteView),
                any()
            )
        }
        justRun { GraphicResolver.addToWidget(widgetRv, weekRv) }

        DaysService.draw(
            context = context,
            widgetRemoteView = widgetRv,
            firstDayOfWeek = testProperties.firstDayOfWeek,
            widgetTheme = testProperties.widgetTheme,
            transparency = testProperties.transparency,
            textSize = testProperties.textSize
        )

        verifyGetSystemLocalDate()
        verifyIsReadCalendarPermitted()
        verifyGetSystemZoneId()
        verify { CalendarResolver.getInstances(context, initEpochMillis, endEpochMillis) }

        verifyShowDeclinedEvents()
        verifyFocusOnCurrentWeek()
        verifyInstancesSymbolSet()
        verifyInstancesColour()

        verify(exactly = 6) { GraphicResolver.createDaysRow(context) }

        testProperties.expectedDayProperties.forEach { dayUseCaseTest ->
            val cellDay = testProperties.widgetTheme
                .getCellDay(dayUseCaseTest.isInMonth, dayUseCaseTest.dayOfWeek)
            cellDay.background?.let {
                verify {
                    GraphicResolver.getColourAsString(context, it)
                    val transparencyRange = when (dayUseCaseTest.dayOfWeek) {
                        SATURDAY -> TransparencyRange.MODERATE
                        SUNDAY -> TransparencyRange.MODERATE
                        else -> TransparencyRange.LOW
                    }
                    GraphicResolver.parseColour(
                        "#${testProperties.transparency.getAlphaInHex(transparencyRange)}ground"
                    )
                }
            }

            verify {
                GraphicResolver.createDayLayout(context)
                GraphicResolver.createDayLayout(context)
                GraphicResolver.addToDaysRow(
                    context = context,
                    weekRowRemoteView = weekRv,
                    backgroundColour = cellDay.background?.let { expectedBackground },
                    cells = listOf(
                        dayRv to Cell(
                            text = dayUseCaseTest.dayOfMonth,
                            colour = cellDay.textColour,
                            relativeSize = testProperties.textSize.relativeValue,
                            bold = dayUseCaseTest.isToday,
                            highlightDrawable = dayUseCaseTest.dayOfMonthHighlightDrawable,
                            alignment = dayUseCaseTest.dayOfMonthAlignment
                        ),
                        instancesSymbolRemoteView to Cell(
                            text = dayUseCaseTest.instancesSymbol.toString(),
                            colour = testProperties.instancesColour
                                .getInstancesColour(dayUseCaseTest.isToday, testProperties.widgetTheme),
                            relativeSize = testProperties.textSize.relativeValue
                                * testProperties.instancesSymbolSet.relativeSize,
                            bold = true
                        )
                    )
                )
                ActionableView.CellDay.addListener(
                    context = context,
                    remoteViews = arrayOf(dayRv, instancesSymbolRemoteView),
                    startOfDay = dayUseCaseTest.startOfDay(systemZoneOffset)
                )
            }
        }
        verify(exactly = 6) { GraphicResolver.addToWidget(widgetRv, weekRv) }
        confirmVerified(widgetRv, weekRv, dayRv)
    }

    @ParameterizedTest
    @MethodSource("getSystemLocalDateAndFirstDayOfWeekWithExpectedCurrentWeekFocusedInitialLocalDate")
    fun getFocusedOnCurrentWeekInitialLocalDate_shouldReturnWidgetInitialDate(
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
                end = "2019-10-02T11:20:00Z".toInstant(systemZoneOffset),
                zoneId = systemZoneOffset,
                isDeclined = false
            ),
            Instance(
                eventId = random.nextInt(),
                start = "2019-01-02T05:00:00Z".toInstant(systemZoneOffset),
                end = "2019-08-02T11:20:00Z".toInstant(systemZoneOffset),
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

    private fun getDaysDrawInputVariablesAndExpectedOutput() = listOf(
        DrawDaysUseCaseTestProperties(
            systemLocalDate = systemLocalDate,
            systemInstances = getSystemInstances(),
            firstDayOfWeek = MONDAY,
            widgetTheme = Theme.DARK,
            transparency = Transparency(32),
            textSize = TextSize(32),
            focusOnCurrentWeek = false,
            instancesColour = Colour.CYAN,
            instancesSymbolSet = SymbolSet.MINIMAL,
            showDeclinedEvents = false,
            shouldIncludeInstancesSymbolRemoteView = true,
            expectedFirstDay = of(2018, 11, 26),
            expectedDayProperties = listOf(
                ExpectedDayProperties("2018-11-26", "26", '·', MONDAY),
                ExpectedDayProperties("2018-11-27", "27", ' ', TUESDAY),
                ExpectedDayProperties("2018-11-28", "28", '·', WEDNESDAY),
                ExpectedDayProperties("2018-11-29", "29", '·', THURSDAY),
                ExpectedDayProperties("2018-11-30", "30", ' ', FRIDAY),
                ExpectedDayProperties("2018-12-01", "1", ' ', SATURDAY, true),
                ExpectedDayProperties("2018-12-02", "2", ' ', SUNDAY, true),
                ExpectedDayProperties("2018-12-03", "3", '·', MONDAY, true),
                ExpectedDayProperties(
                    day = "2018-12-04",
                    dayOfMonth = "4",
                    instancesSymbol = '·',
                    dayOfWeek = TUESDAY,
                    isInMonth = true,
                    isToday = true,
                    dayOfMonthHighlightDrawable = 2131165277
                ),
                ExpectedDayProperties("2018-12-05", "5", ' ', WEDNESDAY, true),
                ExpectedDayProperties("2018-12-06", "6", '∴', THURSDAY, true),
                ExpectedDayProperties("2018-12-07", "7", '·', FRIDAY, true),
                ExpectedDayProperties("2018-12-08", "8", ' ', SATURDAY, true),
                ExpectedDayProperties("2018-12-09", "9", ' ', SUNDAY, true),
                ExpectedDayProperties("2018-12-10", "10", '∷', MONDAY, true),
                ExpectedDayProperties("2018-12-11", "11", '·', TUESDAY, true),
                ExpectedDayProperties("2018-12-12", "12", ' ', WEDNESDAY, true),
                ExpectedDayProperties("2018-12-13", "13", ' ', THURSDAY, true),
                ExpectedDayProperties("2018-12-14", "14", ' ', FRIDAY, true),
                ExpectedDayProperties("2018-12-15", "15", ' ', SATURDAY, true),
                ExpectedDayProperties("2018-12-16", "16", ' ', SUNDAY, true),
                ExpectedDayProperties("2018-12-17", "17", ' ', MONDAY, true),
                ExpectedDayProperties("2018-12-18", "18", ' ', TUESDAY, true),
                ExpectedDayProperties("2018-12-19", "19", ' ', WEDNESDAY, true),
                ExpectedDayProperties("2018-12-20", "20", ' ', THURSDAY, true),
                ExpectedDayProperties("2018-12-21", "21", ' ', FRIDAY, true),
                ExpectedDayProperties("2018-12-22", "22", ' ', SATURDAY, true),
                ExpectedDayProperties("2018-12-23", "23", ' ', SUNDAY, true),
                ExpectedDayProperties("2018-12-24", "24", ' ', MONDAY, true),
                ExpectedDayProperties("2018-12-25", "25", ' ', TUESDAY, true),
                ExpectedDayProperties("2018-12-26", "26", ' ', WEDNESDAY, true),
                ExpectedDayProperties("2018-12-27", "27", '·', THURSDAY, true),
                ExpectedDayProperties("2018-12-28", "28", ' ', FRIDAY, true),
                ExpectedDayProperties("2018-12-29", "29", ' ', SATURDAY, true),
                ExpectedDayProperties("2018-12-30", "30", '◇', SUNDAY, true),
                ExpectedDayProperties("2018-12-31", "31", ' ', MONDAY, true),
                ExpectedDayProperties("2019-01-01", "1", '·', TUESDAY),
                ExpectedDayProperties("2019-01-02", "2", '·', WEDNESDAY),
                ExpectedDayProperties("2019-01-03", "3", '·', THURSDAY),
                ExpectedDayProperties("2019-01-04", "4", '·', FRIDAY),
                ExpectedDayProperties("2019-01-05", "5", '◈', SATURDAY),
                ExpectedDayProperties("2019-01-06", "6", '·', SUNDAY)
            )
        ),
        DrawDaysUseCaseTestProperties(
            systemLocalDate = systemLocalDate,
            systemInstances = getSystemInstances(),
            firstDayOfWeek = SUNDAY,
            widgetTheme = Theme.LIGHT,
            transparency = Transparency(32),
            textSize = TextSize(50),
            focusOnCurrentWeek = false,
            instancesColour = Colour.YELLOW,
            instancesSymbolSet = SymbolSet.BINARY,
            showDeclinedEvents = false,
            shouldIncludeInstancesSymbolRemoteView = true,
            expectedFirstDay = of(2018, 11, 25),
            expectedDayProperties = listOf(
                ExpectedDayProperties("2018-11-25", "25", ' ', SUNDAY),
                ExpectedDayProperties("2018-11-26", "26", '☱', MONDAY),
                ExpectedDayProperties("2018-11-27", "27", ' ', TUESDAY),
                ExpectedDayProperties("2018-11-28", "28", '☱', WEDNESDAY),
                ExpectedDayProperties("2018-11-29", "29", '☱', THURSDAY),
                ExpectedDayProperties("2018-11-30", "30", ' ', FRIDAY),
                ExpectedDayProperties("2018-12-01", "1", ' ', SATURDAY, true),
                ExpectedDayProperties("2018-12-02", "2", ' ', SUNDAY, true),
                ExpectedDayProperties("2018-12-03", "3", '☱', MONDAY, true),
                ExpectedDayProperties(
                    day = "2018-12-04",
                    dayOfMonth = "4",
                    instancesSymbol = '☱',
                    dayOfWeek = TUESDAY,
                    isInMonth = true,
                    isToday = true,
                    dayOfMonthHighlightDrawable = 2131165278
                ),
                ExpectedDayProperties("2018-12-05", "5", ' ', WEDNESDAY, true),
                ExpectedDayProperties("2018-12-06", "6", '☳', THURSDAY, true),
                ExpectedDayProperties("2018-12-07", "7", '☱', FRIDAY, true),
                ExpectedDayProperties("2018-12-08", "8", ' ', SATURDAY, true),
                ExpectedDayProperties("2018-12-09", "9", ' ', SUNDAY, true),
                ExpectedDayProperties("2018-12-10", "10", '☴', MONDAY, true),
                ExpectedDayProperties("2018-12-11", "11", '☱', TUESDAY, true),
                ExpectedDayProperties("2018-12-12", "12", ' ', WEDNESDAY, true),
                ExpectedDayProperties("2018-12-13", "13", ' ', THURSDAY, true),
                ExpectedDayProperties("2018-12-14", "14", ' ', FRIDAY, true),
                ExpectedDayProperties("2018-12-15", "15", ' ', SATURDAY, true),
                ExpectedDayProperties("2018-12-16", "16", ' ', SUNDAY, true),
                ExpectedDayProperties("2018-12-17", "17", ' ', MONDAY, true),
                ExpectedDayProperties("2018-12-18", "18", ' ', TUESDAY, true),
                ExpectedDayProperties("2018-12-19", "19", ' ', WEDNESDAY, true),
                ExpectedDayProperties("2018-12-20", "20", ' ', THURSDAY, true),
                ExpectedDayProperties("2018-12-21", "21", ' ', FRIDAY, true),
                ExpectedDayProperties("2018-12-22", "22", ' ', SATURDAY, true),
                ExpectedDayProperties("2018-12-23", "23", ' ', SUNDAY, true),
                ExpectedDayProperties("2018-12-24", "24", ' ', MONDAY, true),
                ExpectedDayProperties("2018-12-25", "25", ' ', TUESDAY, true),
                ExpectedDayProperties("2018-12-26", "26", ' ', WEDNESDAY, true),
                ExpectedDayProperties("2018-12-27", "27", '☱', THURSDAY, true),
                ExpectedDayProperties("2018-12-28", "28", ' ', FRIDAY, true),
                ExpectedDayProperties("2018-12-29", "29", ' ', SATURDAY, true),
                ExpectedDayProperties("2018-12-30", "30", '☵', SUNDAY, true),
                ExpectedDayProperties("2018-12-31", "31", ' ', MONDAY, true),
                ExpectedDayProperties("2019-01-01", "1", '☱', TUESDAY),
                ExpectedDayProperties("2019-01-02", "2", '☱', WEDNESDAY),
                ExpectedDayProperties("2019-01-03", "3", '☱', THURSDAY),
                ExpectedDayProperties("2019-01-04", "4", '☱', FRIDAY),
                ExpectedDayProperties("2019-01-05", "5", '☷', SATURDAY)
            )
        ),
        DrawDaysUseCaseTestProperties(
            systemLocalDate = systemLocalDate,
            systemInstances = getSystemInstances(),
            firstDayOfWeek = THURSDAY,
            widgetTheme = Theme.DARK,
            transparency = Transparency(0),
            textSize = TextSize(60),
            focusOnCurrentWeek = true,
            instancesColour = Colour.SYSTEM_ACCENT,
            instancesSymbolSet = SymbolSet.NONE,
            showDeclinedEvents = true,
            shouldIncludeInstancesSymbolRemoteView = false,
            expectedFirstDay = of(2018, 11, 22),
            expectedDayProperties = listOf(
                ExpectedDayProperties("2018-11-22", "22", ' ', THURSDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-11-23", "23", ' ', FRIDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-11-24", "24", ' ', SATURDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-11-25", "25", ' ', SUNDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-11-26", "26", ' ', MONDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-11-27", "27", ' ', TUESDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-11-28", "28", ' ', WEDNESDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-11-29", "29", ' ', THURSDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-11-30", "30", ' ', FRIDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-01", "1", ' ', SATURDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-02", "2", ' ', SUNDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-03", "3", ' ', MONDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties(
                    day = "2018-12-04",
                    dayOfMonth = "4",
                    instancesSymbol = ' ',
                    dayOfWeek = TUESDAY,
                    isInMonth = true,
                    isToday = true,
                    dayOfMonthHighlightDrawable = 2131165273,
                    dayOfMonthAlignment = null
                ),
                ExpectedDayProperties("2018-12-05", "5", ' ', WEDNESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-06", "6", ' ', THURSDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-07", "7", ' ', FRIDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-08", "8", ' ', SATURDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-09", "9", ' ', SUNDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-10", "10", ' ', MONDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-11", "11", ' ', TUESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-12", "12", ' ', WEDNESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-13", "13", ' ', THURSDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-14", "14", ' ', FRIDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-15", "15", ' ', SATURDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-16", "16", ' ', SUNDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-17", "17", ' ', MONDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-18", "18", ' ', TUESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-19", "19", ' ', WEDNESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-20", "20", ' ', THURSDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-21", "21", ' ', FRIDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-22", "22", ' ', SATURDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-23", "23", ' ', SUNDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-24", "24", ' ', MONDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-25", "25", ' ', TUESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-26", "26", ' ', WEDNESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-27", "27", ' ', THURSDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-28", "28", ' ', FRIDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-29", "29", ' ', SATURDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-30", "30", ' ', SUNDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2018-12-31", "31", ' ', MONDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-01-01", "1", ' ', TUESDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-01-02", "2", ' ', WEDNESDAY, dayOfMonthAlignment = null)
            )
        ),
        DrawDaysUseCaseTestProperties(
            systemLocalDate = systemLocalDate.plusYears(1),
            systemInstances = HashSet(),
            firstDayOfWeek = MONDAY,
            widgetTheme = Theme.DARK,
            transparency = Transparency(0),
            textSize = TextSize(60),
            focusOnCurrentWeek = false,
            instancesColour = Colour.SYSTEM_ACCENT,
            instancesSymbolSet = SymbolSet.ROMAN,
            showDeclinedEvents = true,
            shouldIncludeInstancesSymbolRemoteView = false,
            expectedFirstDay = of(2019, 11, 25),
            expectedDayProperties = listOf(
                ExpectedDayProperties("2019-11-25", "25", ' ', MONDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-11-26", "26", ' ', TUESDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-11-27", "27", ' ', WEDNESDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-11-28", "28", ' ', THURSDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-11-29", "29", ' ', FRIDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-11-30", "30", ' ', SATURDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-01", "1", ' ', SUNDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-02", "2", ' ', MONDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-03", "3", ' ', TUESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties(
                    day = "2019-12-04",
                    dayOfMonth = "4",
                    instancesSymbol = ' ',
                    dayOfWeek = WEDNESDAY,
                    isInMonth = true,
                    isToday = true,
                    dayOfMonthHighlightDrawable = 2131165273,
                    dayOfMonthAlignment = null
                ),
                ExpectedDayProperties("2019-12-05", "5", ' ', THURSDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-06", "6", ' ', FRIDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-07", "7", ' ', SATURDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-08", "8", ' ', SUNDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-09", "9", ' ', MONDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-10", "10", ' ', TUESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-11", "11", ' ', WEDNESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-12", "12", ' ', THURSDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-13", "13", ' ', FRIDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-14", "14", ' ', SATURDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-15", "15", ' ', SUNDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-16", "16", ' ', MONDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-17", "17", ' ', TUESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-18", "18", ' ', WEDNESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-19", "19", ' ', THURSDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-20", "20", ' ', FRIDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-21", "21", ' ', SATURDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-22", "22", ' ', SUNDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-23", "23", ' ', MONDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-24", "24", ' ', TUESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-25", "25", ' ', WEDNESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-26", "26", ' ', THURSDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-27", "27", ' ', FRIDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-28", "28", ' ', SATURDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-29", "29", ' ', SUNDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-30", "30", ' ', MONDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2019-12-31", "31", ' ', TUESDAY, true, dayOfMonthAlignment = null),
                ExpectedDayProperties("2020-01-01", "1", ' ', WEDNESDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2020-01-02", "2", ' ', THURSDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2020-01-03", "3", ' ', FRIDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2020-01-04", "4", ' ', SATURDAY, dayOfMonthAlignment = null),
                ExpectedDayProperties("2020-01-05", "5", ' ', SUNDAY, dayOfMonthAlignment = null)
            )
        )
    )

    private fun getSystemLocalDateAndFirstDayOfWeekWithExpectedCurrentWeekFocusedInitialLocalDate() = listOf(
        Arguments.of(of(2022, 2, 24), MONDAY, of(2022, 2, 14)),
        Arguments.of(of(2022, 2, 27), MONDAY, of(2022, 2, 14)),
        Arguments.of(of(2022, 2, 28), MONDAY, of(2022, 2, 21)),
        Arguments.of(of(2022, 2, 28), TUESDAY, of(2022, 2, 15)),
        Arguments.of(of(2022, 3, 1), TUESDAY, of(2022, 2, 22)),
        Arguments.of(of(2022, 1, 1), WEDNESDAY, of(2021, 12, 22)),
        Arguments.of(of(2022, 1, 1), SATURDAY, of(2021, 12, 25)),
        Arguments.of(of(2022, 2, 20), SUNDAY, of(2022, 2, 13)),
        Arguments.of(of(2022, 2, 25), SUNDAY, of(2022, 2, 13)),
        Arguments.of(of(2022, 2, 26), SUNDAY, of(2022, 2, 13)),
        Arguments.of(of(2022, 2, 27), SUNDAY, of(2022, 2, 20))
    )

    private fun getSystemLocalDateAndFirstDayOfWeekWithExpectedNaturalMonthInitialLocalDate() = listOf(
        Arguments.of(of(2018, 1, 26), MONDAY, of(2018, 1, 1)),
        Arguments.of(of(2018, 1, 26), TUESDAY, of(2017, 12, 26)),
        Arguments.of(of(2018, 1, 26), WEDNESDAY, of(2017, 12, 27)),
        Arguments.of(of(2018, 1, 26), THURSDAY, of(2017, 12, 28)),
        Arguments.of(of(2018, 1, 26), FRIDAY, of(2017, 12, 29)),
        Arguments.of(of(2018, 1, 26), SATURDAY, of(2017, 12, 30)),
        Arguments.of(of(2018, 1, 26), SUNDAY, of(2017, 12, 31)),
        Arguments.of(of(2005, 2, 19), WEDNESDAY, of(2005, 1, 26)),
        Arguments.of(of(2027, 3, 5), SUNDAY, of(2027, 2, 28)),
        Arguments.of(of(2099, 4, 30), MONDAY, of(2099, 3, 30)),
        Arguments.of(of(2000, 5, 1), SATURDAY, of(2000, 4, 29)),
        Arguments.of(of(1998, 6, 2), WEDNESDAY, of(1998, 5, 27)),
        Arguments.of(of(1992, 7, 7), TUESDAY, of(1992, 6, 30)),
        Arguments.of(of(2018, 8, 1), FRIDAY, of(2018, 7, 27)),
        Arguments.of(of(1987, 9, 12), FRIDAY, of(1987, 8, 28)),
        Arguments.of(of(2017, 10, 1), THURSDAY, of(2017, 9, 28)),
        Arguments.of(of(1000, 11, 12), SATURDAY, of(1000, 11, 1)),
        Arguments.of(of(1994, 12, 13), THURSDAY, of(1994, 12, 1)),
        Arguments.of(of(2021, 2, 13), MONDAY, of(2021, 2, 1)),
        Arguments.of(of(2021, 3, 13), MONDAY, of(2021, 3, 1))
    )

    private fun getLocalDateAndIncludeDeclinedEventsWithExpectedNumberOfInstances() = listOf(
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
    )

    internal data class DrawDaysUseCaseTestProperties(
        val systemLocalDate: LocalDate,
        val systemInstances: Set<Instance>,
        val firstDayOfWeek: DayOfWeek,
        val widgetTheme: Theme,
        val transparency: Transparency,
        val textSize: TextSize,
        val focusOnCurrentWeek: Boolean,
        val instancesColour: Colour,
        val instancesSymbolSet: SymbolSet,
        val showDeclinedEvents: Boolean,
        val shouldIncludeInstancesSymbolRemoteView: Boolean,
        val expectedFirstDay: LocalDate,
        val expectedDayProperties: List<ExpectedDayProperties>
    )

    internal data class ExpectedDayProperties(
        private val day: String,
        val dayOfMonth: String,
        val instancesSymbol: Char,
        val dayOfWeek: DayOfWeek,
        val isInMonth: Boolean = false,
        val isToday: Boolean = false,
        val dayOfMonthHighlightDrawable: Int? = null,
        val dayOfMonthAlignment: Layout.Alignment? = Layout.Alignment.ALIGN_OPPOSITE
    ) {
        fun startOfDay(zoneOffset: ZoneOffset): Instant =
            LocalDateTime.parse("${day}T00:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant(zoneOffset)
    }
}