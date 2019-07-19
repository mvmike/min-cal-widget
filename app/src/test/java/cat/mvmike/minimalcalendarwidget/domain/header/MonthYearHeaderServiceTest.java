// Copyright (c) 2019, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.header;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import android.widget.RemoteViews;

import java.time.Instant;
import java.util.Locale;
import java.util.stream.Stream;

import cat.mvmike.minimalcalendarwidget.BaseTest;

import static cat.mvmike.minimalcalendarwidget.domain.header.MonthYearHeaderService.setMonthYearHeader;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class MonthYearHeaderServiceTest extends BaseTest {

    private final RemoteViews widgetRv = mock(RemoteViews.class);

    @ParameterizedTest
    @MethodSource("getSpreadInstantsInEnglish")
    void setMonthYearHeader_shouldAddViewBasedOnCurrentMonthAndYearInEnglish(final Instant instant, final String expectedMonthAndYear) {

        when(systemResolver.getInstant()).thenReturn(instant);
        when(systemResolver.getLocale(context)).thenReturn(Locale.ENGLISH);

        setMonthYearHeader(context, widgetRv);

        verify(systemResolver, times(1)).getInstant();
        verify(systemResolver, times(1)).getLocale(context);
        verify(systemResolver, times(1)).createMonthYearHeader(widgetRv, expectedMonthAndYear, 0.7f);
        verifyNoMoreInteractions(systemResolver);
    }

    @ParameterizedTest
    @MethodSource("getSpreadInstantsInCatalan")
    void setMonthYearHeader_shouldAddViewBasedOnCurrentMonthAndYearInCatalan(final Instant instant, final String expectedMonthAndYear) {

        when(systemResolver.getInstant()).thenReturn(instant);
        when(systemResolver.getLocale(context)).thenReturn(new Locale("ca", "ES"));

        setMonthYearHeader(context, widgetRv);

        verify(systemResolver, times(1)).getInstant();
        verify(systemResolver, times(1)).getLocale(context);
        verify(systemResolver, times(1)).createMonthYearHeader(widgetRv, expectedMonthAndYear, 0.7f);
        verifyNoMoreInteractions(systemResolver);
    }

    @ParameterizedTest
    @MethodSource("getSpreadInstantsInRussian")
    void setMonthYearHeader_shouldAddViewBasedOnCurrentMonthAndYearInRussian(final Instant instant, final String expectedMonthAndYear) {

        when(systemResolver.getInstant()).thenReturn(instant);
        when(systemResolver.getLocale(context)).thenReturn(new Locale("ru", "RU"));

        setMonthYearHeader(context, widgetRv);

        verify(systemResolver, times(1)).getInstant();
        verify(systemResolver, times(1)).getLocale(context);
        verify(systemResolver, times(1)).createMonthYearHeader(widgetRv, expectedMonthAndYear, 0.7f);
        verifyNoMoreInteractions(systemResolver);
    }

    private static Stream<Arguments> getSpreadInstantsInEnglish() {

        return Stream.of(
            Arguments.of(Instant.ofEpochMilli(896745600000L), "June 1998"),      // 1998-06-02 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1516924800000L), "January 2018"),  // 2018-01-26 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1804204800000L), "March 2027"),    // 2027-03-05 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1108771200000L), "February 2005"), // 2005-02-19 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1544659200000L), "December 2018")  // 2018-12-13 00:00 UTC
        );
    }

    private static Stream<Arguments> getSpreadInstantsInCatalan() {

        return Stream.of(
            Arguments.of(Instant.ofEpochMilli(896745600000L), "Juny 1998"),     // 1998-06-02 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1516924800000L), "Gener 2018"),   // 2018-01-26 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1804204800000L), "Març 2027"),    // 2027-03-05 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1108771200000L), "Febrer 2005"),  // 2005-02-19 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1544659200000L), "Desembre 2018") // 2018-12-13 00:00 UTC
        );
    }

    private static Stream<Arguments> getSpreadInstantsInRussian() {

        return Stream.of(
            Arguments.of(Instant.ofEpochMilli(896745600000L), "Июнь 1998"),     // 1998-06-02 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1516924800000L), "Январь 2018"),  // 2018-01-26 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1804204800000L), "Март 2027"),    // 2027-03-05 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1108771200000L), "Февраль 2005"), // 2005-02-19 00:00 UTC
            Arguments.of(Instant.ofEpochMilli(1544659200000L), "Декабрь 2018")  // 2018-12-13 00:00 UTC
        );
    }
}
