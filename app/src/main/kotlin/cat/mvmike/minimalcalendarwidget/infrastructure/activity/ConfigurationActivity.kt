// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.activity

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import cat.mvmike.minimalcalendarwidget.domain.configuration.clearAllConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.isFirstDayOfWeekLocalePreferenceEnabled
import cat.mvmike.minimalcalendarwidget.domain.configuration.isPerAppLanguagePreferenceEnabled
import cat.mvmike.minimalcalendarwidget.domain.getDisplayValue
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver.getSystemFirstDayOfWeek
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver.isDarkThemeEnabled

class ConfigurationActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) = context.startActivity(
            Intent(context, ConfigurationActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(
            when (applicationContext.isDarkThemeEnabled()) {
                true -> androidx.appcompat.R.style.Theme_AppCompat
                else -> androidx.appcompat.R.style.Theme_AppCompat_DayNight
            }
        )

        super.onCreate(savedInstanceState)

        setContentView(R.layout.configuration)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.configuration_view, SettingsFragment())
            .commit()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickResetSettingsButton(view: View) =
        clearAllConfiguration(applicationContext)

    @Suppress("UNUSED_PARAMETER")
    fun onClickCloseSettingsButton(view: View) {
        intent
            ?.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )?.takeIf {
                it != AppWidgetManager.INVALID_APPWIDGET_ID
            }?.let {
                setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, it))
            }
        finishAfterTransition()
    }

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

        @SuppressLint("InlinedApi")
        private fun fillRegionalPreferencesValues() =
            EnumConfigurationItem.FirstDayOfWeek.key.asPreference()?.let {
                it.summary = getSystemFirstDayOfWeek().getDisplayValue(requireContext())
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
                    .setData(Uri.parse(this))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (ignored: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.no_browser_application, Toast.LENGTH_SHORT).show()
        }
    }
}