// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import java.time.DayOfWeek
import io.mockk.Called
import io.mockk.verify
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

internal class ConfigurationTest : BaseTest() {

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

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 5, 17, 50, 51, 72, 80, 99, 100])
    fun setWidgetTransparency_shouldPutPercentageInteger(percentage: Int) {
        val transparency = Transparency(percentage)
        mockSharedPreferences()

        Configuration.WidgetTransparency.set(context, transparency)

        verifySharedPreferencesAccess()
        verifySharedPreferencesEdit()
        verify { editor.putInt(Configuration.WidgetTransparency.key, transparency.percentage) }
        verify { editor.apply() }
    }

    @ParameterizedTest
    @MethodSource("getCombinationOfEnumConfigurationItemsWithValuesAndKey")
    fun <E : Enum<E>> setEnumConfigurationItem_shouldPutEnumNameString(
        values: Array<E>,
        item: EnumConfiguration<E>,
        key: String
    ) {
        mockSharedPreferences()
        values.forEach { enumValue ->
            item.set(context, enumValue)
            verifySharedPreferencesAccess()
            verifySharedPreferencesEdit()
            verify { editor.putString(key, enumValue.name) }
            verify { editor.apply() }
        }
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

    @Suppress("unused")
    private fun getCombinationOfEnumConfigurationItemsWithValuesAndKey(): Stream<Arguments> = Stream.of(
        Arguments.of(
            Theme.values(),
            EnumConfiguration.WidgetTheme,
            "WIDGET_THEME"
        ),
        Arguments.of(
            DayOfWeek.values(),
            EnumConfiguration.FirstDayOfWeek,
            "FIRST_DAY_OF_WEEK"
        ),
        Arguments.of(
            SymbolSet.values(),
            EnumConfiguration.InstancesSymbolSet,
            "INSTANCES_SYMBOL_SET"
        ),
        Arguments.of(
            Colour.values(),
            EnumConfiguration.InstancesColour,
            "INSTANCES_COLOUR"
        )
    )
}
