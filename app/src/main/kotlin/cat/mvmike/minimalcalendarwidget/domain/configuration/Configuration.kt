// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import android.content.Context
import android.content.SharedPreferences
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getColourDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getSymbolSetDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getThemeDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.entry.getDayOfWeekDisplayValues
import java.time.DayOfWeek

sealed class Configuration<E : Enum<E>>(
    internal val key: String,
    internal val resource: Int,
    private val enumClass: Class<E>,
    internal val defaultValue: E
) {

    fun get(context: Context): E = java.lang.Enum.valueOf(
        enumClass,
        getConfiguration(context).getString(key, defaultValue.name)!!
    )

    fun get(ordinal: Int): E = enumClass.enumConstants!![ordinal]

    fun set(context: Context, value: Enum<*>) {
        getConfiguration(context).edit().putString(key, value.name).apply()
    }

    abstract fun getDisplayValues(context: Context): Array<String>

    object FirstDayOfWeek : Configuration<DayOfWeek>(
        key = "FIRST_DAY_OF_WEEK",
        resource = R.id.startWeekDaySpinner,
        enumClass = DayOfWeek::class.java,
        defaultValue = DayOfWeek.MONDAY
    ) {
        override fun getDisplayValues(context: Context) = getDayOfWeekDisplayValues(context)
    }

    object CalendarTheme : Configuration<Theme>(
        key = "CALENDAR_THEME",
        resource = R.id.themeSpinner,
        enumClass = Theme::class.java,
        defaultValue = Theme.BLACK
    ) {
        override fun getDisplayValues(context: Context) = getThemeDisplayValues(context)
    }

    object InstancesSymbolSet : Configuration<SymbolSet>(
        key = "INSTANCES_SYMBOL_SET",
        resource = R.id.symbolSetSpinner,
        enumClass = SymbolSet::class.java,
        defaultValue = SymbolSet.MINIMAL
    ) {
        override fun getDisplayValues(context: Context) = getSymbolSetDisplayValues(context)
    }

    object InstancesColour : Configuration<Colour>(
        key = "INSTANCES_COLOUR",
        resource = R.id.symbolColourSpinner,
        enumClass = Colour::class.java,
        defaultValue = Colour.CYAN
    ) {
        override fun getDisplayValues(context: Context) = getColourDisplayValues(context)
    }
}

fun clearAllConfiguration(context: Context) = getConfiguration(context).edit().clear().apply()

private fun getConfiguration(context: Context): SharedPreferences {
    return context.getSharedPreferences("mincal_prefs", Context.MODE_PRIVATE)
}
