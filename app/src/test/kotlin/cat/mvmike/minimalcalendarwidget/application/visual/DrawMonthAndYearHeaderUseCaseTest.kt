// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Stream

internal class DrawMonthAndYearHeaderUseCaseTest : BaseTest() {

    private val widgetRv = mock(RemoteViews::class.java)

    @ParameterizedTest
    @MethodSource("getSpreadInstantsWithExpectedMonthAndYearTranslation")
    fun execute(
        instant: Instant,
        expectedMonthAndYear: String
    ) {
        mockGetSystemInstant(instant)
        mockGetSystemZoneId()
        mockGetSystemLocale(Locale.ENGLISH)
        Month.values().forEach {
            `when`(context.getString(it.getExpectedResourceId())).thenReturn(it.getExpectedAbbreviatedString())
        }

        DrawMonthAndYearHeaderUseCase.execute(context, widgetRv)

        verify(systemResolver, times(1)).getLocale(context)
        verify(systemResolver, times(1)).getInstant()
        verify(systemResolver, times(1)).getSystemZoneId()
        verify(systemResolver, times(1)).createMonthAndYearHeader(widgetRv, expectedMonthAndYear, 0.7f)
        verifyNoMoreInteractions(systemResolver)
    }

    companion object {

        @JvmStatic
        @Suppress("unused")
        fun getSpreadInstantsWithExpectedMonthAndYearTranslation() = Stream.of(
            Arguments.of("2018-01-26".toInstant(), "January 2018"),
            Arguments.of("2005-02-19".toInstant(), "February 2005"),
            Arguments.of("2027-03-05".toInstant(), "March 2027"),
            Arguments.of("2099-04-30".toInstant(), "April 2099"),
            Arguments.of("2000-05-01".toInstant(), "May 2000"),
            Arguments.of("1998-06-02".toInstant(), "June 1998"),
            Arguments.of("1992-07-07".toInstant(), "July 1992"),
            Arguments.of("2018-08-01".toInstant(), "August 2018"),
            Arguments.of("1987-09-12".toInstant(), "September 1987"),
            Arguments.of("2017-10-01".toInstant(), "October 2017"),
            Arguments.of("1000-11-12".toInstant(), "November 1000"),
            Arguments.of("1994-12-13".toInstant(), "December 1994")
        )!!

        private fun String.toInstant() = LocalDateTime
            .parse(this.plus("T00:00:00Z"), DateTimeFormatter.ISO_ZONED_DATE_TIME)
            .toInstant(ZoneOffset.UTC)
    }

    private fun Month.getExpectedResourceId() =
        when (this) {
            Month.JANUARY -> R.string.january
            Month.FEBRUARY -> R.string.february
            Month.MARCH -> R.string.march
            Month.APRIL -> R.string.april
            Month.MAY -> R.string.may
            Month.JUNE -> R.string.june
            Month.JULY -> R.string.july
            Month.AUGUST -> R.string.august
            Month.SEPTEMBER -> R.string.september
            Month.OCTOBER -> R.string.october
            Month.NOVEMBER -> R.string.november
            Month.DECEMBER -> R.string.december
        }

    private fun Month.getExpectedAbbreviatedString() =
        when (this) {
            Month.JANUARY -> "January"
            Month.FEBRUARY -> "February"
            Month.MARCH -> "March"
            Month.APRIL -> "April"
            Month.MAY -> "May"
            Month.JUNE -> "June"
            Month.JULY -> "July"
            Month.AUGUST -> "August"
            Month.SEPTEMBER -> "September"
            Month.OCTOBER -> "October"
            Month.NOVEMBER -> "November"
            Month.DECEMBER -> "December"
        }
}