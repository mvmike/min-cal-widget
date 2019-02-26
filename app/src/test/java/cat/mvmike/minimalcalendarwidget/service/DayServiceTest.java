// Copyright (c) 2019, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;

import android.content.Context;
import android.widget.RemoteViews;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cat.mvmike.minimalcalendarwidget.BaseTest;
import cat.mvmike.minimalcalendarwidget.service.dto.InstanceDto;
import cat.mvmike.minimalcalendarwidget.service.enums.Theme;
import cat.mvmike.minimalcalendarwidget.status.DayStatus;

import static cat.mvmike.minimalcalendarwidget.service.DayService.getDayLayout;
import static cat.mvmike.minimalcalendarwidget.service.DayService.getNumberOfInstances;
import static cat.mvmike.minimalcalendarwidget.service.DayService.setDays;
import static cat.mvmike.minimalcalendarwidget.service.enums.Colour.CYAN;
import static cat.mvmike.minimalcalendarwidget.service.enums.DayOfWeek.MONDAY;
import static cat.mvmike.minimalcalendarwidget.service.enums.Symbol.MINIMAL;
import static cat.mvmike.minimalcalendarwidget.service.enums.Theme.BLACK;
import static cat.mvmike.minimalcalendarwidget.service.enums.Theme.GREY;
import static cat.mvmike.minimalcalendarwidget.service.enums.Theme.WHITE;
import static cat.mvmike.minimalcalendarwidget.status.DayStatusTest.IN_MONTH_SATURDAY;
import static cat.mvmike.minimalcalendarwidget.status.DayStatusTest.IN_MONTH_SUNDAY;
import static cat.mvmike.minimalcalendarwidget.status.DayStatusTest.IN_MONTH_WEEKDAY;
import static cat.mvmike.minimalcalendarwidget.status.DayStatusTest.NOT_IN_MONTH_SATURDAY;
import static cat.mvmike.minimalcalendarwidget.status.DayStatusTest.NOT_IN_MONTH_SUNDAY;
import static cat.mvmike.minimalcalendarwidget.status.DayStatusTest.NOT_IN_MONTH_WEEKDAY;
import static cat.mvmike.minimalcalendarwidget.status.DayStatusTest.TODAY_SATURDAY;
import static cat.mvmike.minimalcalendarwidget.status.DayStatusTest.TODAY_SUNDAY;
import static cat.mvmike.minimalcalendarwidget.status.DayStatusTest.TODAY_WEEKDAY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DayServiceTest extends BaseTest {

    private RemoteViews widgetRv = mock(RemoteViews.class);

    private RemoteViews rowRv = mock(RemoteViews.class);

    private RemoteViews cellRv = mock(RemoteViews.class);

    @Test
    void setDays_shouldReturnSafeDateSpanOfSystemTimeZoneInstances() {

        reset(widgetRv);

        mockStartWeekDay(sharedPreferences, MONDAY);
        mockTheme(sharedPreferences, BLACK);
        mockInstancesSymbols(sharedPreferences, MINIMAL);
        mockInstancesSymbolsColour(sharedPreferences, CYAN);

        when(systemResolver.getSystemLocalDate()).thenReturn(LocalDate.of(2018, 12, 4));
        when(systemResolver.isReadCalendarPermitted(context)).thenReturn(true);
        when(systemResolver.createDay(any(Context.class), anyInt())).thenReturn(cellRv);
        when(systemResolver.getInstances(context, 1539982800000L, 1547758800000L)).thenReturn(getSpreadInstances());
        when(systemResolver.getColorInstancesTodayId(context)).thenReturn(98); // today
        when(systemResolver.getColorInstancesId(context, CYAN)).thenReturn(99); // not today
        when(systemResolver.createRow(context)).thenReturn(rowRv);

        setDays(context, widgetRv);

        // per instance
        verify(systemResolver, times(1)).isReadCalendarPermitted(context);
        verify(systemResolver, times(1)).getInstances(context, 1539982800000L, 1547758800000L);
        verify(systemResolver, times(2)).getSystemLocalDate();


        // per week
        verify(systemResolver, times(6)).createRow(context);
        verify(systemResolver, times(6)).addRowToWidget(widgetRv, rowRv);

        verify(systemResolver, times(11)).createDay(context, 2131296284); // out of month
        verify(systemResolver, times(1)).createDay(context, 2131296290); // today
        verify(systemResolver, times(20)).createDay(context, 2131296289); // weekday
        verify(systemResolver, times(5)).createDay(context, 2131296285); // saturday
        verify(systemResolver, times(5)).createDay(context, 2131296287); // sunday

        verify(systemResolver, times(1)).getColorInstancesTodayId(context); // today
        verify(systemResolver, times(41)).getColorInstancesId(context, CYAN); // not today

        InOrder inOrder = inOrder(systemResolver);
        getExpectedDays().forEach(
            c -> inOrder.verify(systemResolver, times(1)).addDayCellRemoteView(rowRv, cellRv, c.getKey(), c.getValue(), 1.2f, c.getValue() ? 98 : 99)
        );

        verifyNoMoreInteractions(systemResolver);
    }

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndDayStatuses")
    void getDayLayout_shouldComputeBasedOnThemeAndDayStatus(final Theme theme, final DayStatus ds, final int expectedResult) {
        assertEquals(expectedResult, getDayLayout(theme, ds));
    }

    @ParameterizedTest
    @MethodSource("getCombinationOfInstanceSetsAndDayStatuses")
    void getNumberOfInstances_shouldComputeBasedOnInstanceSet(final Set<InstanceDto> instanceSet, final DayStatus ds, final int expectedResult) {
        assertEquals(expectedResult, getNumberOfInstances(instanceSet, ds));
    }

    private static Set<InstanceDto> getSpreadInstances() {

        return Stream.of(
            new InstanceDto(1543190400000L, 1543276800000L, 0, 0), // 11/26 all day
            new InstanceDto(1543363200000L, 1543449600000L, 0, 0), // 11/28 all day
            new InstanceDto(1543449600000L, 1543536000000L, 0, 0), // 11/29 all day
            new InstanceDto(1543795200000L, 1543967999000L, 0, 0), // 12/3 all day
            new InstanceDto(1543881600000L, 1543968000000L, 0, 0), // 12/4 all day
            new InstanceDto(1544054400000L, 1544140800000L, 0, 0), // 12/6 all day
            new InstanceDto(1544054400000L, 1544140800000L, 0, 0), // 12/6 all day
            new InstanceDto(1544054400000L, 1544140800000L, 0, 0), // 12/6 all day
            new InstanceDto(1544400000000L, 1544486400000L, 0, 0), // 12/10 all day
            new InstanceDto(1544400000000L, 1544486400000L, 0, 0), // 12/10 all day
            new InstanceDto(1544400000000L, 1544486400000L, 0, 0), // 12/10 all day
            new InstanceDto(1544400000000L, 1544486400000L, 0, 0), // 12/10 all day
            new InstanceDto(1545868800000L, 1545955200000L, 0, 0), // 12/27 all day
            new InstanceDto(1546128000000L, 1546214400000L, 0, 0), // 12/30 all day
            new InstanceDto(1546128000000L, 1546214400000L, 0, 0), // 12/30 all day
            new InstanceDto(1546128000000L, 1546214400000L, 0, 0), // 12/30 all day
            new InstanceDto(1546128000000L, 1546214400000L, 0, 0), // 12/30 all day
            new InstanceDto(1546128000000L, 1546214400000L, 0, 0), // 12/30 all day
            new InstanceDto(1546300800000L, 1546387200000L, 0, 0), // 01/01 all day
            new InstanceDto(1546387200000L, 1546473600000L, 0, 0), // 01/02 all day
            new InstanceDto(1546646400000L, 1546732800000L, 0, 0), // 01/05 all day
            new InstanceDto(1546646400000L, 1546732800000L, 0, 0), // 01/05 all day
            new InstanceDto(1546646400000L, 1546732800000L, 0, 0), // 01/05 all day
            new InstanceDto(1546646400000L, 1546732800000L, 0, 0), // 01/05 all day
            new InstanceDto(1546646400000L, 1546732800000L, 0, 0), // 01/05 all day
            new InstanceDto(1546646400000L, 1546732800000L, 0, 0) // 01/05 all day
        ).collect(Collectors.toCollection(HashSet::new));
    }

    private static Stream<Map.Entry<String, Boolean>> getExpectedDays() {
        return Stream.of(
            new AbstractMap.SimpleEntry<>(" 26 ·", false),
            new AbstractMap.SimpleEntry<>(" 27  ", false),
            new AbstractMap.SimpleEntry<>(" 28 ·", false),
            new AbstractMap.SimpleEntry<>(" 29 ·", false),
            new AbstractMap.SimpleEntry<>(" 30  ", false),
            new AbstractMap.SimpleEntry<>(" 1    ", false),
            new AbstractMap.SimpleEntry<>(" 2    ", false),
            new AbstractMap.SimpleEntry<>(" 3   ·", false),
            new AbstractMap.SimpleEntry<>(" 4   ∶", true),
            new AbstractMap.SimpleEntry<>(" 5   ·", false),
            new AbstractMap.SimpleEntry<>(" 6   ∴", false),
            new AbstractMap.SimpleEntry<>(" 7    ", false),
            new AbstractMap.SimpleEntry<>(" 8    ", false),
            new AbstractMap.SimpleEntry<>(" 9    ", false),
            new AbstractMap.SimpleEntry<>(" 10 ∷", false),
            new AbstractMap.SimpleEntry<>(" 11  ", false),
            new AbstractMap.SimpleEntry<>(" 12  ", false),
            new AbstractMap.SimpleEntry<>(" 13  ", false),
            new AbstractMap.SimpleEntry<>(" 14  ", false),
            new AbstractMap.SimpleEntry<>(" 15  ", false),
            new AbstractMap.SimpleEntry<>(" 16  ", false),
            new AbstractMap.SimpleEntry<>(" 17  ", false),
            new AbstractMap.SimpleEntry<>(" 18  ", false),
            new AbstractMap.SimpleEntry<>(" 19  ", false),
            new AbstractMap.SimpleEntry<>(" 20  ", false),
            new AbstractMap.SimpleEntry<>(" 21  ", false),
            new AbstractMap.SimpleEntry<>(" 22  ", false),
            new AbstractMap.SimpleEntry<>(" 23  ", false),
            new AbstractMap.SimpleEntry<>(" 24  ", false),
            new AbstractMap.SimpleEntry<>(" 25  ", false),
            new AbstractMap.SimpleEntry<>(" 26  ", false),
            new AbstractMap.SimpleEntry<>(" 27 ·", false),
            new AbstractMap.SimpleEntry<>(" 28  ", false),
            new AbstractMap.SimpleEntry<>(" 29  ", false),
            new AbstractMap.SimpleEntry<>(" 30 ◇", false),
            new AbstractMap.SimpleEntry<>(" 31  ", false),
            new AbstractMap.SimpleEntry<>(" 1   ·", false),
            new AbstractMap.SimpleEntry<>(" 2   ·", false),
            new AbstractMap.SimpleEntry<>(" 3    ", false),
            new AbstractMap.SimpleEntry<>(" 4    ", false),
            new AbstractMap.SimpleEntry<>(" 5   ◈", false),
            new AbstractMap.SimpleEntry<>(" 6    ", false)
        );
    }

    private static Stream<Arguments> getCombinationOfThemesAndDayStatuses() {

        return Stream.of(
            Arguments.of(BLACK, TODAY_WEEKDAY, 2131296290),
            Arguments.of(BLACK, TODAY_SATURDAY, 2131296286),
            Arguments.of(BLACK, TODAY_SUNDAY, 2131296288),
            Arguments.of(BLACK, IN_MONTH_WEEKDAY, 2131296289),
            Arguments.of(BLACK, IN_MONTH_SATURDAY, 2131296285),
            Arguments.of(BLACK, IN_MONTH_SUNDAY, 2131296287),
            Arguments.of(BLACK, NOT_IN_MONTH_WEEKDAY, 2131296284),
            Arguments.of(BLACK, NOT_IN_MONTH_SATURDAY, 2131296284),
            Arguments.of(BLACK, NOT_IN_MONTH_SUNDAY, 2131296284),
            Arguments.of(GREY, TODAY_WEEKDAY, 2131296301),
            Arguments.of(GREY, TODAY_SATURDAY, 2131296297),
            Arguments.of(GREY, TODAY_SUNDAY, 2131296299),
            Arguments.of(GREY, IN_MONTH_WEEKDAY, 2131296300),
            Arguments.of(GREY, IN_MONTH_SATURDAY, 2131296296),
            Arguments.of(GREY, IN_MONTH_SUNDAY, 2131296298),
            Arguments.of(GREY, NOT_IN_MONTH_WEEKDAY, 2131296295),
            Arguments.of(GREY, NOT_IN_MONTH_SATURDAY, 2131296295),
            Arguments.of(GREY, NOT_IN_MONTH_SUNDAY, 2131296295),
            Arguments.of(WHITE, TODAY_WEEKDAY, 2131296324),
            Arguments.of(WHITE, TODAY_SATURDAY, 2131296320),
            Arguments.of(WHITE, TODAY_SUNDAY, 2131296322),
            Arguments.of(WHITE, IN_MONTH_WEEKDAY, 2131296323),
            Arguments.of(WHITE, IN_MONTH_SATURDAY, 2131296319),
            Arguments.of(WHITE, IN_MONTH_SUNDAY, 2131296321),
            Arguments.of(WHITE, NOT_IN_MONTH_WEEKDAY, 2131296318),
            Arguments.of(WHITE, NOT_IN_MONTH_SATURDAY, 2131296318),
            Arguments.of(WHITE, NOT_IN_MONTH_SUNDAY, 2131296318)
        );
    }

    private static Stream<Arguments> getCombinationOfInstanceSetsAndDayStatuses() {

        return Stream.of(

            // all in
            Arguments.of(Stream.of(
                new InstanceDto(1543881600000L, 1543967999000L, 0, 0), // 12/4 00:00 - 12/5 00:00
                new InstanceDto(1543950000000L, 1543957200000L, 0, 0) // 12/4 22:00 - 12/5 00:00
            ).collect(Collectors.toCollection(HashSet::new)), TODAY_WEEKDAY, 2),

            // with instances that are in more than 1 day
            Arguments.of(Stream.of(
                new InstanceDto(1543795200000L, 1543967999000L, 0, 1), // 12/3 00:00 - 12/5 00:00
                new InstanceDto(1543881600000L, 1544054400000L, 0, 1), // 12/4 00:00 - 12/6 00:00
                new InstanceDto(1543863600000L, 1543874400000L, 0, 0), // 12/3 22:00 - 12/4 01:00
                new InstanceDto(1543870800000L, 1543993200000L, 0, 1) // 12/4 00:00 - 12/5 10:00
            ).collect(Collectors.toCollection(HashSet::new)), TODAY_WEEKDAY, 4),

            // with instances before and after the day
            Arguments.of(Stream.of(
                new InstanceDto(1543795200000L, 1543881600000L, 0, 0), // 12/3 00:00 - 12/4 00:00
                new InstanceDto(1543968000000L, 1544054400000L, 0, 0), // 12/5 00:00 - 12/6 00:00
                new InstanceDto(1543863600000L, 1543870800000L, 0, 1), // 12/3 22:00 - 12/4 00:00
                new InstanceDto(1543957200000L, 1543993200000L, 0, 0) // 12/5 00:00 - 12/5 10:00
            ).collect(Collectors.toCollection(HashSet::new)), TODAY_WEEKDAY, 0)
        );
    }
}
