package cat.mvmike.minimalcalendarwidget.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;

import android.widget.RemoteViews;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import cat.mvmike.minimalcalendarwidget.BaseTest;
import cat.mvmike.minimalcalendarwidget.service.enums.DayOfWeek;
import cat.mvmike.minimalcalendarwidget.service.enums.Theme;

import static cat.mvmike.minimalcalendarwidget.service.DayHeaderService.setDayHeaders;

import static cat.mvmike.minimalcalendarwidget.service.enums.Theme.BLACK;
import static cat.mvmike.minimalcalendarwidget.service.enums.Theme.GREY;
import static cat.mvmike.minimalcalendarwidget.service.enums.Theme.WHITE;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DayHeaderServiceTest extends BaseTest {

    private RemoteViews widgetRv = mock(RemoteViews.class);

    private RemoteViews headerRowRv = mock(RemoteViews.class);

    @ParameterizedTest
    @MethodSource("combinationOfStartWeekDayAndThemeConfig")
    void setDayHeaders_shouldAddViewBasedOnCurrentDayAndConfig(final DayOfWeek startWeekDay, final Theme theme) {

        mockStartWeekDay(sharedPreferences, startWeekDay);
        mockTheme(sharedPreferences, theme);
        when(systemResolver.createHeaderRow(context)).thenReturn(headerRowRv);

        setDayHeaders(context, widgetRv);

        verify(systemResolver, times(1)).createHeaderRow(context);

        InOrder inOrder = inOrder(systemResolver);
        rotateWeekDays(startWeekDay.ordinal(), theme)
            .forEach(c -> inOrder.verify(systemResolver, times(1)).addHeaderDayToHeader(context, headerRowRv, c.getKey(), c.getValue()));

        verify(systemResolver, times(1)).addHeaderRowToWidget(widgetRv, headerRowRv);
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

        Integer cellHeaderThemeId;
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
