// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import android.content.Context
import android.content.SharedPreferences
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Calendar
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
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
        @CsvSource(
            "1,false",
            "5,true",
            "29,false",
            "32,true",
            "33,false",
            "35,false",
            "99,true"
        )
        fun getCalendarVisibilitySelection_shouldReturnSharedPreferencesValue(
            calendarId: Int,
            calendarVisibilitySelection: Boolean
        ) {
            mockCalendarVisibilitySelection(calendarId, calendarVisibilitySelection)

            val result = BooleanConfigurationItem.CalendarVisibilitySelection(calendarId).get(context, appWidgetId)

            assertThat(result).isEqualTo(calendarVisibilitySelection)
            verifyCalendarVisibilitySelection(calendarId)
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun getDefaultVisibleCalendars_shouldReturnSharedPreferencesValue(defaultVisibleCalendars: Boolean) {
            mockDefaultVisibleCalendars(defaultVisibleCalendars)

            val result = BooleanConfigurationItem.DefaultVisibleCalendars.get(context, appWidgetId)

            assertThat(result).isEqualTo(defaultVisibleCalendars)
            verifyDefaultVisibleCalendars()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun getShowDeclinedEvents_shouldReturnSharedPreferencesValue(showDeclinedEvents: Boolean) {
            mockShowDeclinedEvents(showDeclinedEvents)

            val result = BooleanConfigurationItem.ShowDeclinedEvents.get(context, appWidgetId)

            assertThat(result).isEqualTo(showDeclinedEvents)
            verifyShowDeclinedEvents()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun getFocusOnCurrentWeek_shouldReturnSharedPreferencesValue(focusOnCurrentWeek: Boolean) {
            mockFocusOnCurrentWeek(focusOnCurrentWeek)

            val result = BooleanConfigurationItem.FocusOnCurrentWeek.get(context, appWidgetId)

            assertThat(result).isEqualTo(focusOnCurrentWeek)
            verifyFocusOnCurrentWeek()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun getOpenCalendarOnClickedDay_shouldReturnSharedPreferencesValue(openCalendarOnClickedDay: Boolean) {
            mockOpenCalendarOnClickedDay(openCalendarOnClickedDay)

            val result = BooleanConfigurationItem.OpenCalendarOnClickedDay.get(context, appWidgetId)

            assertThat(result).isEqualTo(openCalendarOnClickedDay)
            verifyOpenCalendarOnClickedDay()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun getShowWeekNumber_shouldReturnSharedPreferencesValue(showWeekNumber: Boolean) {
            mockShowWeekNumber(showWeekNumber)

            val result = BooleanConfigurationItem.ShowWeekNumber.get(context, appWidgetId)

            assertThat(result).isEqualTo(showWeekNumber)
            verifyShowWeekNumber()
            verify { editor wasNot Called }
        }
    }

    @Nested
    inner class EnumConfigurationItems {

        @ParameterizedTest
        @EnumSource(value = Theme::class)
        fun getCalendarTheme_shouldReturnSharedPreferencesValue(widgetTheme: Theme) {
            mockWidgetTheme(widgetTheme)

            val result = EnumConfigurationItem.WidgetTheme.get(context, appWidgetId)

            assertThat(result).isEqualTo(widgetTheme)
            verifyWidgetTheme()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @EnumSource(value = Calendar::class)
        fun getCalendar_shouldReturnSharedPreferencesValue(widgetCalendar: Calendar) {
            mockWidgetCalendar(widgetCalendar)

            val result = EnumConfigurationItem.WidgetCalendar.get(context, appWidgetId)

            assertThat(result).isEqualTo(widgetCalendar)
            verifyWidgetCalendar()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @EnumSource(value = DayOfWeek::class)
        fun getFirstDayOfWeek_shouldReturnSharedPreferencesValue(dayOfWeek: DayOfWeek) {
            mockFirstDayOfWeek(dayOfWeek)

            val result = EnumConfigurationItem.FirstDayOfWeek.get(context, appWidgetId)

            assertThat(result).isEqualTo(dayOfWeek)
            verifyFirstDayOfWeek()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @EnumSource(value = SymbolSet::class)
        fun getInstancesSymbolSet_shouldReturnSharedPreferencesValue(symbolSet: SymbolSet) {
            mockInstancesSymbolSet(symbolSet)

            val result = EnumConfigurationItem.InstancesSymbolSet.get(context, appWidgetId)

            assertThat(result).isEqualTo(symbolSet)
            verifyInstancesSymbolSet()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @EnumSource(value = Colour::class)
        fun getInstancesColour_shouldReturnSharedPreferencesValue(colour: Colour) {
            mockInstancesColour(colour)

            val result = EnumConfigurationItem.InstancesColour.get(context, appWidgetId)

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

            val result = PercentageConfigurationItem.WidgetTransparency.get(context, appWidgetId)

            assertThat(result).isEqualTo(transparency)
            verifyWidgetTransparency()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @ValueSource(ints = [0, 1, 5, 17, 50, 51, 72, 80, 99, 100])
        fun getWidgetTextSize_shouldReturnSharedPreferencesValue(percentage: Int) {
            val textSize = TextSize(percentage)
            mockWidgetTextSize(textSize)

            val result = PercentageConfigurationItem.WidgetTextSize.get(context, appWidgetId)

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

    @ParameterizedTest
    @ValueSource(ints = [1, 7, 2611, 99999])
    fun getConfigurationName_shouldBeSuffixedWithAppWidgetId(appWidgetId: Int) {
        assertThat(getConfigurationName(appWidgetId)).isEqualTo("mincal_prefs_$appWidgetId")
    }

    @Test
    fun clearAllConfiguration_shouldRemoveAllWidgetPreferences() {
        mockSharedPreferences()

        clearAllConfiguration(context, appWidgetId)

        verifySharedPreferencesAccess()
        verifySharedPreferencesEdit()
        verify { editor.clear() }
        verify { editor.apply() }
    }

    @Test
    fun deleteConfiguration_shouldDeleteWidgetPreferences() {
        every { context.deleteSharedPreferences("mincal_prefs_$appWidgetId") } returns true

        deleteConfiguration(context, appWidgetId)

        verify { context.deleteSharedPreferences("mincal_prefs_$appWidgetId") }
    }

    @Test
    fun moveConfiguration_shouldCopyEachOldWidgetPreferencesToItsNewWidgetAndDeleteOldOnes() {
        val oldSharedPreferences = mockk<SharedPreferences>()
        val overlappingSharedPreferences = mockk<SharedPreferences>()
        val newSharedPreferences = mockk<SharedPreferences>()
        val overlappingEditor = mockk<SharedPreferences.Editor>()
        val newEditor = mockk<SharedPreferences.Editor>()
        every { context.getSharedPreferences("mincal_prefs_4", Context.MODE_PRIVATE) } returns oldSharedPreferences
        every { context.getSharedPreferences("mincal_prefs_9", Context.MODE_PRIVATE) } returns overlappingSharedPreferences
        every { context.getSharedPreferences("mincal_prefs_15", Context.MODE_PRIVATE) } returns newSharedPreferences
        every { oldSharedPreferences.all } returns mapOf("WIDGET_THEME" to "LIGHT")
        every { overlappingSharedPreferences.all } returns mapOf("WIDGET_TEXT_SIZE" to 60)
        every { overlappingSharedPreferences.edit() } returns overlappingEditor
        every { newSharedPreferences.edit() } returns newEditor
        every { overlappingEditor.putString(any(), any()) } returns overlappingEditor
        every { overlappingEditor.commit() } returns true
        every { newEditor.putInt(any(), any()) } returns newEditor
        every { newEditor.commit() } returns true
        every { context.deleteSharedPreferences(any()) } returns true

        moveConfiguration(context, intArrayOf(4, 9), intArrayOf(9, 15))

        verify { context.getSharedPreferences("mincal_prefs_4", Context.MODE_PRIVATE) }
        verify(exactly = 2) { context.getSharedPreferences("mincal_prefs_9", Context.MODE_PRIVATE) }
        verify { context.getSharedPreferences("mincal_prefs_15", Context.MODE_PRIVATE) }
        verify { oldSharedPreferences.all }
        verify { overlappingSharedPreferences.all }
        verify { context.deleteSharedPreferences("mincal_prefs_4") }
        verify { context.deleteSharedPreferences("mincal_prefs_9") }
        verify { overlappingSharedPreferences.edit() }
        verify { overlappingEditor.putString("WIDGET_THEME", "LIGHT") }
        verify { overlappingEditor.commit() }
        verify { newSharedPreferences.edit() }
        verify { newEditor.putInt("WIDGET_TEXT_SIZE", 60) }
        verify { newEditor.commit() }
        confirmVerified(oldSharedPreferences, overlappingSharedPreferences, newSharedPreferences, overlappingEditor, newEditor)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun hasLegacyConfiguration_shouldDependOnLegacyPreferencesContent(hasLegacyConfiguration: Boolean) {
        val legacySharedPreferences = mockk<SharedPreferences>()
        every { context.getSharedPreferences("mincal_prefs", Context.MODE_PRIVATE) } returns legacySharedPreferences
        every { legacySharedPreferences.all } returns when (hasLegacyConfiguration) {
            true -> mapOf("WIDGET_THEME" to "LIGHT")
            else -> emptyMap()
        }

        val result = hasLegacyConfiguration(context)

        assertThat(result).isEqualTo(hasLegacyConfiguration)
        verify { context.getSharedPreferences("mincal_prefs", Context.MODE_PRIVATE) }
        verify { legacySharedPreferences.all }
        confirmVerified(legacySharedPreferences)
    }

    @Test
    fun migrateLegacyConfiguration_shouldKeepLegacyPreferencesWhenThereAreNoWidgets() {
        migrateLegacyConfiguration(context, emptyList())
    }

    @Test
    fun migrateLegacyConfiguration_shouldCopyLegacyPreferencesToEveryWidgetAndClearThem() {
        val appWidgetIds = listOf(3, 8)
        val legacySharedPreferences = mockk<SharedPreferences>()
        val legacyEditor = mockk<SharedPreferences.Editor>()
        every { context.getSharedPreferences("mincal_prefs", Context.MODE_PRIVATE) } returns legacySharedPreferences
        every { legacySharedPreferences.all } returns mapOf(
            "WIDGET_THEME" to "LIGHT",
            "WIDGET_TEXT_SIZE" to 60,
            "SHOW_WEEK_NUMBER" to true
        )
        every { legacySharedPreferences.edit() } returns legacyEditor
        every { legacyEditor.clear() } returns legacyEditor
        every { legacyEditor.commit() } returns true
        appWidgetIds.forEach { mockSharedPreferences(it) }

        migrateLegacyConfiguration(context, appWidgetIds)

        verify { context.getSharedPreferences("mincal_prefs", Context.MODE_PRIVATE) }
        verify { legacySharedPreferences.all }
        appWidgetIds.forEach { verifySharedPreferencesAccess(it) }
        verify(exactly = 2) { sharedPreferences.edit() }
        verify(exactly = 2) { editor.putString("WIDGET_THEME", "LIGHT") }
        verify(exactly = 2) { editor.putInt("WIDGET_TEXT_SIZE", 60) }
        verify(exactly = 2) { editor.putBoolean("SHOW_WEEK_NUMBER", true) }
        verify(exactly = 2) { editor.commit() }
        verify { legacySharedPreferences.edit() }
        verify { legacyEditor.clear() }
        verify { legacyEditor.commit() }
        confirmVerified(legacySharedPreferences, legacyEditor)
    }
}