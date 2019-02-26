package cat.mvmike.minimalcalendarwidget.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import android.widget.RemoteViews;

import java.time.Instant;
import java.util.Locale;
import java.util.stream.Stream;

import cat.mvmike.minimalcalendarwidget.BaseTest;

import static cat.mvmike.minimalcalendarwidget.service.MonthYearHeaderService.setMonthYearHeader;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class MonthYearHeaderServiceTest extends BaseTest {

    private RemoteViews widgetRv = mock(RemoteViews.class);

    @ParameterizedTest
    @MethodSource("getSpreadInstantsInEnglish")
    void setMonthYearHeader_shouldAddViewBasedOnCurrentMonthAndYearInEnglish(final Instant instant, final String expectedMonthAndYear) {

        when(systemResolver.getInstant()).thenReturn(instant);
        when(localeResolver.getSafeLocale(context)).thenReturn(Locale.ENGLISH);

        setMonthYearHeader(context, widgetRv);

        verify(systemResolver, times(1)).getInstant();
        verify(localeResolver, times(1)).getSafeLocale(context);
        verify(systemResolver, times(1)).createMonthYearHeader(widgetRv, expectedMonthAndYear, 0.7f);
        verifyNoMoreInteractions(systemResolver);
        verifyNoMoreInteractions(localeResolver);
    }

    @ParameterizedTest
    @MethodSource("getSpreadInstantsInCatalan")
    void setMonthYearHeader_shouldAddViewBasedOnCurrentMonthAndYearInCatalan(final Instant instant, final String expectedMonthAndYear) {

        when(systemResolver.getInstant()).thenReturn(instant);
        when(localeResolver.getSafeLocale(context)).thenReturn(new Locale("ca", "ES"));

        setMonthYearHeader(context, widgetRv);

        verify(systemResolver, times(1)).getInstant();
        verify(localeResolver, times(1)).getSafeLocale(context);
        verify(systemResolver, times(1)).createMonthYearHeader(widgetRv, expectedMonthAndYear, 0.7f);
        verifyNoMoreInteractions(systemResolver);
        verifyNoMoreInteractions(localeResolver);
    }

    private static Stream<Arguments> getSpreadInstantsInEnglish() {

        return Stream.of(
            Arguments.of(Instant.ofEpochMilli(896745600000L), "June 1998"), // 1998-06-02 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1516924800000L), "January 2018"), // 2018-01-26 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1804204800000L), "March 2027"), // 2027-03-05 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1108771200000L), "February 2005"), // 2005-02-19 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1544659200000L), "December 2018") // 2018-12-13 00:00 UTC
        );
    }

    private static Stream<Arguments> getSpreadInstantsInCatalan() {

        return Stream.of(
            Arguments.of(Instant.ofEpochMilli(896745600000L), "Juny 1998"), // 1998-06-02 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1516924800000L), "Gener 2018"), // 2018-01-26 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1804204800000L), "Març 2027"), // 2027-03-05 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1108771200000L), "Febrer 2005"), // 2005-02-19 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1544659200000L), "Desembre 2018") // 2018-12-13 00:00 UTC
        );
    }
}
