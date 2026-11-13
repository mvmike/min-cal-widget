// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import android.content.Context
import android.content.SharedPreferences
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Calendar
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextColour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getSymbolSetDisplayValues
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
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
        fun getShowWeekNumber_shouldReturnSharedPreferencesValue(showWeekNumber: Boolean) {
            mockShowWeekNumber(showWeekNumber)

            val result = BooleanConfigurationItem.ShowWeekNumber.get(context, appWidgetId)

            assertThat(result).isEqualTo(showWeekNumber)
            verifyShowWeekNumber()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun getAllowPagination_shouldReturnSharedPreferencesValue(allowPagination: Boolean) {
            mockAllowPagination(allowPagination)

            val result = BooleanConfigurationItem.AllowPagination.get(context, appWidgetId)

            assertThat(result).isEqualTo(allowPagination)
            verifyAllowPagination()
            verify { editor wasNot Called }
        }
    }

    @Nested
    inner class IntConfigurationItems {

        @ParameterizedTest
        @ValueSource(ints = [-10, -1, 0, 1, 5, 42])
        fun getPage_shouldReturnSharedPreferencesValue(page: Int) {
            mockPage(page)

            val result = IntConfigurationItem.Page.get(context, appWidgetId)

            assertThat(result).isEqualTo(page)
            verifyPage()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @CsvSource(
            "-5,-6",
            "-1,-2",
            "0,-1",
            "1,0",
            "5,4"
        )
        fun decrementPage_shouldSubtractOneFromCurrentPage(currentPage: Int, expectedPage: Int) {
            mockPage(currentPage)

            IntConfigurationItem.Page.decrement(context, appWidgetId)

            verifyPage()
            verifySetPage(expectedPage)
        }

        @ParameterizedTest
        @CsvSource(
            "-5,-4",
            "-1,0",
            "0,1",
            "1,2",
            "5,6"
        )
        fun incrementPage_shouldAddOneToCurrentPage(currentPage: Int, expectedPage: Int) {
            mockPage(currentPage)

            IntConfigurationItem.Page.increment(context, appWidgetId)

            verifyPage()
            verifySetPage(expectedPage)
        }

        @Test
        fun resetPage_shouldSetPageToZero() {
            mockSharedPreferences()

            IntConfigurationItem.Page.reset(context, appWidgetId)

            verifySetPage(0)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
        @MethodSource("getEnumConfigurationItemsWithExpectedKeys")
        fun getKeys_shouldReturnEveryEnumConstantName(
            enumConfigurationItem: EnumConfigurationItem<*>,
            expectedKeys: List<String>
        ) {
            val result = enumConfigurationItem.getKeys()

            assertThat(result).containsExactlyElementsOf(expectedKeys)
        }

        @ParameterizedTest
        @MethodSource("getEnumConfigurationItemsWithValue")
        fun getCurrentKey_shouldReturnSharedPreferencesValueName(
            enumConfigurationItem: EnumConfigurationItem<*>,
            value: Enum<*>
        ) {
            mockSharedPreferences()
            every { sharedPreferences.getString(enumConfigurationItem.key, enumConfigurationItem.defaultValue.name) } returns value.name

            val result = enumConfigurationItem.getCurrentKey(context, appWidgetId)

            assertThat(result).isEqualTo(value.name)
            verifySharedPreferencesAccess()
            verify { sharedPreferences.getString(enumConfigurationItem.key, enumConfigurationItem.defaultValue.name) }
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @MethodSource("getEnumConfigurationItemsWithValueAndTranslation")
        fun getCurrentDisplayValue_shouldReturnCapitalisedTranslationOfSharedPreferencesValue(
            enumConfigurationItem: EnumConfigurationItem<*>,
            value: Enum<*>,
            stringResourceId: Int,
            translation: String,
            expectedDisplayValue: String
        ) {
            mockSharedPreferences()
            every { sharedPreferences.getString(enumConfigurationItem.key, enumConfigurationItem.defaultValue.name) } returns value.name
            every { context.getString(stringResourceId) } returns translation

            val result = enumConfigurationItem.getCurrentDisplayValue(context, appWidgetId)

            assertThat(result).isEqualTo(expectedDisplayValue)
            verifySharedPreferencesAccess()
            verify { sharedPreferences.getString(enumConfigurationItem.key, enumConfigurationItem.defaultValue.name) }
            verify { context.getString(stringResourceId) }
            verify { editor wasNot Called }
        }

        @Test
        fun getCurrentDisplayValue_shouldReturnSymbolsOfSharedPreferencesValue_whenInstancesSymbolSet() {
            mockInstancesSymbolSet(SymbolSet.ROMAN)

            val result = EnumConfigurationItem.InstancesSymbolSet.getCurrentDisplayValue(context, appWidgetId)

            assertThat(result).isEqualTo(SymbolSet.ROMAN.getDisplayValue())
            verifyInstancesSymbolSet()
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @MethodSource("getEnumConfigurationItemsWithTranslationsAndExpectedDisplayValues")
        fun getDisplayValues_shouldReturnCapitalisedTranslationOfEveryEnumConstant(
            enumConfigurationItem: EnumConfigurationItem<*>,
            translations: Map<Int, String>,
            expectedDisplayValues: List<String>
        ) {
            translations.forEach { (stringResourceId, translation) ->
                every { context.getString(stringResourceId) } returns translation
            }

            val result = enumConfigurationItem.getDisplayValues(context)

            assertThat(result).containsExactlyElementsOf(expectedDisplayValues)
            translations.keys.forEach { stringResourceId ->
                verify { context.getString(stringResourceId) }
            }
        }

        @Test
        fun getDisplayValues_shouldReturnSymbolsOfEverySymbolSet_whenInstancesSymbolSet() {
            val result = EnumConfigurationItem.InstancesSymbolSet.getDisplayValues(context)

            assertThat(result).isEqualTo(getSymbolSetDisplayValues())
        }

        private fun getEnumConfigurationItemsWithExpectedKeys() = listOf(
            of(EnumConfigurationItem.WidgetTheme, listOf("DARK", "LIGHT")),
            of(EnumConfigurationItem.WidgetCalendar, listOf("GREGORIAN", "HOLOCENE")),
            of(EnumConfigurationItem.FirstDayOfWeek, listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")),
            of(EnumConfigurationItem.InstancesSymbolSet, listOf("MINIMAL", "VERTICAL", "CIRCLES", "BLOCKS", "NUMBERS", "ROMAN", "BINARY", "NONE"))
        )

        private fun getEnumConfigurationItemsWithValue() = listOf(
            of(EnumConfigurationItem.WidgetTheme, Theme.LIGHT),
            of(EnumConfigurationItem.WidgetCalendar, Calendar.HOLOCENE),
            of(EnumConfigurationItem.FirstDayOfWeek, DayOfWeek.SUNDAY),
            of(EnumConfigurationItem.InstancesSymbolSet, SymbolSet.ROMAN)
        )

        private fun getEnumConfigurationItemsWithValueAndTranslation() = listOf(
            of(EnumConfigurationItem.WidgetTheme, Theme.LIGHT, R.string.light, "light", "Light"),
            of(EnumConfigurationItem.WidgetCalendar, Calendar.HOLOCENE, R.string.holocene, "holocene", "Holocene"),
            of(EnumConfigurationItem.FirstDayOfWeek, DayOfWeek.SUNDAY, R.string.sunday, "sunday", "Sunday")
        )

        private fun getEnumConfigurationItemsWithTranslationsAndExpectedDisplayValues() = listOf(
            of(
                EnumConfigurationItem.WidgetTheme,
                mapOf(R.string.dark to "dark", R.string.light to "light"),
                listOf("Dark", "Light")
            ),
            of(
                EnumConfigurationItem.WidgetCalendar,
                mapOf(R.string.gregorian to "gregorian", R.string.holocene to "holocene"),
                listOf("Gregorian", "Holocene")
            ),
            of(
                EnumConfigurationItem.FirstDayOfWeek,
                mapOf(
                    R.string.monday to "monday",
                    R.string.tuesday to "tuesday",
                    R.string.wednesday to "wednesday",
                    R.string.thursday to "thursday",
                    R.string.friday to "friday",
                    R.string.saturday to "saturday",
                    R.string.sunday to "sunday"
                ),
                listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
            )
        )
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ColourConfigurationItems {

        @ParameterizedTest
        @MethodSource("getInstancesColours")
        fun getInstancesColour_shouldReturnSharedPreferencesValue(colour: TextColour) {
            if (colour is TextColour.SystemAccent) {
                mockGetRuntimeSDK(31)
            }
            mockInstancesColour(colour)

            val result = ColourConfigurationItem.InstancesColour.get(context, appWidgetId)

            assertThat(result).isEqualTo(colour)
            verifyInstancesColour()
            if (colour is TextColour.SystemAccent) {
                verifyGetRuntimeSDK()
            }
            verify { editor wasNot Called }
        }

        @ParameterizedTest
        @MethodSource("getInstancesColours")
        fun setInstancesColour_shouldPersistStoredValue(colour: TextColour) {
            mockSharedPreferences()
            every { editor.putString(ColourConfigurationItem.InstancesColour.key, colour.getStoredValue()) } returns editor

            ColourConfigurationItem.InstancesColour.set(context, appWidgetId, colour)

            verifySharedPreferencesAccess()
            verifySharedPreferencesEdit()
            verify { editor.putString(ColourConfigurationItem.InstancesColour.key, colour.getStoredValue()) }
            verify { editor.apply() }
        }

        private fun getInstancesColours() = listOf(
            TextColour.SystemAccent,
            TextColour.CustomHex("#FF123456")
        )
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
    fun preferenceKey_shouldMatchPersistedSharedPreferencesName() {
        assertThat(PREFERENCE_KEY).isEqualTo("mincal_prefs")
    }

    @ParameterizedTest
    @MethodSource("getConfigurationItemsWithPersistedKey")
    fun key_shouldMatchPersistedSharedPreferencesKey(
        configurationItem: ConfigurationItem<*>,
        expectedKey: String
    ) {
        assertThat(configurationItem.key).isEqualTo(expectedKey)
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
        every { context.deleteSharedPreferences(getConfigurationName(appWidgetId)) } returns true

        deleteConfiguration(context, appWidgetId)

        verify { context.deleteSharedPreferences(getConfigurationName(appWidgetId)) }
    }

    @Test
    fun moveConfiguration_shouldCopyEachOldWidgetPreferencesToItsNewWidgetAndDeleteOldOnes() {
        val oldSharedPreferences = mockk<SharedPreferences>()
        val overlappingSharedPreferences = mockk<SharedPreferences>()
        val newSharedPreferences = mockk<SharedPreferences>()
        val overlappingEditor = mockk<SharedPreferences.Editor>()
        val newEditor = mockk<SharedPreferences.Editor>()
        every { context.getSharedPreferences(getConfigurationName(4), Context.MODE_PRIVATE) } returns oldSharedPreferences
        every { context.getSharedPreferences(getConfigurationName(9), Context.MODE_PRIVATE) } returns overlappingSharedPreferences
        every { context.getSharedPreferences(getConfigurationName(15), Context.MODE_PRIVATE) } returns newSharedPreferences
        every { oldSharedPreferences.all } returns mapOf(EnumConfigurationItem.WidgetTheme.key to Theme.LIGHT.name)
        every { overlappingSharedPreferences.all } returns mapOf(PercentageConfigurationItem.WidgetTextSize.key to 60)
        every { overlappingSharedPreferences.edit() } returns overlappingEditor
        every { newSharedPreferences.edit() } returns newEditor
        every { overlappingEditor.putString(any(), any()) } returns overlappingEditor
        every { overlappingEditor.commit() } returns true
        every { newEditor.putInt(any(), any()) } returns newEditor
        every { newEditor.commit() } returns true
        every { context.deleteSharedPreferences(any()) } returns true

        moveConfiguration(context, intArrayOf(4, 9), intArrayOf(9, 15))

        verify { context.getSharedPreferences(getConfigurationName(4), Context.MODE_PRIVATE) }
        verify(exactly = 2) { context.getSharedPreferences(getConfigurationName(9), Context.MODE_PRIVATE) }
        verify { context.getSharedPreferences(getConfigurationName(15), Context.MODE_PRIVATE) }
        verify { oldSharedPreferences.all }
        verify { overlappingSharedPreferences.all }
        verify { context.deleteSharedPreferences(getConfigurationName(4)) }
        verify { context.deleteSharedPreferences(getConfigurationName(9)) }
        verify { overlappingSharedPreferences.edit() }
        verify { overlappingEditor.putString(EnumConfigurationItem.WidgetTheme.key, Theme.LIGHT.name) }
        verify { overlappingEditor.commit() }
        verify { newSharedPreferences.edit() }
        verify { newEditor.putInt(PercentageConfigurationItem.WidgetTextSize.key, 60) }
        verify { newEditor.commit() }
        confirmVerified(oldSharedPreferences, overlappingSharedPreferences, newSharedPreferences, overlappingEditor, newEditor)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun hasLegacyConfiguration_shouldDependOnLegacyPreferencesContent(hasLegacyConfiguration: Boolean) {
        val legacySharedPreferences = mockk<SharedPreferences>()
        every { context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE) } returns legacySharedPreferences
        every { legacySharedPreferences.all } returns when (hasLegacyConfiguration) {
            true -> mapOf(EnumConfigurationItem.WidgetTheme.key to Theme.LIGHT.name)
            else -> emptyMap()
        }

        val result = hasLegacyConfiguration(context)

        assertThat(result).isEqualTo(hasLegacyConfiguration)
        verify { context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE) }
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
        every { context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE) } returns legacySharedPreferences
        every { legacySharedPreferences.all } returns mapOf(
            EnumConfigurationItem.WidgetTheme.key to Theme.LIGHT.name,
            PercentageConfigurationItem.WidgetTextSize.key to 60,
            BooleanConfigurationItem.ShowWeekNumber.key to true
        )
        every { legacySharedPreferences.edit() } returns legacyEditor
        every { legacyEditor.clear() } returns legacyEditor
        every { legacyEditor.commit() } returns true
        appWidgetIds.forEach { mockSharedPreferences(it) }

        migrateLegacyConfiguration(context, appWidgetIds)

        verify { context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE) }
        verify { legacySharedPreferences.all }
        appWidgetIds.forEach { verifySharedPreferencesAccess(it) }
        verify(exactly = 2) { sharedPreferences.edit() }
        verify(exactly = 2) { editor.putString(EnumConfigurationItem.WidgetTheme.key, Theme.LIGHT.name) }
        verify(exactly = 2) { editor.putInt(PercentageConfigurationItem.WidgetTextSize.key, 60) }
        verify(exactly = 2) { editor.putBoolean(BooleanConfigurationItem.ShowWeekNumber.key, true) }
        verify(exactly = 2) { editor.commit() }
        verify { legacySharedPreferences.edit() }
        verify { legacyEditor.clear() }
        verify { legacyEditor.commit() }
        confirmVerified(legacySharedPreferences, legacyEditor)
    }

    private fun getConfigurationItemsWithPersistedKey() = listOf(
        of(BooleanConfigurationItem.CalendarVisibilitySelection(7), "CALENDAR_VISIBILITY_SELECTION_7"),
        of(BooleanConfigurationItem.DefaultVisibleCalendars, "DEFAULT_VISIBLE_CALENDARS"),
        of(BooleanConfigurationItem.ShowDeclinedEvents, "SHOW_DECLINED_EVENTS"),
        of(BooleanConfigurationItem.FocusOnCurrentWeek, "FOCUS_ON_CURRENT_WEEK"),
        of(BooleanConfigurationItem.ShowWeekNumber, "SHOW_WEEK_NUMBER"),
        of(BooleanConfigurationItem.AllowPagination, "ALLOW_PAGINATION"),
        of(IntConfigurationItem.Page, "PAGE"),
        of(EnumConfigurationItem.WidgetTheme, "WIDGET_THEME"),
        of(EnumConfigurationItem.WidgetCalendar, "WIDGET_CALENDAR"),
        of(EnumConfigurationItem.FirstDayOfWeek, "FIRST_DAY_OF_WEEK"),
        of(EnumConfigurationItem.InstancesSymbolSet, "INSTANCES_SYMBOL_SET"),
        of(ColourConfigurationItem.InstancesColour, "INSTANCES_COLOUR"),
        of(PercentageConfigurationItem.WidgetTransparency, "WIDGET_TRANSPARENCY"),
        of(PercentageConfigurationItem.WidgetTextSize, "WIDGET_TEXT_SIZE")
    )
}