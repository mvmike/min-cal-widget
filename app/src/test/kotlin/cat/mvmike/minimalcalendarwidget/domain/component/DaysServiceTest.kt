// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.text.Layout
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.Day
import cat.mvmike.minimalcalendarwidget.domain.Instance
import cat.mvmike.minimalcalendarwidget.domain.Instance.AllDayInstance
import cat.mvmike.minimalcalendarwidget.domain.Instance.TimedInstance
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
import java.time.Instant
import java.time.Instant.ofEpochSecond
import java.time.LocalDate
import java.time.LocalDate.parse
import java.time.ZonedDateTime

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
        mockGetSystemZoneId()
        mockIsReadCalendarPermitted(true)

        every {
            CalendarResolver.getInstances(
                context,
                testProperties.expectedInstancesQueryInitEpochMillis,
                testProperties.expectedInstancesQueryEndEpochMillis
            )
        } returns testProperties.systemInstances

        mockFocusOnCurrentWeek(testProperties.focusOnCurrentWeek)
        mockInstancesSymbolSet(testProperties.instancesSymbolSet)
        mockInstancesColour(testProperties.instancesColour)
        mockShowDeclinedEvents(testProperties.showDeclinedEvents)

        every { GraphicResolver.createDaysRow(context) } returns weekRv

        val expectedBackground = random.nextInt()
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
        verifyGetSystemZoneId()
        verifyIsReadCalendarPermitted()
        verify {
            CalendarResolver.getInstances(
                context,
                testProperties.expectedInstancesQueryInitEpochMillis,
                testProperties.expectedInstancesQueryEndEpochMillis
            )
        }

        verifyShowDeclinedEvents()
        verifyFocusOnCurrentWeek()
        verifyInstancesSymbolSet()
        verifyInstancesColour()

        verify(exactly = 6) { GraphicResolver.createDaysRow(context) }

        testProperties.expectedDayProperties.forEach { expectedDayProperties ->
            val cellDay = testProperties.widgetTheme
                .getCellDay(expectedDayProperties.isInMonth, expectedDayProperties.dayOfWeek)
            cellDay.background?.let {
                verify {
                    GraphicResolver.getColourAsString(context, it)
                    val transparencyRange = when (expectedDayProperties.dayOfWeek) {
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
                            text = expectedDayProperties.dayOfMonth,
                            colour = cellDay.textColour,
                            relativeSize = testProperties.textSize.relativeValue,
                            bold = expectedDayProperties.isToday,
                            highlightDrawable = expectedDayProperties.highlightDrawable,
                            alignment = expectedDayProperties.alignment
                        ),
                        instancesSymbolRemoteView to Cell(
                            text = expectedDayProperties.instancesSymbol.toString(),
                            colour = testProperties.instancesColour
                                .getInstancesColour(expectedDayProperties.isToday, testProperties.widgetTheme),
                            relativeSize = testProperties.textSize.relativeValue
                                * testProperties.instancesSymbolSet.relativeSize,
                            bold = true
                        )
                    )
                )
                ActionableView.CellDay.addListener(
                    context = context,
                    remoteViews = arrayOf(dayRv, instancesSymbolRemoteView),
                    startOfDay = expectedDayProperties.startOfDay
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
        val result = getSystemInstances().getNumberOfInstances(Day(localDate), systemZoneId, includeDeclinedEvents)

        assertThat(result).isEqualTo(expectedNumberOfInstances)
    }

    private fun getSystemInstances(): Set<Instance> = setOf(
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-11-26"),
            end = parse("2018-11-26")
        ),
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-11-28T00:00:00+03:00"),
            end = ZonedDateTime.parse("2018-11-29T09:00:00+03:00")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-03"),
            end = parse("2018-12-03")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-04"),
            end = parse("2018-12-04")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-06"),
            end = parse("2018-12-06")
        ),
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-06T02:00:00+03:00"),
            end = ZonedDateTime.parse("2018-12-07T04:00:00+03:00")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-06"),
            end = parse("2018-12-06")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-10"),
            end = parse("2018-12-10")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-10"),
            end = parse("2018-12-10")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-10"),
            end = parse("2018-12-10")
        ),
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2018-12-10T12:00:00+03:00"),
            end = ZonedDateTime.parse("2018-12-11T13:00:00+03:00")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = true,
            start = parse("2018-12-18"),
            end = parse("2018-12-18")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-27"),
            end = parse("2018-12-27")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-30"),
            end = parse("2018-12-30")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-30"),
            end = parse("2018-12-30")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-30"),
            end = parse("2018-12-30")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-30"),
            end = parse("2018-12-30")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2018-12-30"),
            end = parse("2018-12-30")
        ),
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2019-01-01T05:00:00+03:00"),
            end = ZonedDateTime.parse("2019-10-02T11:20:00+03:00")
        ),
        TimedInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = ZonedDateTime.parse("2019-01-02T05:00:00+03:00"),
            end = ZonedDateTime.parse("2019-08-02T11:20:00+03:00")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2019-01-05"),
            end = parse("2019-01-05")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2019-01-05"),
            end = parse("2019-01-05")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2019-01-05"),
            end = parse("2019-01-05")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2019-01-05"),
            end = parse("2019-01-05")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2019-01-05"),
            end = parse("2019-01-05")
        ),
        AllDayInstance(
            eventId = random.nextInt(),
            isDeclined = false,
            start = parse("2019-01-05"),
            end = parse("2019-01-05")
        )
    )

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
            expectedFirstDay = parse("2018-11-26"),
            expectedInstancesQueryInitEpochMillis = 1543179600000,
            expectedInstancesQueryEndEpochMillis = 1546808400000,
            expectedDayProperties = listOf(
                ExpectedDayProperties("26", '·', MONDAY, ofEpochSecond(1543179600)),
                ExpectedDayProperties("27", ' ', TUESDAY, ofEpochSecond(1543266000)),
                ExpectedDayProperties("28", '·', WEDNESDAY, ofEpochSecond(1543352400)),
                ExpectedDayProperties("29", '·', THURSDAY, ofEpochSecond(1543438800)),
                ExpectedDayProperties("30", ' ', FRIDAY, ofEpochSecond(1543525200)),
                ExpectedDayProperties("1", ' ', SATURDAY, ofEpochSecond(1543611600), true),
                ExpectedDayProperties("2", ' ', SUNDAY, ofEpochSecond(1543698000), true),
                ExpectedDayProperties("3", '·', MONDAY, ofEpochSecond(1543784400), true),
                ExpectedDayProperties(
                    dayOfMonth = "4",
                    instancesSymbol = '·',
                    dayOfWeek = TUESDAY,
                    startOfDay = ofEpochSecond(1543870800),
                    isInMonth = true,
                    isToday = true,
                    highlightDrawable = 2131165277
                ),
                ExpectedDayProperties("5", ' ', WEDNESDAY, ofEpochSecond(1543957200), true),
                ExpectedDayProperties("6", '∴', THURSDAY, ofEpochSecond(1544043600), true),
                ExpectedDayProperties("7", '·', FRIDAY, ofEpochSecond(1544130000), true),
                ExpectedDayProperties("8", ' ', SATURDAY, ofEpochSecond(1544216400), true),
                ExpectedDayProperties("9", ' ', SUNDAY, ofEpochSecond(1544302800), true),
                ExpectedDayProperties("10", '∷', MONDAY, ofEpochSecond(1544389200), true),
                ExpectedDayProperties("11", '·', TUESDAY, ofEpochSecond(1544475600), true),
                ExpectedDayProperties("12", ' ', WEDNESDAY, ofEpochSecond(1544562000), true),
                ExpectedDayProperties("13", ' ', THURSDAY, ofEpochSecond(1544648400), true),
                ExpectedDayProperties("14", ' ', FRIDAY, ofEpochSecond(1544734800), true),
                ExpectedDayProperties("15", ' ', SATURDAY, ofEpochSecond(1544821200), true),
                ExpectedDayProperties("16", ' ', SUNDAY, ofEpochSecond(1544907600), true),
                ExpectedDayProperties("17", ' ', MONDAY, ofEpochSecond(1544994000), true),
                ExpectedDayProperties("18", ' ', TUESDAY, ofEpochSecond(1545080400), true),
                ExpectedDayProperties("19", ' ', WEDNESDAY, ofEpochSecond(1545166800), true),
                ExpectedDayProperties("20", ' ', THURSDAY, ofEpochSecond(1545253200), true),
                ExpectedDayProperties("21", ' ', FRIDAY, ofEpochSecond(1545339600), true),
                ExpectedDayProperties("22", ' ', SATURDAY, ofEpochSecond(1545426000), true),
                ExpectedDayProperties("23", ' ', SUNDAY, ofEpochSecond(1545512400), true),
                ExpectedDayProperties("24", ' ', MONDAY, ofEpochSecond(1545598800), true),
                ExpectedDayProperties("25", ' ', TUESDAY, ofEpochSecond(1545685200), true),
                ExpectedDayProperties("26", ' ', WEDNESDAY, ofEpochSecond(1545771600), true),
                ExpectedDayProperties("27", '·', THURSDAY, ofEpochSecond(1545858000), true),
                ExpectedDayProperties("28", ' ', FRIDAY, ofEpochSecond(1545944400), true),
                ExpectedDayProperties("29", ' ', SATURDAY, ofEpochSecond(1546030800), true),
                ExpectedDayProperties("30", '◇', SUNDAY, ofEpochSecond(1546117200), true),
                ExpectedDayProperties("31", ' ', MONDAY, ofEpochSecond(1546203600), true),
                ExpectedDayProperties("1", '·', TUESDAY, ofEpochSecond(1546290000)),
                ExpectedDayProperties("2", '∶', WEDNESDAY, ofEpochSecond(1546376400)),
                ExpectedDayProperties("3", '∶', THURSDAY, ofEpochSecond(1546462800)),
                ExpectedDayProperties("4", '∶', FRIDAY, ofEpochSecond(1546549200)),
                ExpectedDayProperties("5", '◈', SATURDAY, ofEpochSecond(1546635600)),
                ExpectedDayProperties("6", '∶', SUNDAY, ofEpochSecond(1546722000))
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
            expectedFirstDay = parse("2018-11-25"),
            expectedInstancesQueryInitEpochMillis = 1543093200000,
            expectedInstancesQueryEndEpochMillis = 1546722000000,
            expectedDayProperties = listOf(
                ExpectedDayProperties("25", ' ', SUNDAY, ofEpochSecond(1543093200)),
                ExpectedDayProperties("26", '☱', MONDAY, ofEpochSecond(1543179600)),
                ExpectedDayProperties("27", ' ', TUESDAY, ofEpochSecond(1543266000)),
                ExpectedDayProperties("28", '☱', WEDNESDAY, ofEpochSecond(1543352400)),
                ExpectedDayProperties("29", '☱', THURSDAY, ofEpochSecond(1543438800)),
                ExpectedDayProperties("30", ' ', FRIDAY, ofEpochSecond(1543525200)),
                ExpectedDayProperties("1", ' ', SATURDAY, ofEpochSecond(1543611600), true),
                ExpectedDayProperties("2", ' ', SUNDAY, ofEpochSecond(1543698000), true),
                ExpectedDayProperties("3", '☱', MONDAY, ofEpochSecond(1543784400), true),
                ExpectedDayProperties(
                    dayOfMonth = "4",
                    instancesSymbol = '☱',
                    dayOfWeek = TUESDAY,
                    startOfDay = ofEpochSecond(1543870800),
                    isInMonth = true,
                    isToday = true,
                    highlightDrawable = 2131165278
                ),
                ExpectedDayProperties("5", ' ', WEDNESDAY, ofEpochSecond(1543957200), true),
                ExpectedDayProperties("6", '☳', THURSDAY, ofEpochSecond(1544043600), true),
                ExpectedDayProperties("7", '☱', FRIDAY, ofEpochSecond(1544130000), true),
                ExpectedDayProperties("8", ' ', SATURDAY, ofEpochSecond(1544216400), true),
                ExpectedDayProperties("9", ' ', SUNDAY, ofEpochSecond(1544302800), true),
                ExpectedDayProperties("10", '☴', MONDAY, ofEpochSecond(1544389200), true),
                ExpectedDayProperties("11", '☱', TUESDAY, ofEpochSecond(1544475600), true),
                ExpectedDayProperties("12", ' ', WEDNESDAY, ofEpochSecond(1544562000), true),
                ExpectedDayProperties("13", ' ', THURSDAY, ofEpochSecond(1544648400), true),
                ExpectedDayProperties("14", ' ', FRIDAY, ofEpochSecond(1544734800), true),
                ExpectedDayProperties("15", ' ', SATURDAY, ofEpochSecond(1544821200), true),
                ExpectedDayProperties("16", ' ', SUNDAY, ofEpochSecond(1544907600), true),
                ExpectedDayProperties("17", ' ', MONDAY, ofEpochSecond(1544994000), true),
                ExpectedDayProperties("18", ' ', TUESDAY, ofEpochSecond(1545080400), true),
                ExpectedDayProperties("19", ' ', WEDNESDAY, ofEpochSecond(1545166800), true),
                ExpectedDayProperties("20", ' ', THURSDAY, ofEpochSecond(1545253200), true),
                ExpectedDayProperties("21", ' ', FRIDAY, ofEpochSecond(1545339600), true),
                ExpectedDayProperties("22", ' ', SATURDAY, ofEpochSecond(1545426000), true),
                ExpectedDayProperties("23", ' ', SUNDAY, ofEpochSecond(1545512400), true),
                ExpectedDayProperties("24", ' ', MONDAY, ofEpochSecond(1545598800), true),
                ExpectedDayProperties("25", ' ', TUESDAY, ofEpochSecond(1545685200), true),
                ExpectedDayProperties("26", ' ', WEDNESDAY, ofEpochSecond(1545771600), true),
                ExpectedDayProperties("27", '☱', THURSDAY, ofEpochSecond(1545858000), true),
                ExpectedDayProperties("28", ' ', FRIDAY, ofEpochSecond(1545944400), true),
                ExpectedDayProperties("29", ' ', SATURDAY, ofEpochSecond(1546030800), true),
                ExpectedDayProperties("30", '☵', SUNDAY, ofEpochSecond(1546117200), true),
                ExpectedDayProperties("31", ' ', MONDAY, ofEpochSecond(1546203600), true),
                ExpectedDayProperties("1", '☱', TUESDAY, ofEpochSecond(1546290000)),
                ExpectedDayProperties("2", '☲', WEDNESDAY, ofEpochSecond(1546376400)),
                ExpectedDayProperties("3", '☲', THURSDAY, ofEpochSecond(1546462800)),
                ExpectedDayProperties("4", '☲', FRIDAY, ofEpochSecond(1546549200)),
                ExpectedDayProperties("5", '※', SATURDAY, ofEpochSecond(1546635600))
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
            expectedFirstDay = parse("2018-11-22"),
            expectedInstancesQueryInitEpochMillis = 1542834000000,
            expectedInstancesQueryEndEpochMillis = 1546462800000,
            expectedDayProperties = listOf(
                ExpectedDayProperties("22", ' ', THURSDAY, ofEpochSecond(1542834000), alignment = null),
                ExpectedDayProperties("23", ' ', FRIDAY, ofEpochSecond(1542920400), alignment = null),
                ExpectedDayProperties("24", ' ', SATURDAY, ofEpochSecond(1543006800), alignment = null),
                ExpectedDayProperties("25", ' ', SUNDAY, ofEpochSecond(1543093200), alignment = null),
                ExpectedDayProperties("26", ' ', MONDAY, ofEpochSecond(1543179600), alignment = null),
                ExpectedDayProperties("27", ' ', TUESDAY, ofEpochSecond(1543266000), alignment = null),
                ExpectedDayProperties("28", ' ', WEDNESDAY, ofEpochSecond(1543352400), alignment = null),
                ExpectedDayProperties("29", ' ', THURSDAY, ofEpochSecond(1543438800), alignment = null),
                ExpectedDayProperties("30", ' ', FRIDAY, ofEpochSecond(1543525200), alignment = null),
                ExpectedDayProperties("1", ' ', SATURDAY, ofEpochSecond(1543611600), true, alignment = null),
                ExpectedDayProperties("2", ' ', SUNDAY, ofEpochSecond(1543698000), true, alignment = null),
                ExpectedDayProperties("3", ' ', MONDAY, ofEpochSecond(1543784400), true, alignment = null),
                ExpectedDayProperties(
                    dayOfMonth = "4",
                    instancesSymbol = ' ',
                    dayOfWeek = TUESDAY,
                    startOfDay = ofEpochSecond(1543870800),
                    isInMonth = true,
                    isToday = true,
                    highlightDrawable = 2131165273,
                    alignment = null
                ),
                ExpectedDayProperties("5", ' ', WEDNESDAY, ofEpochSecond(1543957200), true, alignment = null),
                ExpectedDayProperties("6", ' ', THURSDAY, ofEpochSecond(1544043600), true, alignment = null),
                ExpectedDayProperties("7", ' ', FRIDAY, ofEpochSecond(1544130000), true, alignment = null),
                ExpectedDayProperties("8", ' ', SATURDAY, ofEpochSecond(1544216400), true, alignment = null),
                ExpectedDayProperties("9", ' ', SUNDAY, ofEpochSecond(1544302800), true, alignment = null),
                ExpectedDayProperties("10", ' ', MONDAY, ofEpochSecond(1544389200), true, alignment = null),
                ExpectedDayProperties("11", ' ', TUESDAY, ofEpochSecond(1544475600), true, alignment = null),
                ExpectedDayProperties("12", ' ', WEDNESDAY, ofEpochSecond(1544562000), true, alignment = null),
                ExpectedDayProperties("13", ' ', THURSDAY, ofEpochSecond(1544648400), true, alignment = null),
                ExpectedDayProperties("14", ' ', FRIDAY, ofEpochSecond(1544734800), true, alignment = null),
                ExpectedDayProperties("15", ' ', SATURDAY, ofEpochSecond(1544821200), true, alignment = null),
                ExpectedDayProperties("16", ' ', SUNDAY, ofEpochSecond(1544907600), true, alignment = null),
                ExpectedDayProperties("17", ' ', MONDAY, ofEpochSecond(1544994000), true, alignment = null),
                ExpectedDayProperties("18", ' ', TUESDAY, ofEpochSecond(1545080400), true, alignment = null),
                ExpectedDayProperties("19", ' ', WEDNESDAY, ofEpochSecond(1545166800), true, alignment = null),
                ExpectedDayProperties("20", ' ', THURSDAY, ofEpochSecond(1545253200), true, alignment = null),
                ExpectedDayProperties("21", ' ', FRIDAY, ofEpochSecond(1545339600), true, alignment = null),
                ExpectedDayProperties("22", ' ', SATURDAY, ofEpochSecond(1545426000), true, alignment = null),
                ExpectedDayProperties("23", ' ', SUNDAY, ofEpochSecond(1545512400), true, alignment = null),
                ExpectedDayProperties("24", ' ', MONDAY, ofEpochSecond(1545598800), true, alignment = null),
                ExpectedDayProperties("25", ' ', TUESDAY, ofEpochSecond(1545685200), true, alignment = null),
                ExpectedDayProperties("26", ' ', WEDNESDAY, ofEpochSecond(1545771600), true, alignment = null),
                ExpectedDayProperties("27", ' ', THURSDAY, ofEpochSecond(1545858000), true, alignment = null),
                ExpectedDayProperties("28", ' ', FRIDAY, ofEpochSecond(1545944400), true, alignment = null),
                ExpectedDayProperties("29", ' ', SATURDAY, ofEpochSecond(1546030800), true, alignment = null),
                ExpectedDayProperties("30", ' ', SUNDAY, ofEpochSecond(1546117200), true, alignment = null),
                ExpectedDayProperties("31", ' ', MONDAY, ofEpochSecond(1546203600), true, alignment = null),
                ExpectedDayProperties("1", ' ', TUESDAY, ofEpochSecond(1546290000), alignment = null),
                ExpectedDayProperties("2", ' ', WEDNESDAY, ofEpochSecond(1546376400), alignment = null)
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
            expectedFirstDay = parse("2019-11-25"),
            expectedInstancesQueryInitEpochMillis = 1574629200000,
            expectedInstancesQueryEndEpochMillis = 1578258000000,
            expectedDayProperties = listOf(
                ExpectedDayProperties("25", ' ', MONDAY, ofEpochSecond(1574629200), alignment = null),
                ExpectedDayProperties("26", ' ', TUESDAY, ofEpochSecond(1574715600), alignment = null),
                ExpectedDayProperties("27", ' ', WEDNESDAY, ofEpochSecond(1574802000), alignment = null),
                ExpectedDayProperties("28", ' ', THURSDAY, ofEpochSecond(1574888400), alignment = null),
                ExpectedDayProperties("29", ' ', FRIDAY, ofEpochSecond(1574974800), alignment = null),
                ExpectedDayProperties("30", ' ', SATURDAY, ofEpochSecond(1575061200), alignment = null),
                ExpectedDayProperties("1", ' ', SUNDAY, ofEpochSecond(1575147600), true, alignment = null),
                ExpectedDayProperties("2", ' ', MONDAY, ofEpochSecond(1575234000), true, alignment = null),
                ExpectedDayProperties("3", ' ', TUESDAY, ofEpochSecond(1575320400), true, alignment = null),
                ExpectedDayProperties(
                    dayOfMonth = "4",
                    instancesSymbol = ' ',
                    dayOfWeek = WEDNESDAY,
                    startOfDay = ofEpochSecond(1575406800),
                    isInMonth = true,
                    isToday = true,
                    highlightDrawable = 2131165273,
                    alignment = null
                ),
                ExpectedDayProperties("5", ' ', THURSDAY, ofEpochSecond(1575493200), true, alignment = null),
                ExpectedDayProperties("6", ' ', FRIDAY, ofEpochSecond(1575579600), true, alignment = null),
                ExpectedDayProperties("7", ' ', SATURDAY, ofEpochSecond(1575666000), true, alignment = null),
                ExpectedDayProperties("8", ' ', SUNDAY, ofEpochSecond(1575752400), true, alignment = null),
                ExpectedDayProperties("9", ' ', MONDAY, ofEpochSecond(1575838800), true, alignment = null),
                ExpectedDayProperties("10", ' ', TUESDAY, ofEpochSecond(1575925200), true, alignment = null),
                ExpectedDayProperties("11", ' ', WEDNESDAY, ofEpochSecond(1576011600), true, alignment = null),
                ExpectedDayProperties("12", ' ', THURSDAY, ofEpochSecond(1576098000), true, alignment = null),
                ExpectedDayProperties("13", ' ', FRIDAY, ofEpochSecond(1576184400), true, alignment = null),
                ExpectedDayProperties("14", ' ', SATURDAY, ofEpochSecond(1576270800), true, alignment = null),
                ExpectedDayProperties("15", ' ', SUNDAY, ofEpochSecond(1576357200), true, alignment = null),
                ExpectedDayProperties("16", ' ', MONDAY, ofEpochSecond(1576443600), true, alignment = null),
                ExpectedDayProperties("17", ' ', TUESDAY, ofEpochSecond(1576530000), true, alignment = null),
                ExpectedDayProperties("18", ' ', WEDNESDAY, ofEpochSecond(1576616400), true, alignment = null),
                ExpectedDayProperties("19", ' ', THURSDAY, ofEpochSecond(1576702800), true, alignment = null),
                ExpectedDayProperties("20", ' ', FRIDAY, ofEpochSecond(1576789200), true, alignment = null),
                ExpectedDayProperties("21", ' ', SATURDAY, ofEpochSecond(1576875600), true, alignment = null),
                ExpectedDayProperties("22", ' ', SUNDAY, ofEpochSecond(1576962000), true, alignment = null),
                ExpectedDayProperties("23", ' ', MONDAY, ofEpochSecond(1577048400), true, alignment = null),
                ExpectedDayProperties("24", ' ', TUESDAY, ofEpochSecond(1577134800), true, alignment = null),
                ExpectedDayProperties("25", ' ', WEDNESDAY, ofEpochSecond(1577221200), true, alignment = null),
                ExpectedDayProperties("26", ' ', THURSDAY, ofEpochSecond(1577307600), true, alignment = null),
                ExpectedDayProperties("27", ' ', FRIDAY, ofEpochSecond(1577394000), true, alignment = null),
                ExpectedDayProperties("28", ' ', SATURDAY, ofEpochSecond(1577480400), true, alignment = null),
                ExpectedDayProperties("29", ' ', SUNDAY, ofEpochSecond(1577566800), true, alignment = null),
                ExpectedDayProperties("30", ' ', MONDAY, ofEpochSecond(1577653200), true, alignment = null),
                ExpectedDayProperties("31", ' ', TUESDAY, ofEpochSecond(1577739600), true, alignment = null),
                ExpectedDayProperties("1", ' ', WEDNESDAY, ofEpochSecond(1577826000), alignment = null),
                ExpectedDayProperties("2", ' ', THURSDAY, ofEpochSecond(1577912400), alignment = null),
                ExpectedDayProperties("3", ' ', FRIDAY, ofEpochSecond(1577998800), alignment = null),
                ExpectedDayProperties("4", ' ', SATURDAY, ofEpochSecond(1578085200), alignment = null),
                ExpectedDayProperties("5", ' ', SUNDAY, ofEpochSecond(1578171600), alignment = null)
            )
        )
    )

    private fun getSystemLocalDateAndFirstDayOfWeekWithExpectedCurrentWeekFocusedInitialLocalDate() = listOf(
        of(parse("2022-02-24"), MONDAY, parse("2022-02-14")),
        of(parse("2022-02-27"), MONDAY, parse("2022-02-14")),
        of(parse("2022-02-28"), MONDAY, parse("2022-02-21")),
        of(parse("2022-02-28"), TUESDAY, parse("2022-02-15")),
        of(parse("2022-03-01"), TUESDAY, parse("2022-02-22")),
        of(parse("2022-01-01"), WEDNESDAY, parse("2021-12-22")),
        of(parse("2022-01-01"), SATURDAY, parse("2021-12-25")),
        of(parse("2022-02-20"), SUNDAY, parse("2022-02-13")),
        of(parse("2022-02-25"), SUNDAY, parse("2022-02-13")),
        of(parse("2022-02-26"), SUNDAY, parse("2022-02-13")),
        of(parse("2022-02-27"), SUNDAY, parse("2022-02-20"))
    )

    private fun getSystemLocalDateAndFirstDayOfWeekWithExpectedNaturalMonthInitialLocalDate() = listOf(
        of(parse("2018-01-26"), MONDAY, parse("2018-01-01")),
        of(parse("2018-01-26"), TUESDAY, parse("2017-12-26")),
        of(parse("2018-01-26"), WEDNESDAY, parse("2017-12-27")),
        of(parse("2018-01-26"), THURSDAY, parse("2017-12-28")),
        of(parse("2018-01-26"), FRIDAY, parse("2017-12-29")),
        of(parse("2018-01-26"), SATURDAY, parse("2017-12-30")),
        of(parse("2018-01-26"), SUNDAY, parse("2017-12-31")),
        of(parse("2005-02-19"), WEDNESDAY, parse("2005-01-26")),
        of(parse("2027-03-05"), SUNDAY, parse("2027-02-28")),
        of(parse("2099-04-30"), MONDAY, parse("2099-03-30")),
        of(parse("2000-05-01"), SATURDAY, parse("2000-04-29")),
        of(parse("1998-06-02"), WEDNESDAY, parse("1998-05-27")),
        of(parse("1992-07-07"), TUESDAY, parse("1992-06-30")),
        of(parse("2018-08-01"), FRIDAY, parse("2018-07-27")),
        of(parse("1987-09-12"), FRIDAY, parse("1987-08-28")),
        of(parse("2017-10-01"), THURSDAY, parse("2017-09-28")),
        of(parse("1000-11-12"), SATURDAY, parse("1000-11-01")),
        of(parse("1994-12-13"), THURSDAY, parse("1994-12-01")),
        of(parse("2021-02-13"), MONDAY, parse("2021-02-01")),
        of(parse("2021-03-13"), MONDAY, parse("2021-03-01"))
    )

    private fun getLocalDateAndIncludeDeclinedEventsWithExpectedNumberOfInstances() = listOf(
        of(parse("2018-11-26"), false, 1),
        of(parse("2018-11-27"), false, 0),
        of(parse("2018-11-28"), false, 1),
        of(parse("2018-11-29"), false, 1),
        of(parse("2018-11-30"), false, 0),
        of(parse("2018-12-01"), false, 0),
        of(parse("2018-12-02"), false, 0),
        of(parse("2018-12-03"), false, 1),
        of(parse("2018-12-04"), false, 1),
        of(parse("2018-12-05"), false, 0),
        of(parse("2018-12-06"), false, 3),
        of(parse("2018-12-07"), false, 1),
        of(parse("2018-12-08"), false, 0),
        of(parse("2018-12-09"), false, 0),
        of(parse("2018-12-10"), false, 4),
        of(parse("2018-12-11"), false, 1),
        of(parse("2018-12-12"), false, 0),
        of(parse("2018-12-13"), false, 0),
        of(parse("2018-12-14"), false, 0),
        of(parse("2018-12-15"), false, 0),
        of(parse("2018-12-16"), false, 0),
        of(parse("2018-12-17"), false, 0),
        of(parse("2018-12-18"), false, 0),
        of(parse("2018-12-19"), false, 0),
        of(parse("2018-12-20"), false, 0),
        of(parse("2018-12-21"), false, 0),
        of(parse("2018-12-22"), false, 0),
        of(parse("2018-12-23"), false, 0),
        of(parse("2018-12-24"), false, 0),
        of(parse("2018-12-25"), false, 0),
        of(parse("2018-12-26"), false, 0),
        of(parse("2018-12-27"), false, 1),
        of(parse("2018-12-28"), false, 0),
        of(parse("2018-12-29"), false, 0),
        of(parse("2018-12-30"), false, 5),
        of(parse("2018-12-31"), false, 0),
        of(parse("2019-01-01"), false, 1),
        of(parse("2019-01-02"), false, 2),
        of(parse("2019-01-03"), false, 2),
        of(parse("2019-01-04"), false, 2),
        of(parse("2019-01-05"), false, 8),
        of(parse("2019-01-06"), false, 2),
        of(parse("2018-11-26"), true, 1),
        of(parse("2018-11-27"), true, 0),
        of(parse("2018-11-28"), true, 1),
        of(parse("2018-11-29"), true, 1),
        of(parse("2018-11-30"), true, 0),
        of(parse("2018-12-01"), true, 0),
        of(parse("2018-12-02"), true, 0),
        of(parse("2018-12-03"), true, 1),
        of(parse("2018-12-04"), true, 1),
        of(parse("2018-12-05"), true, 0),
        of(parse("2018-12-06"), true, 3),
        of(parse("2018-12-07"), true, 1),
        of(parse("2018-12-08"), true, 0),
        of(parse("2018-12-09"), true, 0),
        of(parse("2018-12-10"), true, 4),
        of(parse("2018-12-11"), true, 1),
        of(parse("2018-12-12"), true, 0),
        of(parse("2018-12-13"), true, 0),
        of(parse("2018-12-14"), true, 0),
        of(parse("2018-12-15"), true, 0),
        of(parse("2018-12-16"), true, 0),
        of(parse("2018-12-17"), true, 0),
        of(parse("2018-12-18"), true, 1),
        of(parse("2018-12-19"), true, 0),
        of(parse("2018-12-20"), true, 0),
        of(parse("2018-12-21"), true, 0),
        of(parse("2018-12-22"), true, 0),
        of(parse("2018-12-23"), true, 0),
        of(parse("2018-12-24"), true, 0),
        of(parse("2018-12-25"), true, 0),
        of(parse("2018-12-26"), true, 0),
        of(parse("2018-12-27"), true, 1),
        of(parse("2018-12-28"), true, 0),
        of(parse("2018-12-29"), true, 0),
        of(parse("2018-12-30"), true, 5),
        of(parse("2018-12-31"), true, 0),
        of(parse("2019-01-01"), true, 1),
        of(parse("2019-01-02"), true, 2),
        of(parse("2019-01-03"), true, 2),
        of(parse("2019-01-04"), true, 2),
        of(parse("2019-01-05"), true, 8),
        of(parse("2019-01-06"), true, 2)
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
        val expectedInstancesQueryInitEpochMillis: Long,
        val expectedInstancesQueryEndEpochMillis: Long,
        val expectedDayProperties: List<ExpectedDayProperties>
    )

    internal data class ExpectedDayProperties(
        val dayOfMonth: String,
        val instancesSymbol: Char,
        val dayOfWeek: DayOfWeek,
        val startOfDay: Instant,
        val isInMonth: Boolean = false,
        val isToday: Boolean = false,
        val highlightDrawable: Int? = null,
        val alignment: Layout.Alignment? = Layout.Alignment.ALIGN_OPPOSITE
    )
}