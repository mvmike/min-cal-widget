// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Stream

internal class DrawMonthAndYearHeaderUseCaseTest : BaseTest() {

    private val widgetRv = mock(RemoteViews::class.java)

    @ParameterizedTest
    @MethodSource("getSpreadInstantsInDifferentLocales")
    fun execute(
        instant: Instant,
        locale: Locale,
        expectedMonthAndYear: String
    ) {
        mockGetSystemLocale(locale)
        mockGetSystemInstant(instant)
        mockGetSystemZoneId()

        DrawMonthAndYearHeaderUseCase.execute(context, widgetRv)

        verify(systemResolver, times(1)).getLocale(context)
        verify(systemResolver, times(1)).getInstant()
        verify(systemResolver, times(1)).getSystemZoneId()
        verify(systemResolver, times(1)).createMonthAndYearHeader(widgetRv, expectedMonthAndYear, 0.7f)
        verifyNoMoreInteractions(systemResolver)
    }

    companion object {

        @JvmStatic
        @Suppress("unused", "LongMethod")
        fun getSpreadInstantsInDifferentLocales(): Stream<Arguments> {
            val catalanLocale = Locale("ca", "ES")
            val frenchLocale = Locale("fr", "FR")
            val russianLocale = Locale("ru", "RU")
            val italianLocale = Locale("it", "IT")

            return mapOf(
                "2018-01-26".toInstant() to mapOf(
                    Locale.ENGLISH to "January 2018",
                    catalanLocale to "Gener 2018",
                    frenchLocale to "Janvier 2018",
                    russianLocale to "Января 2018",
                    italianLocale to "Gennaio 2018"
                ),
                "2005-02-19".toInstant() to mapOf(
                    Locale.ENGLISH to "February 2005",
                    catalanLocale to "Febrer 2005",
                    frenchLocale to "Février 2005",
                    russianLocale to "Февраля 2005",
                    italianLocale to "Febbraio 2005"
                ),
                "2027-03-05".toInstant() to mapOf(
                    Locale.ENGLISH to "March 2027",
                    catalanLocale to "Març 2027",
                    frenchLocale to "Mars 2027",
                    russianLocale to "Марта 2027",
                    italianLocale to "Marzo 2027"
                ),
                "2099-04-30".toInstant() to mapOf(
                    Locale.ENGLISH to "April 2099",
                    catalanLocale to "Abril 2099",
                    frenchLocale to "Avril 2099",
                    russianLocale to "Апреля 2099",
                    italianLocale to "Aprile 2099"
                ),
                "2000-05-01".toInstant() to mapOf(
                    Locale.ENGLISH to "May 2000",
                    catalanLocale to "Maig 2000",
                    frenchLocale to "Mai 2000",
                    russianLocale to "Мая 2000",
                    italianLocale to "Maggio 2000"
                ),
                "1998-06-02".toInstant() to mapOf(
                    Locale.ENGLISH to "June 1998",
                    catalanLocale to "Juny 1998",
                    frenchLocale to "Juin 1998",
                    russianLocale to "Июня 1998",
                    italianLocale to "Giugno 1998"
                ),
                "1992-07-07".toInstant() to mapOf(
                    Locale.ENGLISH to "July 1992",
                    catalanLocale to "Juliol 1992",
                    frenchLocale to "Juillet 1992",
                    russianLocale to "Июля 1992",
                    italianLocale to "Luglio 1992"
                ),
                "2018-08-01".toInstant() to mapOf(
                    Locale.ENGLISH to "August 2018",
                    catalanLocale to "Agost 2018",
                    frenchLocale to "Août 2018",
                    russianLocale to "Августа 2018",
                    italianLocale to "Agosto 2018"
                ),
                "1987-09-12".toInstant() to mapOf(
                    Locale.ENGLISH to "September 1987",
                    catalanLocale to "Setembre 1987",
                    frenchLocale to "Septembre 1987",
                    russianLocale to "Сентября 1987",
                    italianLocale to "Settembre 1987"
                ),
                "2017-10-01".toInstant() to mapOf(
                    Locale.ENGLISH to "October 2017",
                    catalanLocale to "Octubre 2017",
                    frenchLocale to "Octobre 2017",
                    russianLocale to "Октября 2017",
                    italianLocale to "Ottobre 2017"
                ),
                "1000-11-12".toInstant() to mapOf(
                    Locale.ENGLISH to "November 1000",
                    catalanLocale to "Novembre 1000",
                    frenchLocale to "Novembre 1000",
                    russianLocale to "Ноября 1000",
                    italianLocale to "Novembre 1000"
                ),
                "1994-12-13".toInstant() to mapOf(
                    Locale.ENGLISH to "December 1994",
                    catalanLocale to "Desembre 1994",
                    frenchLocale to "Décembre 1994",
                    russianLocale to "Декабря 1994",
                    italianLocale to "Dicembre 1994"
                )
            ).map { instantAndTestCases ->
                instantAndTestCases.value.entries
                    .map { Arguments.of(instantAndTestCases.key, it.key, it.value) }
            }.flatten().stream()
        }

        private fun String.toInstant() = LocalDateTime
            .parse(this.plus("T00:00:00Z"), DateTimeFormatter.ISO_ZONED_DATE_TIME)
            .toInstant(ZoneOffset.UTC)
    }
}
