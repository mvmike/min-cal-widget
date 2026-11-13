// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.fragment

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import cat.mvmike.minimalcalendarwidget.BuildConfig
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.Widget
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.Colour
import cat.mvmike.minimalcalendarwidget.domain.Variant
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem.DefaultVisibleCalendars
import cat.mvmike.minimalcalendarwidget.domain.configuration.ColourConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.IntConfigurationItem.Page
import cat.mvmike.minimalcalendarwidget.domain.configuration.PercentageConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.getConfigurationName
import cat.mvmike.minimalcalendarwidget.domain.configuration.isFirstDayOfWeekLocalePreferenceEnabled
import cat.mvmike.minimalcalendarwidget.domain.configuration.isPerAppLanguagePreferenceEnabled
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextColour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.isSystemAccentAvailable
import cat.mvmike.minimalcalendarwidget.domain.getDisplayValue
import cat.mvmike.minimalcalendarwidget.infrastructure.preference.MultilineColourPreference
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver.isReadCalendarPermitted
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver.getSystemFirstDayOfWeek
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

private const val SOURCE_KEY = "SOURCE"

private const val TRANSLATE_KEY = "TRANSLATE"

private const val LANGUAGE_KEY = "LANGUAGE"

private const val VERSION_KEY = "VERSION"

private const val SOURCE_URL = "https://github.com/mvmike/min-cal-widget"

private const val TRANSLATE_URL = "https://hosted.weblate.org/engage/min-cal-widget"

private const val VISIBLE_CALENDAR_SELECTION_KEY = "SELECT_VISIBLE_CALENDARS"

class SettingsFragment :
    PreferenceFragmentCompat(),
    OnSharedPreferenceChangeListener {

    private val appWidgetId
        get() = requireArguments().getInt(EXTRA_APPWIDGET_ID)

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        preferenceManager.sharedPreferencesName = getConfigurationName(appWidgetId)
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val context = requireContext()
        fillEntriesAndValues(context)
        if (isFirstDayOfWeekLocalePreferenceEnabled()) {
            fillRegionalPreferencesValues(context)
        }
        if (isPerAppLanguagePreferenceEnabled()) {
            fillAppLocaleSettings(context)
        }
        fillVisibleCalendarSelection()
        fillInstancesColourPreference(context)
        fillVariantPreferences(context)
        updateCurrentSelection(context)
        fillAboutSection(context)

        preferenceManager.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(
        p0: SharedPreferences?,
        p1: String?
    ) {
        val context = requireContext()
        if (p1 == BooleanConfigurationItem.AllowPagination.key) {
            Page.reset(context, appWidgetId)
        }
        updateCurrentSelection(context)
        RedrawWidgetUseCase.execute(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun fillEntriesAndValues(context: Context) = enumConfigurationItems().forEach {
        it.asListPreference()?.apply {
            entries = it.getDisplayValues(context).toTypedArray()
            entryValues = it.getKeys()
            value = it.getCurrentKey(context, appWidgetId)
        }
    }

    @SuppressLint("InlinedApi")
    private fun fillAppLocaleSettings(context: Context) =
        LANGUAGE_KEY.asPreference()?.let {
            it.summary = SystemResolver.getSystemLocale().displayLanguage
            it.setOnPreferenceClickListener {
                runCatching {
                    startActivity(
                        Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
                            .setData(Uri.fromParts("package", context.packageName, null))
                            .addFlags(
                                Intent.FLAG_ACTIVITY_NO_HISTORY or
                                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                            )
                    )
                }
                true
            }
        }

    @SuppressLint("InlinedApi")
    private fun fillRegionalPreferencesValues(context: Context) =
        EnumConfigurationItem.FirstDayOfWeek.key.asPreference()?.let {
            it.summary = getSystemFirstDayOfWeek().getDisplayValue(context)
            it.setOnPreferenceClickListener {
                runCatching {
                    startActivity(
                        Intent(Settings.ACTION_REGIONAL_PREFERENCES_SETTINGS)
                            .addFlags(
                                Intent.FLAG_ACTIVITY_NO_HISTORY or
                                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                            )
                    )
                }
                true
            }
        }

    private fun fillVisibleCalendarSelection() =
        VISIBLE_CALENDAR_SELECTION_KEY.asPreference()?.extras?.putInt(EXTRA_APPWIDGET_ID, appWidgetId)

    private fun fillInstancesColourPreference(context: Context) =
        ColourConfigurationItem.InstancesColour.key.asPreference()?.setOnPreferenceClickListener {
            val builder = ColorPickerDialog
                .Builder(context)
                .setTitle(R.string.symbol_colour)
                .setPositiveButton(
                    android.R.string.ok,
                    ColorEnvelopeListener { envelope, _ ->
                        ColourConfigurationItem.InstancesColour.set(
                            context,
                            appWidgetId,
                            TextColour.CustomHex("#${envelope.hexCode}")
                        )
                    }
                ).setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }.attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)

            val currentColour = ColourConfigurationItem.InstancesColour.get(context, appWidgetId)
            if (currentColour is TextColour.CustomHex) {
                builder.colorPickerView.setInitialColor(GraphicResolver.getColour(Colour.Hex(currentColour.hexColour)))
            }

            if (isSystemAccentAvailable()) {
                builder.setNeutralButton(R.string.system_accent) { _, _ ->
                    ColourConfigurationItem.InstancesColour.set(
                        context,
                        appWidgetId,
                        TextColour.SystemAccent
                    )
                }
            }

            builder.show()
            true
        }

    private fun fillVariantPreferences(context: Context) {
        val provider = AppWidgetManager.getInstance(context).getAppWidgetInfo(appWidgetId)?.provider
        BooleanConfigurationItem.FocusOnCurrentWeek.key.asPreference()?.let {
            it.isVisible = provider?.className != Widget.getReceiverClass(Variant.WEEK).name
        }
    }

    private fun fillAboutSection(context: Context) {
        SOURCE_KEY.asPreference()?.let {
            it.summary = SOURCE_URL
            it.setOnPreferenceClickListener {
                SOURCE_URL.openInBrowser(context)
                true
            }
        }
        TRANSLATE_KEY.asPreference()?.let {
            it.summary = TRANSLATE_URL
            it.setOnPreferenceClickListener {
                TRANSLATE_URL.openInBrowser(context)
                true
            }
        }
        VERSION_KEY.asPreference()?.summary = BuildConfig.VERSION_NAME
    }

    private fun updateCurrentSelection(context: Context) {
        VISIBLE_CALENDAR_SELECTION_KEY.asPreference()?.let {
            val isReadCalendarPermitted = isReadCalendarPermitted(context)
            val default = DefaultVisibleCalendars.get(context, appWidgetId)
            it.summary = when {
                !isReadCalendarPermitted -> null
                default -> R.string.visible_calendar_selection_default
                else -> R.string.visible_calendar_selection_custom
            }?.let { resource -> context.getString(resource) }
        }

        enumConfigurationItems().forEach {
            it.asListPreference()?.summary = it.getCurrentDisplayValue(context, appWidgetId)
        }

        (ColourConfigurationItem.InstancesColour.key.asPreference() as? MultilineColourPreference)?.let { pref ->
            when (val colour = ColourConfigurationItem.InstancesColour.get(context, appWidgetId)) {
                is TextColour.SystemAccent -> {
                    pref.summary = colour.getDisplayValue(context)
                    val widgetTheme = EnumConfigurationItem.WidgetTheme.get(context, appWidgetId)
                    pref.swatchColour = GraphicResolver.getColour(context, colour.getInstancesColour(false, widgetTheme))
                }
                is TextColour.CustomHex -> {
                    pref.summary = " "
                    pref.swatchColour = GraphicResolver.getColour(Colour.Hex(colour.hexColour))
                }
            }
        }

        booleanConfigurationItems().forEach {
            it.asCheckBoxPreference()?.isChecked = it.get(context, appWidgetId)
        }

        percentageConfigurationItems().forEach {
            it.asSeekBarPreference()?.value = it.get(context, appWidgetId).value
        }
    }

    private fun enumConfigurationItems() = setOf(
        EnumConfigurationItem.WidgetTheme,
        EnumConfigurationItem.WidgetCalendar,
        EnumConfigurationItem.FirstDayOfWeek,
        EnumConfigurationItem.InstancesSymbolSet
    )

    private fun booleanConfigurationItems() = setOf(
        BooleanConfigurationItem.ShowDeclinedEvents,
        BooleanConfigurationItem.FocusOnCurrentWeek,
        BooleanConfigurationItem.ShowWeekNumber,
        BooleanConfigurationItem.AllowPagination
    )

    private fun percentageConfigurationItems() = setOf(
        PercentageConfigurationItem.WidgetTransparency,
        PercentageConfigurationItem.WidgetTextSize
    )

    private fun String.asPreference() =
        preferenceManager.findPreference(this) as? Preference

    private fun <E> ConfigurationItem<E>.asListPreference() =
        preferenceManager.findPreference<Preference>(key) as? ListPreference

    private fun <E> ConfigurationItem<E>.asCheckBoxPreference() =
        preferenceManager.findPreference<Preference>(key) as? CheckBoxPreference

    private fun <E> ConfigurationItem<E>.asSeekBarPreference() =
        preferenceManager.findPreference<Preference>(key) as? SeekBarPreference

    private fun String.openInBrowser(context: Context) = try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setData(toUri())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, R.string.no_browser_application, Toast.LENGTH_SHORT).show()
    }
}