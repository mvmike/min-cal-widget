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
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.Instant
import java.time.Instant.ofEpochSecond
import java.time.LocalDate
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
    @CsvSource(
        "2022-02-24,MONDAY,2022-02-14",
        "2022-02-27,MONDAY,2022-02-14",
        "2022-02-28,MONDAY,2022-02-21",
        "2022-02-28,TUESDAY,2022-02-15",
        "2022-03-01,TUESDAY,2022-02-22",
        "2022-01-01,WEDNESDAY,2021-12-22",
        "2022-01-01,SATURDAY,2021-12-25",
        "2022-02-20,SUNDAY,2022-02-13",
        "2022-02-25,SUNDAY,2022-02-13",
        "2022-02-26,SUNDAY,2022-02-13",
        "2022-02-27,SUNDAY,2022-02-20"
    )
    fun getFocusedOnCurrentWeekInitialLocalDate_shouldReturnWidgetInitialDate(
        systemLocalDate: LocalDate,
        firstDayOfWeek: DayOfWeek,
        expectedInitialLocalDate: LocalDate
    ) {
        val result = DaysService.getFocusedOnCurrentWeekInitialLocalDate(systemLocalDate, firstDayOfWeek)

        assertThat(result).isEqualTo(expectedInitialLocalDate)
    }

    @ParameterizedTest
    @CsvSource(
        "2018-01-26,MONDAY,2018-01-01",
        "2018-01-26,TUESDAY,2017-12-26",
        "2018-01-26,WEDNESDAY,2017-12-27",
        "2018-01-26,THURSDAY,2017-12-28",
        "2018-01-26,FRIDAY,2017-12-29",
        "2018-01-26,SATURDAY,2017-12-30",
        "2018-01-26,SUNDAY,2017-12-31",
        "2005-02-19,WEDNESDAY,2005-01-26",
        "2027-03-05,SUNDAY,2027-02-28",
        "2099-04-30,MONDAY,2099-03-30",
        "2000-05-01,SATURDAY,2000-04-29",
        "1998-06-02,WEDNESDAY,1998-05-27",
        "1992-07-07,TUESDAY,1992-06-30",
        "2018-08-01,FRIDAY,2018-07-27",
        "1987-09-12,FRIDAY,1987-08-28",
        "2017-10-01,THURSDAY,2017-09-28",
        "1000-11-12,SATURDAY,1000-11-01",
        "1994-12-13,THURSDAY,1994-12-01",
        "2021-02-13,MONDAY,2021-02-01",
        "2021-03-13,MONDAY,2021-03-01"
    )
    fun getNaturalMonthInitialLocalDate_shouldReturnWidgetInitialDate(
        systemLocalDate: LocalDate,
        firstDayOfWeek: DayOfWeek,
        expectedInitialLocalDate: LocalDate
    ) {
        val result = DaysService.getNaturalMonthInitialLocalDate(systemLocalDate, firstDayOfWeek)

        assertThat(result).isEqualTo(expectedInitialLocalDate)
    }

    @ParameterizedTest
    @CsvSource(
        "2018-11-26,1,1",
        "2018-11-27,0,0",
        "2018-11-28,1,1",
        "2018-11-29,1,1",
        "2018-11-30,0,0",
        "2018-12-01,0,0",
        "2018-12-02,0,0",
        "2018-12-03,1,1",
        "2018-12-04,1,1",
        "2018-12-05,0,0",
        "2018-12-06,3,3",
        "2018-12-07,1,1",
        "2018-12-08,0,0",
        "2018-12-09,0,0",
        "2018-12-10,4,4",
        "2018-12-11,1,1",
        "2018-12-12,0,0",
        "2018-12-13,0,0",
        "2018-12-14,0,0",
        "2018-12-15,0,0",
        "2018-12-16,0,0",
        "2018-12-17,0,0",
        "2018-12-18,1,0",
        "2018-12-19,0,0",
        "2018-12-20,0,0",
        "2018-12-21,0,0",
        "2018-12-22,0,0",
        "2018-12-23,0,0",
        "2018-12-24,0,0",
        "2018-12-25,0,0",
        "2018-12-26,0,0",
        "2018-12-27,1,1",
        "2018-12-28,0,0",
        "2018-12-29,0,0",
        "2018-12-30,5,3",
        "2018-12-31,0,0",
        "2019-01-01,1,1",
        "2019-01-02,2,2",
        "2019-01-03,2,2",
        "2019-01-04,2,2",
        "2019-01-05,8,7",
        "2019-01-06,2,2"
    )
    fun getNumberOfInstances_shouldReturnEventsInDayAndConsideringDeclinedEvents(
        localDate: LocalDate,
        expectedNumberOfInstancesWithDeclinedEvents: Int,
        expectedNumberOfInstancesWithoutDeclinedEvents: Int
    ) {
        val day = Day(localDate)
        val systemInstances = getSystemInstances()

        val numberOfInstancesWithDeclinedEvents = systemInstances.getNumberOfInstances(day, systemZoneId, true)
        val numberOfInstancesWithoutDeclinedEvents = systemInstances.getNumberOfInstances(day, systemZoneId, false)

        assertThat(numberOfInstancesWithDeclinedEvents).isEqualTo(expectedNumberOfInstancesWithDeclinedEvents)
        assertThat(numberOfInstancesWithoutDeclinedEvents).isEqualTo(expectedNumberOfInstancesWithoutDeclinedEvents)
    }

    private fun getSystemInstances(): Set<Instance> = (
        readTestResourceCsvFile("/system_all_day_instances.csv").map {
            AllDayInstance(
                id = random.nextInt(),
                eventId = random.nextInt(),
                isDeclined = it[0].toBoolean(),
                start = LocalDate.parse(it[1]),
                end = LocalDate.parse(it[2])
            )
        } +
            readTestResourceCsvFile("/system_timed_instances.csv").map {
                TimedInstance(
                    id = random.nextInt(),
                    eventId = random.nextInt(),
                    isDeclined = it[0].toBoolean(),
                    start = ZonedDateTime.parse(it[1]),
                    end = ZonedDateTime.parse(it[2])
                )
            }
    ).toSet()

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
            expectedFirstDay = LocalDate.parse("2018-11-26"),
            expectedInstancesQueryInitEpochMillis = 1543179600000,
            expectedInstancesQueryEndEpochMillis = 1546808400000,
            expectedDayProperties = readTestResourceCsvFile(
                "/test_case/component/day/day_properties_MONDAY_MINIMAL_system_instances.csv"
            ).map { DayProperties(it) }
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
            expectedFirstDay = LocalDate.parse("2018-11-25"),
            expectedInstancesQueryInitEpochMillis = 1543093200000,
            expectedInstancesQueryEndEpochMillis = 1546722000000,
            expectedDayProperties = readTestResourceCsvFile(
                "/test_case/component/day/day_properties_SUNDAY_BINARY_system_instances.csv"
            ).map { DayProperties(it) }
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
            expectedFirstDay = LocalDate.parse("2018-11-22"),
            expectedInstancesQueryInitEpochMillis = 1542834000000,
            expectedInstancesQueryEndEpochMillis = 1546462800000,
            expectedDayProperties = readTestResourceCsvFile(
                "/test_case/component/day/day_properties_THURSDAY_NONE_system_instances.csv"
            ).map { DayProperties(it) }
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
            expectedFirstDay = LocalDate.parse("2019-11-25"),
            expectedInstancesQueryInitEpochMillis = 1574629200000,
            expectedInstancesQueryEndEpochMillis = 1578258000000,
            expectedDayProperties = readTestResourceCsvFile(
                "/test_case/component/day/day_properties_MONDAY_ROMAN_no_instances.csv"
            ).map { DayProperties(it) }
        )
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
        val expectedDayProperties: List<DayProperties>
    )

    internal data class DayProperties(
        val dayOfMonth: String,
        val instancesSymbol: Char,
        val dayOfWeek: DayOfWeek,
        val startOfDay: Instant,
        val isInMonth: Boolean,
        val isToday: Boolean,
        val highlightDrawable: Int?,
        val alignment: Layout.Alignment?
    ) {
        constructor(csvEntry: Array<String>) : this(
            dayOfMonth = csvEntry[0],
            instancesSymbol = csvEntry[1].single(),
            dayOfWeek = DayOfWeek.valueOf(csvEntry[2]),
            startOfDay = ofEpochSecond(csvEntry[3].toLong()),
            isInMonth = csvEntry[4].toBoolean(),
            isToday = csvEntry[5].toBoolean(),
            highlightDrawable = csvEntry[6].takeIf { it.isNotBlank() }?.toInt(),
            alignment = csvEntry[7].takeIf { it.isNotBlank() }?.let { Layout.Alignment.valueOf(it) }
        )
    }
}