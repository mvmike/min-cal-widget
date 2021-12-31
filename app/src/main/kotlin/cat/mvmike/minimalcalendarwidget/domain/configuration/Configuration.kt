// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import android.content.Context
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getColourDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getSymbolSetDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getThemeDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.entry.getDayOfWeekDisplayValues
import java.lang.Enum.valueOf
import java.time.DayOfWeek

sealed class Configuration<E>(
    internal open val key: String,
    internal open val resource: Int,
    internal open val defaultValue: E
) {
    abstract fun get(context: Context): E

    abstract fun set(context: Context, value: E)

    object WidgetTransparency : Configuration<Transparency>(
        key = "WIDGET_TRANSPARENCY",
        resource = R.id.transparencySeekBar,
        defaultValue = Transparency(20)
    ) {
        override fun get(context: Context) = Transparency(
            getConfiguration(context).getInt(key, defaultValue.percentage)
        )

        override fun set(context: Context, value: Transparency) =
            getConfiguration(context).edit().putInt(key, value.percentage).apply()
    }

    object WidgetShowDeclinedEvents : Configuration<Boolean>(
        key = "WIDGET_SHOW_DECLINED_EVENTS",
        resource = R.id.show_declined_eventsCheckBox,
        defaultValue = false
    ) {
        override fun get(context: Context) =
            getConfiguration(context).getBoolean(key, defaultValue)

        override fun set(context: Context, value: Boolean) =
            getConfiguration(context).edit().putBoolean(key, value).apply()
    }
}

sealed class EnumConfiguration<E : Enum<E>>(
    override val key: String,
    override val resource: Int,
    override val defaultValue: E,
    private val enumClass: Class<E>
): Configuration<E>(
    key = key,
    resource = resource,
    defaultValue = defaultValue
) {
    override fun get(context: Context): E = valueOf(
        enumClass,
        getConfiguration(context).getString(key, defaultValue.name)!!
    )

    override fun set(context: Context, value: E) =
        getConfiguration(context).edit().putString(key, value.name).apply()

    abstract fun getDisplayValues(context: Context): Array<String>

    fun set(context: Context, ordinal: Int) = set(context, enumClass.enumConstants!![ordinal])

    object FirstDayOfWeek : EnumConfiguration<DayOfWeek>(
        key = "FIRST_DAY_OF_WEEK",
        resource = R.id.startWeekDaySpinner,
        enumClass = DayOfWeek::class.java,
        defaultValue = DayOfWeek.MONDAY
    ) {
        override fun getDisplayValues(context: Context) = getDayOfWeekDisplayValues(context)
    }

    object WidgetTheme : EnumConfiguration<Theme>(
        key = "WIDGET_THEME",
        resource = R.id.themeSpinner,
        enumClass = Theme::class.java,
        defaultValue = Theme.DARK
    ) {
        override fun getDisplayValues(context: Context) = getThemeDisplayValues(context)
    }

    object InstancesSymbolSet : EnumConfiguration<SymbolSet>(
        key = "INSTANCES_SYMBOL_SET",
        resource = R.id.symbolSetSpinner,
        enumClass = SymbolSet::class.java,
        defaultValue = SymbolSet.MINIMAL
    ) {
        override fun getDisplayValues(context: Context) = getSymbolSetDisplayValues(context)
    }

    object InstancesColour : EnumConfiguration<Colour>(
        key = "INSTANCES_COLOUR",
        resource = R.id.symbolColourSpinner,
        enumClass = Colour::class.java,
        defaultValue = Colour.CYAN
    ) {
        override fun getDisplayValues(context: Context) = getColourDisplayValues(context)
    }
}

fun clearAllConfiguration(context: Context) = getConfiguration(context).edit().clear().apply()

private fun getConfiguration(context: Context) = context.getSharedPreferences("mincal_prefs", Context.MODE_PRIVATE)
