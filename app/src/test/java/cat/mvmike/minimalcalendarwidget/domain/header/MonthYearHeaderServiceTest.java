// Copyright (c) 2019, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.header;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import android.widget.RemoteViews;

import java.time.Instant;
import java.util.AbstractMap.SimpleImmutableEntry;
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
    @MethodSource("getSpreadInstantsInDifferentLocales")
    void setMonthYearHeader_shouldAddViewBasedOnCurrentMonthAndYear(final Instant instant,
                                                                    final SimpleImmutableEntry<Locale, String> localeAndExpectedOutput) {

        when(systemResolver.getInstant()).thenReturn(instant);
        when(systemResolver.getLocale(context)).thenReturn(localeAndExpectedOutput.getKey());

        setMonthYearHeader(context, widgetRv);

        verify(systemResolver, times(1)).getInstant();
        verify(systemResolver, times(1)).getLocale(context);
        verify(systemResolver, times(1)).createMonthYearHeader(widgetRv, localeAndExpectedOutput.getValue(), 0.7f);
        verifyNoMoreInteractions(systemResolver);
    }

    private static Stream<Arguments> getSpreadInstantsInDifferentLocales() {
        Locale catalanLocale = new Locale("ca", "ES");
        Locale russianLocale = new Locale("ru", "RU");
        return Stream.of(
            Arguments.of(Instant.ofEpochMilli(896745600000L), // 1998-06-02 00:00 UTC
                    new SimpleImmutableEntry<>(Locale.ENGLISH, "June 1998"),
                    new SimpleImmutableEntry<>(catalanLocale, "Juny 1998"),
                    new SimpleImmutableEntry<>(russianLocale, "Июнь 1998")
            ),
            Arguments.of(Instant.ofEpochMilli(1516924800000L), // 2018-01-26 00:00 UTC
                    new SimpleImmutableEntry<>(Locale.ENGLISH, "January 2018"),
                    new SimpleImmutableEntry<>(catalanLocale, "Gener 2018"),
                    new SimpleImmutableEntry<>(russianLocale, "Январь 2018")
            ),
            Arguments.of(Instant.ofEpochMilli(1804204800000L), // 2027-03-05 00:00 UTC
                    new SimpleImmutableEntry<>(Locale.ENGLISH, "March 2027"),
                    new SimpleImmutableEntry<>(catalanLocale, "Març 2027"),
                    new SimpleImmutableEntry<>(russianLocale, "Март 2027")
            ),
            Arguments.of(Instant.ofEpochMilli(1108771200000L), // 2005-02-19 00:00 UTC
                    new SimpleImmutableEntry<>(Locale.ENGLISH, "February 2005"),
                    new SimpleImmutableEntry<>(catalanLocale, "Febrer 2005"),
                    new SimpleImmutableEntry<>(russianLocale, "Февраль 2005")
            ),
            Arguments.of(Instant.ofEpochMilli(1544659200000L), // 2018-12-13 00:00 UTC
                    new SimpleImmutableEntry<>(Locale.ENGLISH, "December 2018"),
                    new SimpleImmutableEntry<>(catalanLocale, "Desembre 2018"),
                    new SimpleImmutableEntry<>(russianLocale, "Декабрь 2018")
            )
        );
    }
}
