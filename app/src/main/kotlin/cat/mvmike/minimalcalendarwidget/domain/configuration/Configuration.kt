// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import android.content.Context
import android.os.Build
import androidx.core.content.edit
import cat.mvmike.minimalcalendarwidget.domain.Percentage
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Calendar
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getAvailableColors
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getCalendarDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getColourDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getDisplayValue
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getSymbolSetDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getThemeDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.getDayOfWeekDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.getDisplayValue
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import java.lang.Enum.valueOf
import java.time.DayOfWeek

const val PREFERENCE_KEY = "mincal_prefs"

sealed class ConfigurationItem<E>(
    internal open val key: String,
    internal open val defaultValue: E
) {
    abstract fun get(context: Context): E
}

sealed class BooleanConfigurationItem(
    override val key: String,
    override val defaultValue: Boolean = false
) : ConfigurationItem<Boolean>(
        key = key,
        defaultValue = defaultValue
    ) {
    override fun get(context: Context) =
        getConfiguration(context).getBoolean(key, defaultValue)

    data class CalendarVisibilitySelection(
        val calendarId: Int
    ) : BooleanConfigurationItem(
            key = "CALENDAR_VISIBILITY_SELECTION_$calendarId"
        )

    data object DefaultVisibleCalendars : BooleanConfigurationItem(
        key = "DEFAULT_VISIBLE_CALENDARS",
        defaultValue = true
    )

    data object ShowDeclinedEvents : BooleanConfigurationItem(
        key = "SHOW_DECLINED_EVENTS"
    )

    data object FocusOnCurrentWeek : BooleanConfigurationItem(
        key = "FOCUS_ON_CURRENT_WEEK"
    )

    data object OpenCalendarOnClickedDay : BooleanConfigurationItem(
        key = "OPEN_CALENDAR_ON_CLICKED_DAY"
    )

    data object ShowWeekNumber : BooleanConfigurationItem(
        key = "SHOW_WEEK_NUMBER"
    )
}

sealed class EnumConfigurationItem<E : Enum<E>>(
    override val key: String,
    override val defaultValue: E,
    private val enumClass: Class<E>
) : ConfigurationItem<E>(
        key = key,
        defaultValue = defaultValue
    ) {
    override fun get(context: Context): E = valueOf(
        enumClass,
        getConfiguration(context).getString(key, defaultValue.name)!!
    )

    fun getKeys() = getEnumConstants().map { it.name }.toTypedArray()

    fun getCurrentKey(context: Context) = get(context).name

    internal open fun getEnumConstants() = enumClass.enumConstants!!

    abstract fun getDisplayValues(context: Context): List<String>

    abstract fun getCurrentDisplayValue(context: Context): String

    data object WidgetTheme : EnumConfigurationItem<Theme>(
        key = "WIDGET_THEME",
        enumClass = Theme::class.java,
        defaultValue = Theme.DARK
    ) {
        override fun getDisplayValues(context: Context) = getThemeDisplayValues(context)

        override fun getCurrentDisplayValue(context: Context) = get(context).getDisplayValue(context)
    }

    data object WidgetCalendar : EnumConfigurationItem<Calendar>(
        key = "WIDGET_CALENDAR",
        enumClass = Calendar::class.java,
        defaultValue = Calendar.GREGORIAN
    ) {
        override fun getDisplayValues(context: Context) = getCalendarDisplayValues(context)

        override fun getCurrentDisplayValue(context: Context) = get(context).getDisplayValue(context)
    }

    data object FirstDayOfWeek : EnumConfigurationItem<DayOfWeek>(
        key = "FIRST_DAY_OF_WEEK",
        enumClass = DayOfWeek::class.java,
        defaultValue = DayOfWeek.MONDAY
    ) {
        override fun getDisplayValues(context: Context): List<String> = getDayOfWeekDisplayValues(context)

        override fun getCurrentDisplayValue(context: Context) = get(context).getDisplayValue(context)
    }

    data object InstancesSymbolSet : EnumConfigurationItem<SymbolSet>(
        key = "INSTANCES_SYMBOL_SET",
        enumClass = SymbolSet::class.java,
        defaultValue = SymbolSet.MINIMAL
    ) {
        override fun getDisplayValues(context: Context) = getSymbolSetDisplayValues()

        override fun getCurrentDisplayValue(context: Context) = get(context).getDisplayValue()
    }

    data object InstancesColour : EnumConfigurationItem<Colour>(
        key = "INSTANCES_COLOUR",
        enumClass = Colour::class.java,
        defaultValue = getAvailableColors().first()
    ) {
        override fun getEnumConstants() = getAvailableColors().toTypedArray()

        override fun getDisplayValues(context: Context) = getColourDisplayValues(context)

        override fun getCurrentDisplayValue(context: Context) = get(context).getDisplayValue(context)
    }
}

sealed class PercentageConfigurationItem<E : Percentage>(
    override val key: String,
    override val defaultValue: E
) : ConfigurationItem<Percentage>(
        key = key,
        defaultValue = defaultValue
    ) {
    override fun get(context: Context) = Percentage(
        getConfiguration(context).getInt(key, defaultValue.value)
    )

    data object WidgetTransparency : PercentageConfigurationItem<Transparency>(
        key = "WIDGET_TRANSPARENCY",
        defaultValue = Transparency(20)
    ) {
        override fun get(context: Context) = Transparency(
            getConfiguration(context).getInt(key, defaultValue.percentage)
        )
    }

    data object WidgetTextSize : PercentageConfigurationItem<TextSize>(
        key = "WIDGET_TEXT_SIZE",
        defaultValue = TextSize(40)
    ) {
        override fun get(context: Context) = TextSize(
            getConfiguration(context).getInt(key, defaultValue.percentage)
        )
    }
}

fun isPerAppLanguagePreferenceEnabled() =
    SystemResolver.getRuntimeSDK() >= Build.VERSION_CODES.TIRAMISU

fun isFirstDayOfWeekLocalePreferenceEnabled() =
    SystemResolver.getRuntimeSDK() >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

fun clearAllConfiguration(context: Context) = getConfiguration(context).edit { clear() }

private fun getConfiguration(context: Context) = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)