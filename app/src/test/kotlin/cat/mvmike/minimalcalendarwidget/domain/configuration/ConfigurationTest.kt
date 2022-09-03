// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import io.mockk.Called
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.DayOfWeek

internal class ConfigurationTest : BaseTest() {

    private val appWidgetId = 39587345

    @ParameterizedTest
    @ValueSource(ints = [-10, 1, 5, 100, 180, 351])
    fun getWidgetFormat_shouldReturnSharedPreferencesValue(width: Int) {
        val format = Format(width)
        mockSharedPreferences()
        mockWidgetFormat(format, appWidgetId)

        val result = Configuration.WidgetFormat.get(context, appWidgetId)

        assertThat(result).isEqualTo(format)
        verifyWidgetFormat(appWidgetId)
        verify { editor wasNot Called }
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 5, 100, 180, 351])
    fun setWidgetFormat_shouldSetSharedPreferencesValue(width: Int) {
        val format = Format(width)
        mockSharedPreferences()

        Configuration.WidgetFormat.set(context, format, appWidgetId)

        verifySharedPreferencesAccess()
        verifySharedPreferencesEdit()
        verify { editor.putInt("${Configuration.WidgetFormat.key}_$appWidgetId", format.width) }
        verify { editor.apply() }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 5, 17, 50, 51, 72, 80, 99, 100])
    fun getWidgetTransparency_shouldReturnSharedPreferencesValue(percentage: Int) {
        val transparency = Transparency(percentage)
        mockSharedPreferences()
        mockWidgetTransparency(transparency)

        val result = Configuration.WidgetTransparency.get(context)

        assertThat(result).isEqualTo(transparency)
        verifyWidgetTransparency()
        verify { editor wasNot Called }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun getWidgetShowDeclinedEvents_shouldReturnSharedPreferencesValue(showDeclinedEvents: Boolean) {
        mockSharedPreferences()
        mockWidgetShowDeclinedEvents(showDeclinedEvents)

        val result = BooleanConfiguration.WidgetShowDeclinedEvents.get(context)

        assertThat(result).isEqualTo(showDeclinedEvents)
        verifyWidgetShowDeclinedEvents()
        verify { editor wasNot Called }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun getWidgetFocusOnCurrentWeek_shouldReturnSharedPreferencesValue(focusOnCurrentWeek: Boolean) {
        mockSharedPreferences()
        mockWidgetFocusOnCurrentWeek(focusOnCurrentWeek)

        val result = BooleanConfiguration.WidgetFocusOnCurrentWeek.get(context)

        assertThat(result).isEqualTo(focusOnCurrentWeek)
        verifyWidgetFocusOnCurrentWeek()
        verify { editor wasNot Called }
    }

    @ParameterizedTest
    @EnumSource(value = Theme::class)
    fun getCalendarTheme_shouldReturnSharedPreferencesValue(theme: Theme) {
        mockSharedPreferences()
        mockWidgetTheme(theme)

        val result = EnumConfiguration.WidgetTheme.get(context)

        assertThat(result).isEqualTo(theme)
        verifyWidgetTheme()
        verify { editor wasNot Called }
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek::class)
    fun getFirstDayOfWeek_shouldReturnSharedPreferencesValue(dayOfWeek: DayOfWeek) {
        mockSharedPreferences()
        mockFirstDayOfWeek(dayOfWeek)

        val result = EnumConfiguration.FirstDayOfWeek.get(context)

        assertThat(result).isEqualTo(dayOfWeek)
        verifyFirstDayOfWeek()
        verify { editor wasNot Called }
    }

    @ParameterizedTest
    @EnumSource(value = SymbolSet::class)
    fun getInstancesSymbolSet_shouldReturnSharedPreferencesValue(symbolSet: SymbolSet) {
        mockSharedPreferences()
        mockInstancesSymbolSet(symbolSet)

        val result = EnumConfiguration.InstancesSymbolSet.get(context)

        assertThat(result).isEqualTo(symbolSet)
        verifyInstancesSymbolSet()
        verify { editor wasNot Called }
    }

    @ParameterizedTest
    @EnumSource(value = Colour::class)
    fun getInstancesColour_shouldReturnSharedPreferencesValue(colour: Colour) {
        mockSharedPreferences()
        mockInstancesColour(colour)

        val result = EnumConfiguration.InstancesColour.get(context)

        assertThat(result).isEqualTo(colour)
        verifyInstancesColour()
        verify { editor wasNot Called }
    }

    @Test
    fun getInstancesColour_shouldReturnOnlyAvailableValues() {
        val allowedColours = EnumConfiguration.InstancesColour.getEnumConstants().toSet()

        allowedColours.forEach {
            assertThat(it.isAvailable())
        }
        verify { SystemResolver.getRuntimeSDK() }
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
