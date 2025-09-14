// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.fragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.net.toUri
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import cat.mvmike.minimalcalendarwidget.BuildConfig
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.LANGUAGE_KEY
import cat.mvmike.minimalcalendarwidget.domain.configuration.PREFERENCE_KEY
import cat.mvmike.minimalcalendarwidget.domain.configuration.PercentageConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.SOURCE_KEY
import cat.mvmike.minimalcalendarwidget.domain.configuration.SOURCE_URL
import cat.mvmike.minimalcalendarwidget.domain.configuration.TRANSLATE_KEY
import cat.mvmike.minimalcalendarwidget.domain.configuration.TRANSLATE_URL
import cat.mvmike.minimalcalendarwidget.domain.configuration.VERSION_KEY
import cat.mvmike.minimalcalendarwidget.domain.configuration.isFirstDayOfWeekLocalePreferenceEnabled
import cat.mvmike.minimalcalendarwidget.domain.configuration.isPerAppLanguagePreferenceEnabled
import cat.mvmike.minimalcalendarwidget.domain.getDisplayValue
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver.getSystemFirstDayOfWeek

class SettingsFragment :
    PreferenceFragmentCompat(),
    OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        preferenceManager.sharedPreferencesName = PREFERENCE_KEY
        setPreferencesFromResource(R.xml.preferences, rootKey)

        fillEntriesAndValues()
        if (isFirstDayOfWeekLocalePreferenceEnabled()) {
            fillRegionalPreferencesValues()
        }
        if (isPerAppLanguagePreferenceEnabled()) {
            fillAppLocaleSettings()
        }
        updateCurrentSelection()
        fillAboutSection()

        preferenceManager.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(
        p0: SharedPreferences?,
        p1: String?
    ) {
        updateCurrentSelection()
        RedrawWidgetUseCase.execute(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun fillEntriesAndValues() = enumConfigurationItems().forEach {
        it.asListPreference()?.apply {
            entries = it.getDisplayValues(this@SettingsFragment.requireContext()).toTypedArray()
            entryValues = it.getKeys()
            value = it.getCurrentKey(this@SettingsFragment.requireContext())
        }
    }

    @SuppressLint("InlinedApi")
    private fun fillAppLocaleSettings() =
        LANGUAGE_KEY.asPreference()?.let {
            it.summary = SystemResolver.getSystemLocale().displayLanguage
            runCatching {
                it.setOnPreferenceClickListener {
                    startActivity(
                        Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
                            .setData(Uri.fromParts("package", requireContext().packageName, null))
                            .addFlags(
                                Intent.FLAG_ACTIVITY_NO_HISTORY or
                                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                            )
                    )
                    true
                }
            }
        }

    @SuppressLint("InlinedApi")
    private fun fillRegionalPreferencesValues() =
        EnumConfigurationItem.FirstDayOfWeek.key.asPreference()?.let {
            it.summary = getSystemFirstDayOfWeek().getDisplayValue(requireContext())
            runCatching {
                it.setOnPreferenceClickListener {
                    startActivity(
                        Intent(Settings.ACTION_REGIONAL_PREFERENCES_SETTINGS)
                            .addFlags(
                                Intent.FLAG_ACTIVITY_NO_HISTORY or
                                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                            )
                    )
                    true
                }
            }
        }

    private fun fillAboutSection() {
        SOURCE_KEY.asPreference()?.let {
            it.summary = SOURCE_URL
            it.setOnPreferenceClickListener {
                SOURCE_URL.openInBrowser()
                true
            }
        }
        TRANSLATE_KEY.asPreference()?.let {
            it.summary = TRANSLATE_URL
            it.setOnPreferenceClickListener {
                TRANSLATE_URL.openInBrowser()
                true
            }
        }
        VERSION_KEY.asPreference()?.summary = BuildConfig.VERSION_NAME
    }

    private fun updateCurrentSelection() {
        enumConfigurationItems().forEach {
            it.asListPreference()?.summary = it.getCurrentDisplayValue(requireContext())
        }

        booleanConfigurationItems().forEach {
            it.asCheckBoxPreference()?.isChecked = it.get(requireContext())
        }

        percentageConfigurationItems().forEach {
            it.asSeekBarPreference()?.value = it.get(requireContext()).value
        }
    }

    private fun enumConfigurationItems() = setOf(
        EnumConfigurationItem.WidgetTheme,
        EnumConfigurationItem.WidgetCalendar,
        EnumConfigurationItem.FirstDayOfWeek,
        EnumConfigurationItem.InstancesSymbolSet,
        EnumConfigurationItem.InstancesColour
    )

    private fun booleanConfigurationItems() = setOf(
        BooleanConfigurationItem.ShowDeclinedEvents,
        BooleanConfigurationItem.FocusOnCurrentWeek
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

    private fun String.openInBrowser() = try {
        requireContext().startActivity(
            Intent(Intent.ACTION_VIEW)
                .setData(toUri())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(requireContext(), R.string.no_browser_application, Toast.LENGTH_SHORT).show()
    }
}