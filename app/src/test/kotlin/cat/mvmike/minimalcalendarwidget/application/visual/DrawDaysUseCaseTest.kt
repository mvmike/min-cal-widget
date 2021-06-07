// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.entry.Instance
import cat.mvmike.minimalcalendarwidget.domain.entry.toStartOfDayInEpochMilli
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Stream

internal class DrawDaysUseCaseTest : BaseTest() {

    private val widgetRv = mock(RemoteViews::class.java)
    private val rowRv = mock(RemoteViews::class.java)

    @Test
    fun setDays_shouldReturnSafeDateSpanOfSystemTimeZoneInstances() {
        mockGetSystemLocalDate()
        mockGetSystemZoneId()

        mockIsReadCalendarPermitted(true)
        val systemInstances = getSystemInstances()
        val initLocalDate = systemLocalDate.minusDays(45)
        val endLocalDate = systemLocalDate.plusDays(45)
        val initEpochMillis = initLocalDate.toStartOfDayInEpochMilli()
        val endEpochMillis = endLocalDate.toStartOfDayInEpochMilli()
        `when`(systemResolver.getInstances(context, initEpochMillis, endEpochMillis)).thenReturn(systemInstances)

        val instancesColour = Colour.CYAN
        mockSharedPreferences()
        mockFirstDayOfWeek(DayOfWeek.MONDAY)
        mockCalendarTheme(Theme.BLACK)
        mockInstancesSymbolSet(SymbolSet.MINIMAL)
        mockInstancesColour(instancesColour)

        val instancesColourTodayId = 1
        val instancesColourId = 2
        `when`(systemResolver.getInstancesColorTodayId(context)).thenReturn(instancesColourTodayId)
        `when`(systemResolver.getInstancesColorId(context, instancesColour)).thenReturn(instancesColourId)

        `when`(systemResolver.createDaysRow(context)).thenReturn(rowRv)

        DrawDaysUseCase.execute(context, widgetRv)

        verify(systemResolver, times(1)).getSystemLocalDate()
        verify(systemResolver, times(1)).isReadCalendarPermitted(context)
        verify(systemResolver, times(1)).getInstances(context, initEpochMillis, endEpochMillis)
        verify(systemResolver, times(4)).getSystemZoneId()
        verify(systemResolver, times(6)).createDaysRow(context)

        val inOrder = inOrder(systemResolver)
        getDrawDaysUseCaseTestProperties().forEach { dayUseCaseTest ->
            when (dayUseCaseTest.isToday) {
                true -> inOrder.verify(systemResolver, times(1)).getInstancesColorTodayId(context)
                false -> inOrder.verify(systemResolver, times(1)).getInstancesColorId(context, instancesColour)
            }
            inOrder.verify(systemResolver, times(1)).addToDaysRow(
                context = context,
                rowRv = rowRv,
                dayLayout = dayUseCaseTest.dayLayout,
                spanText = dayUseCaseTest.spanText,
                isToday = dayUseCaseTest.isToday,
                isSingleDigitDay = dayUseCaseTest.isSingleDigitDay,
                symbolRelativeSize = dayUseCaseTest.symbolRelativeSize,
                instancesColour = dayUseCaseTest.instancesColour
            )
        }
        verify(systemResolver, times(6)).addToWidget(widgetRv, rowRv)
        verifyNoMoreInteractions(systemResolver)
    }

    companion object {

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
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 26 ·", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 27  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 28 ·", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 29 ·", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 30  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427357, spanText = " 01 ·", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427359, spanText = " 02 ·", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 03 ·", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427362, spanText = " 04 ·", isToday = true, isSingleDigitDay = true, instancesColour = 1),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 05  ", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 06 ∴", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 07 ·", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427357, spanText = " 08  ", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427359, spanText = " 09  ", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 10 ∷", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 11 ·", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 12  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 13  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 14  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427357, spanText = " 15  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427359, spanText = " 16  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 17  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 18  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 19  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 20  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 21  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427357, spanText = " 22  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427359, spanText = " 23  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 24  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 25  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 26  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 27 ·", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 28  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427357, spanText = " 29  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427359, spanText = " 30 ◇", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427361, spanText = " 31  ", isSingleDigitDay = false),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 01 ·", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 02 ·", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 03  ", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 04  ", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 05 ◈", isSingleDigitDay = true),
            DrawDaysUseCaseTestProperties(dayLayout = 2131427356, spanText = " 06  ", isSingleDigitDay = true)
        )

        internal data class DrawDaysUseCaseTestProperties(
            val dayLayout: Int,
            val spanText: String,
            val isToday: Boolean = false,
            val isSingleDigitDay: Boolean,
            val symbolRelativeSize: Float = 1.1f,
            val instancesColour: Int = 2
        )

        private fun String.toInstant(zoneOffset: ZoneOffset) = LocalDateTime
            .parse(this, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            .toInstant(zoneOffset)
    }
}
