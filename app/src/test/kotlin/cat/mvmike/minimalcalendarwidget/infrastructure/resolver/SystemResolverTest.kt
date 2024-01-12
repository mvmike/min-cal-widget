// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import androidx.core.text.util.LocalePreferences
import cat.mvmike.minimalcalendarwidget.BaseTest
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.DayOfWeek
import java.util.Locale

internal class SystemResolverTest : BaseTest() {

    @ParameterizedTest
    @CsvSource(
        "mon,MONDAY",
        "tue,TUESDAY",
        "wed,WEDNESDAY",
        "thu,THURSDAY",
        "fri,FRIDAY",
        "sat,SATURDAY",
        "sun,SUNDAY"
    )
    fun getSystemFirstDayOfWeek_shouldReturnLocalePreferenceValue(
        localPreferenceValue: String,
        expectedDayOfWeek: DayOfWeek
    ) {
        mockkStatic(LocalePreferences::class)
        every {
            LocalePreferences.getFirstDayOfWeek()
        } returns localPreferenceValue

        val result = SystemResolver.getSystemFirstDayOfWeek()

        assertThat(result).isEqualTo(expectedDayOfWeek)
        verify { SystemResolver.getSystemFirstDayOfWeek() }
    }

    @ParameterizedTest
    @CsvSource(
        "ENGLISH,,SUNDAY",
        "en,GB,MONDAY",
        "ru,RU,MONDAY",
        "en,US,SUNDAY",
        "ca,ES,MONDAY",
        "es,ES,MONDAY",
        "fr,FR,MONDAY",
        "iw,IL,SUNDAY",
        "he,IL,SUNDAY",
        "yue,CN,SUNDAY",
        "tr,TR,MONDAY"
    )
    fun getSystemFirstDayOfWeek_shouldReturnLocaleDefaultWhenNoPreferenceValue(
        language: String,
        country: String?,
        expectedDayOfWeek: DayOfWeek
    ) {
        mockkStatic(LocalePreferences::class)
        every { LocalePreferences.getFirstDayOfWeek() } returns ""
        every { SystemResolver.getSystemLocale() } returns Locale(language, country ?: "")

        val result = SystemResolver.getSystemFirstDayOfWeek()

        assertThat(result).isEqualTo(expectedDayOfWeek)
        verify { SystemResolver.getSystemFirstDayOfWeek() }
        verify { SystemResolver.getSystemLocale() }
    }
}