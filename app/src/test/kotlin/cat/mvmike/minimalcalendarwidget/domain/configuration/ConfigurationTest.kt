// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Calendar
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import io.mockk.Called
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.DayOfWeek

internal class ConfigurationTest : BaseTest() {

    @Nested
    inner class BooleanConfigurationItems {

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun getShowDeclinedEvents_shouldReturnSharedPreferencesValue(showDeclinedEvents: Boolean) {
            mockShowDeclinedEvents(showDeclinedEvents)

            val result = BooleanConfigurationItem.ShowDeclinedEvents.get(context)

            assertThat(result).isEqualTo(showDeclinedEvents)
            verifyShowDeclinedEvents()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun getFocusOnCurrentWeek_shouldReturnSharedPreferencesValue(focusOnCurrentWeek: Boolean) {
            mockFocusOnCurrentWeek(focusOnCurrentWeek)

            val result = BooleanConfigurationItem.FocusOnCurrentWeek.get(context)

            assertThat(result).isEqualTo(focusOnCurrentWeek)
            verifyFocusOnCurrentWeek()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun getOpenCalendarOnClickedDay_shouldReturnSharedPreferencesValue(openCalendarOnClickedDay: Boolean) {
            mockOpenCalendarOnClickedDay(openCalendarOnClickedDay)

            val result = BooleanConfigurationItem.OpenCalendarOnClickedDay.get(context)

            assertThat(result).isEqualTo(openCalendarOnClickedDay)
            verifyOpenCalendarOnClickedDay()
            verify { editor wasNot Called }
        }
    }

    @Nested
    inner class EnumConfigurationItems {

        @ParameterizedTest
        @EnumSource(value = Theme::class)
        fun getCalendarTheme_shouldReturnSharedPreferencesValue(widgetTheme: Theme) {
            mockWidgetTheme(widgetTheme)

            val result = EnumConfigurationItem.WidgetTheme.get(context)

            assertThat(result).isEqualTo(widgetTheme)
            verifyWidgetTheme()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @EnumSource(value = Calendar::class)
        fun getCalendar_shouldReturnSharedPreferencesValue(widgetCalendar: Calendar) {
            mockWidgetCalendar(widgetCalendar)

            val result = EnumConfigurationItem.WidgetCalendar.get(context)

            assertThat(result).isEqualTo(widgetCalendar)
            verifyWidgetCalendar()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @EnumSource(value = DayOfWeek::class)
        fun getFirstDayOfWeek_shouldReturnSharedPreferencesValue(dayOfWeek: DayOfWeek) {
            mockFirstDayOfWeek(dayOfWeek)

            val result = EnumConfigurationItem.FirstDayOfWeek.get(context)

            assertThat(result).isEqualTo(dayOfWeek)
            verifyFirstDayOfWeek()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @EnumSource(value = SymbolSet::class)
        fun getInstancesSymbolSet_shouldReturnSharedPreferencesValue(symbolSet: SymbolSet) {
            mockInstancesSymbolSet(symbolSet)

            val result = EnumConfigurationItem.InstancesSymbolSet.get(context)

            assertThat(result).isEqualTo(symbolSet)
            verifyInstancesSymbolSet()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @EnumSource(value = Colour::class)
        fun getInstancesColour_shouldReturnSharedPreferencesValue(colour: Colour) {
            mockInstancesColour(colour)

            val result = EnumConfigurationItem.InstancesColour.get(context)

            assertThat(result).isEqualTo(colour)
            verifyInstancesColour()
            verify { editor wasNot Called }
        }
    }

    @Nested
    inner class PercentageConfigurationItems {

        @ParameterizedTest
        @ValueSource(ints = [0, 1, 5, 17, 50, 51, 72, 80, 99, 100])
        fun getWidgetTransparency_shouldReturnSharedPreferencesValue(percentage: Int) {
            val transparency = Transparency(percentage)
            mockWidgetTransparency(transparency)

            val result = PercentageConfigurationItem.WidgetTransparency.get(context)

            assertThat(result).isEqualTo(transparency)
            verifyWidgetTransparency()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @ValueSource(ints = [0, 1, 5, 17, 50, 51, 72, 80, 99, 100])
        fun getWidgetTextSize_shouldReturnSharedPreferencesValue(width: Int) {
            val textSize = TextSize(width)
            mockWidgetTextSize(textSize)

            val result = PercentageConfigurationItem.WidgetTextSize.get(context)

            assertThat(result).isEqualTo(textSize)
            verifyWidgetTextSize()
            verify { editor wasNot Called }
        }
    }

    @ParameterizedTest
    @CsvSource(
        "1,false",
        "5,false",
        "29,false",
        "32,false",
        "33,true",
        "35,true",
        "99,true"
    )
    fun isPerAppLanguagePreferenceEnabled_shouldDependOnSystemSDK(
        sdkVersion: Int,
        expectedAvailability: Boolean
    ) {
        mockGetRuntimeSDK(sdkVersion)

        val result = isPerAppLanguagePreferenceEnabled()

        assertThat(result).isEqualTo(expectedAvailability)
        verifyGetRuntimeSDK()
    }

    @ParameterizedTest
    @CsvSource(
        "1,false",
        "5,false",
        "29,false",
        "33,false",
        "34,true",
        "35,true",
        "99,true"
    )
    fun isFirstDayOfWeekLocalePreferenceEnabled_shouldDependOnSystemSDK(
        sdkVersion: Int,
        expectedAvailability: Boolean
    ) {
        mockGetRuntimeSDK(sdkVersion)

        val result = isFirstDayOfWeekLocalePreferenceEnabled()

        assertThat(result).isEqualTo(expectedAvailability)
        verifyGetRuntimeSDK()
    }

    @Test
    fun clearAllConfiguration_shouldRemoveAllApplicationPreferences() {
        mockSharedPreferences()
        clearAllConfiguration(context)

        verifySharedPreferencesAccess()
        verifySharedPreferencesEdit()
        verify { editor.clear() }
        verify { editor.apply() }
    }
}