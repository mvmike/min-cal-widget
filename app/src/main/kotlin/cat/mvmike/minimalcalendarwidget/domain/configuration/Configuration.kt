// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit
import cat.mvmike.minimalcalendarwidget.domain.Percentage
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Calendar
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getAvailableColours
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
    abstract fun get(context: Context, appWidgetId: Int): E
}

sealed class BooleanConfigurationItem(
    override val key: String,
    override val defaultValue: Boolean = false
) : ConfigurationItem<Boolean>(
        key = key,
        defaultValue = defaultValue
    ) {
    override fun get(context: Context, appWidgetId: Int) =
        getConfiguration(context, appWidgetId).getBoolean(key, defaultValue)

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
    override fun get(context: Context, appWidgetId: Int): E = valueOf(
        enumClass,
        getConfiguration(context, appWidgetId).getString(key, defaultValue.name)!!
    )

    fun getKeys() = getEnumConstants().map { it.name }.toTypedArray()

    fun getCurrentKey(context: Context, appWidgetId: Int) = get(context, appWidgetId).name

    internal open fun getEnumConstants() = enumClass.enumConstants!!

    abstract fun getDisplayValues(context: Context): List<String>

    abstract fun getCurrentDisplayValue(context: Context, appWidgetId: Int): String

    data object WidgetTheme : EnumConfigurationItem<Theme>(
        key = "WIDGET_THEME",
        enumClass = Theme::class.java,
        defaultValue = Theme.DARK
    ) {
        override fun getDisplayValues(context: Context) = getThemeDisplayValues(context)

        override fun getCurrentDisplayValue(context: Context, appWidgetId: Int) = get(context, appWidgetId).getDisplayValue(context)
    }

    data object WidgetCalendar : EnumConfigurationItem<Calendar>(
        key = "WIDGET_CALENDAR",
        enumClass = Calendar::class.java,
        defaultValue = Calendar.GREGORIAN
    ) {
        override fun getDisplayValues(context: Context) = getCalendarDisplayValues(context)

        override fun getCurrentDisplayValue(context: Context, appWidgetId: Int) = get(context, appWidgetId).getDisplayValue(context)
    }

    data object FirstDayOfWeek : EnumConfigurationItem<DayOfWeek>(
        key = "FIRST_DAY_OF_WEEK",
        enumClass = DayOfWeek::class.java,
        defaultValue = DayOfWeek.MONDAY
    ) {
        override fun getDisplayValues(context: Context): List<String> = getDayOfWeekDisplayValues(context)

        override fun getCurrentDisplayValue(context: Context, appWidgetId: Int) = get(context, appWidgetId).getDisplayValue(context)
    }

    data object InstancesSymbolSet : EnumConfigurationItem<SymbolSet>(
        key = "INSTANCES_SYMBOL_SET",
        enumClass = SymbolSet::class.java,
        defaultValue = SymbolSet.MINIMAL
    ) {
        override fun getDisplayValues(context: Context) = getSymbolSetDisplayValues()

        override fun getCurrentDisplayValue(context: Context, appWidgetId: Int) = get(context, appWidgetId).getDisplayValue()
    }

    data object InstancesColour : EnumConfigurationItem<Colour>(
        key = "INSTANCES_COLOUR",
        enumClass = Colour::class.java,
        defaultValue = getAvailableColours().first()
    ) {
        override fun getEnumConstants() = getAvailableColours().toTypedArray()

        override fun getDisplayValues(context: Context) = getColourDisplayValues(context)

        override fun getCurrentDisplayValue(context: Context, appWidgetId: Int) = get(context, appWidgetId).getDisplayValue(context)
    }
}

sealed class PercentageConfigurationItem<E : Percentage>(
    override val key: String,
    override val defaultValue: E
) : ConfigurationItem<E>(
        key = key,
        defaultValue = defaultValue
    ) {

    data object WidgetTransparency : PercentageConfigurationItem<Transparency>(
        key = "WIDGET_TRANSPARENCY",
        defaultValue = Transparency(20)
    ) {
        override fun get(context: Context, appWidgetId: Int) = Transparency(
            getConfiguration(context, appWidgetId).getInt(key, defaultValue.percentage)
        )
    }

    data object WidgetTextSize : PercentageConfigurationItem<TextSize>(
        key = "WIDGET_TEXT_SIZE",
        defaultValue = TextSize(40)
    ) {
        override fun get(context: Context, appWidgetId: Int) = TextSize(
            getConfiguration(context, appWidgetId).getInt(key, defaultValue.percentage)
        )
    }
}

fun isPerAppLanguagePreferenceEnabled() =
    SystemResolver.getRuntimeSDK() >= Build.VERSION_CODES.TIRAMISU

fun isFirstDayOfWeekLocalePreferenceEnabled() =
    SystemResolver.getRuntimeSDK() >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

fun getConfigurationName(appWidgetId: Int) = "${PREFERENCE_KEY}_$appWidgetId"

fun clearAllConfiguration(context: Context, appWidgetId: Int) = getConfiguration(context, appWidgetId).edit { clear() }

fun deleteConfiguration(context: Context, appWidgetId: Int) =
    context.deleteSharedPreferences(getConfigurationName(appWidgetId))

fun moveConfiguration(context: Context, fromAppWidgetIds: IntArray, toAppWidgetIds: IntArray) {
    val configurations = fromAppWidgetIds.map { getConfiguration(context, it).all }
    fromAppWidgetIds.forEach { deleteConfiguration(context, it) }
    toAppWidgetIds.zip(configurations).forEach { (appWidgetId, configuration) ->
        getConfiguration(context, appWidgetId).putAll(configuration)
    }
}

fun hasLegacyConfiguration(context: Context) = getLegacyConfiguration(context).all.isNotEmpty()

fun migrateLegacyConfiguration(context: Context, appWidgetIds: List<Int>) {
    if (appWidgetIds.isEmpty()) {
        return
    }
    val legacyConfiguration = getLegacyConfiguration(context)
    val configuration = legacyConfiguration.all
    appWidgetIds.forEach { getConfiguration(context, it).putAll(configuration) }
    legacyConfiguration.edit(commit = true) { clear() }
}

private fun SharedPreferences.putAll(configuration: Map<String, *>) = edit(commit = true) {
    configuration.forEach { (key, value) ->
        when (value) {
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is String -> putString(key, value)
        }
    }
}

private fun getConfiguration(context: Context, appWidgetId: Int) =
    context.getSharedPreferences(getConfigurationName(appWidgetId), Context.MODE_PRIVATE)

private fun getLegacyConfiguration(context: Context) = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)