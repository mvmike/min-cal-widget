// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import android.content.Context
import cat.mvmike.minimalcalendarwidget.domain.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getAvailableColors
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getColourDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getDisplayValue
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getSymbolSetDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getThemeDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.getDayOfWeekDisplayValues
import cat.mvmike.minimalcalendarwidget.domain.getDisplayValue
import java.lang.Enum.valueOf
import java.lang.UnsupportedOperationException
import java.time.DayOfWeek

const val PREFERENCE_KEY = "mincal_prefs"

const val SOURCE_KEY = "SOURCE"

const val TRANSLATE_KEY = "TRANSLATE"

const val VERSION_KEY = "VERSION"

const val SOURCE_URL = "https://github.com/mvmike/min-cal-widget"

const val TRANSLATE_URL = "https://hosted.weblate.org/engage/min-cal-widget"

sealed class Configuration<E>(
    internal open val key: String,
    internal open val defaultValue: E
) {
    abstract fun get(context: Context): E

    object WidgetFormat : Configuration<Format>(
        key = "WIDGET_FORMAT",
        defaultValue = Format(220)
    ) {
        override fun get(context: Context) = throw UnsupportedOperationException("must include appWidgetId")

        fun get(context: Context, appWidgetId: Int)= Format(getConfiguration(context).getInt(getKey(appWidgetId), defaultValue.width))

        fun set(context: Context, value: Format, appWidgetId: Int) = getConfiguration(context).edit().putInt(getKey(appWidgetId), value.width).apply()

        private fun getKey(appWidgetId: Int) = "${key}_${appWidgetId}"
    }

    object WidgetTransparency : Configuration<Transparency>(
        key = "WIDGET_TRANSPARENCY",
        defaultValue = Transparency(20)
    ) {
        override fun get(context: Context) = Transparency(
            getConfiguration(context).getInt(key, defaultValue.percentage)
        )
    }
}

sealed class BooleanConfiguration(
    override val key: String,
    override val defaultValue: Boolean
) : Configuration<Boolean>(
    key = key,
    defaultValue = defaultValue
) {
    override fun get(context: Context) =
        getConfiguration(context).getBoolean(key, defaultValue)

    object WidgetShowDeclinedEvents : BooleanConfiguration(
        key = "WIDGET_SHOW_DECLINED_EVENTS",
        defaultValue = false
    )

    object WidgetFocusOnCurrentWeek : BooleanConfiguration(
        key = "WIDGET_FOCUS_ON_CURRENT_WEEK",
        defaultValue = false
    )
}

sealed class EnumConfiguration<E : Enum<E>>(
    override val key: String,
    override val defaultValue: E,
    private val enumClass: Class<E>
) : Configuration<E>(
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

    abstract fun getDisplayValues(context: Context): Array<String>

    abstract fun getCurrentDisplayValue(context: Context): String

    object WidgetTheme : EnumConfiguration<Theme>(
        key = "WIDGET_THEME",
        enumClass = Theme::class.java,
        defaultValue = Theme.DARK
    ) {
        override fun getDisplayValues(context: Context) = getThemeDisplayValues(context)

        override fun getCurrentDisplayValue(context: Context) = get(context).getDisplayValue(context)
    }

    object FirstDayOfWeek : EnumConfiguration<DayOfWeek>(
        key = "FIRST_DAY_OF_WEEK",
        enumClass = DayOfWeek::class.java,
        defaultValue = DayOfWeek.MONDAY
    ) {
        override fun getDisplayValues(context: Context) = getDayOfWeekDisplayValues(context)

        override fun getCurrentDisplayValue(context: Context) = get(context).getDisplayValue(context)
    }

    object InstancesSymbolSet : EnumConfiguration<SymbolSet>(
        key = "INSTANCES_SYMBOL_SET",
        enumClass = SymbolSet::class.java,
        defaultValue = SymbolSet.MINIMAL
    ) {
        override fun getDisplayValues(context: Context) = getSymbolSetDisplayValues(context)

        override fun getCurrentDisplayValue(context: Context) = get(context).getDisplayValue(context)
    }

    object InstancesColour : EnumConfiguration<Colour>(
        key = "INSTANCES_COLOUR",
        enumClass = Colour::class.java,
        defaultValue = getAvailableColors().first()
    ) {
        override fun getEnumConstants() = getAvailableColors().toTypedArray()

        override fun getDisplayValues(context: Context) = getColourDisplayValues(context)

        override fun getCurrentDisplayValue(context: Context) = get(context).getDisplayValue(context)
    }
}

fun clearAllConfiguration(context: Context) = getConfiguration(context).edit().clear().apply()

private fun getConfiguration(context: Context) = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
