// Copyright (c) 2019, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.header;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;

import android.widget.RemoteViews;

import java.time.DayOfWeek;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import cat.mvmike.minimalcalendarwidget.BaseTest;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme;

import static cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme.BLACK;
import static cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme.GREY;
import static cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme.WHITE;
import static cat.mvmike.minimalcalendarwidget.domain.header.DayHeaderService.setDayHeaders;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DayHeaderServiceTest extends BaseTest {

    private final RemoteViews widgetRv = mock(RemoteViews.class);

    private final RemoteViews headerRowRv = mock(RemoteViews.class);

    @ParameterizedTest
    @MethodSource("combinationOfStartWeekDayAndThemeConfig")
    void setDayHeaders_shouldAddViewBasedOnCurrentDayAndConfig(final DayOfWeek startWeekDay, final Theme theme) {

        mockStartWeekDay(sharedPreferences, startWeekDay);
        mockTheme(sharedPreferences, theme);
        when(systemResolver.createHeaderRow(context)).thenReturn(headerRowRv);
        when(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.MONDAY)).thenReturn("MON");
        when(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.TUESDAY)).thenReturn("TUE");
        when(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.WEDNESDAY)).thenReturn("WED");
        when(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.THURSDAY)).thenReturn("THU");
        when(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.FRIDAY)).thenReturn("FRI");
        when(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.SATURDAY)).thenReturn("SAT");
        when(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.SUNDAY)).thenReturn("SUN");

        setDayHeaders(context, widgetRv);

        verify(systemResolver, times(1)).createHeaderRow(context);

        InOrder inOrder = inOrder(systemResolver);
        rotateWeekDays(startWeekDay.ordinal(), theme)
            .forEach(c -> inOrder.verify(systemResolver, times(1)).addHeaderDayToHeader(context, headerRowRv, c.getKey(), c.getValue()));

        verify(systemResolver, times(1)).addHeaderRowToWidget(widgetRv, headerRowRv);
        verify(systemResolver, times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.MONDAY);
        verify(systemResolver, times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.TUESDAY);
        verify(systemResolver, times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.WEDNESDAY);
        verify(systemResolver, times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.THURSDAY);
        verify(systemResolver, times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.FRIDAY);
        verify(systemResolver, times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.SATURDAY);
        verify(systemResolver, times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.SUNDAY);
        verifyNoMoreInteractions(systemResolver);
    }

    private static Stream<Arguments> combinationOfStartWeekDayAndThemeConfig() {

        return Stream.concat(
            Stream.of(DayOfWeek.values()).map(dayOfWeek -> Arguments.of(dayOfWeek, BLACK)),
            Stream.concat(
                Stream.of(DayOfWeek.values()).map(dayOfWeek -> Arguments.of(dayOfWeek, GREY)),
                Stream.of(DayOfWeek.values()).map(dayOfWeek -> Arguments.of(dayOfWeek, WHITE)))
        );
    }

    private static Stream<Map.Entry<String, Integer>> rotateWeekDays(final int numberOfPositions, final Theme theme) {

        String[] weekdays = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

        String[] result = new String[weekdays.length];
        for (int i = 0; i < weekdays.length; i++) {
            result[(i + (weekdays.length - numberOfPositions)) % weekdays.length] = weekdays[i];
        }

        return Arrays.stream(result)
            .map(c -> getWeekDayWithTheme(c, theme));
    }

    private static Map.Entry<String, Integer> getWeekDayWithTheme(final String weekday, final Theme theme) {

        int cellHeaderThemeId;
        switch (weekday) {

            case "SAT":
                cellHeaderThemeId = theme.getCellHeaderSaturday();
                break;

            case "SUN":
                cellHeaderThemeId = theme.getCellHeaderSunday();
                break;

            default:
                cellHeaderThemeId = theme.getCellHeader();
        }

        return new AbstractMap.SimpleEntry<>(weekday, cellHeaderThemeId);
    }
}
