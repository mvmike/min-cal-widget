// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.PREFERENCE_KEY
import cat.mvmike.minimalcalendarwidget.domain.configuration.SOURCE_KEY
import cat.mvmike.minimalcalendarwidget.domain.configuration.SOURCE_VALUE
import cat.mvmike.minimalcalendarwidget.domain.configuration.VERSION_KEY

class ConfigurationActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) = context.startActivity(
            Intent(context, ConfigurationActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuration)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.configuration_view, SettingsFragment())
            .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = PREFERENCE_KEY
            setPreferencesFromResource(R.xml.preferences, rootKey)

            fillEntriesAndValues()
            updateCurrentSelection()
            fillAboutSection()

            preferenceManager.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
            updateCurrentSelection()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            preferenceManager.sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
            RedrawWidgetUseCase.execute(this.requireContext())
        }

        private fun fillEntriesAndValues() = enumConfigurationItems().forEach {
            it.asListPreference().apply {
                entries = it.getDisplayValues(this@SettingsFragment.requireContext())
                entryValues = it.getKeys()
                value = it.getCurrentKey(this@SettingsFragment.requireContext())
            }
        }

        private fun fillAboutSection() {
            VERSION_KEY.asPreference().summary = this.requireContext().packageManager.getPackageInfo(this.requireContext().packageName, 0).versionName
            SOURCE_KEY.asPreference().let {
                it.summary = SOURCE_VALUE
                it.setOnPreferenceClickListener {
                    try {
                        this.requireContext().startActivity(
                            Intent(Intent.ACTION_VIEW)
                                .setData(Uri.parse(SOURCE_VALUE))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    } catch (ignored: ActivityNotFoundException) {
                        Toast.makeText(this.requireContext(), R.string.no_browser_application, Toast.LENGTH_SHORT).show()
                    }
                    true
                }
            }
        }

        private fun updateCurrentSelection() {
            enumConfigurationItems().forEach {
                it.asListPreference().summary = it.getCurrentDisplayValue(this.requireContext())
            }

            booleanConfigurationItems().forEach {
                it.asCheckBoxPreference().isChecked = it.get(this.requireContext())
            }

            Configuration.WidgetTransparency.asSeekBarPreference().value = Configuration.WidgetTransparency.get(this.requireContext()).percentage
        }

        private fun enumConfigurationItems() = setOf(
            EnumConfiguration.WidgetTheme,
            EnumConfiguration.FirstDayOfWeek,
            EnumConfiguration.InstancesSymbolSet,
            EnumConfiguration.InstancesColour
        )

        private fun booleanConfigurationItems() = setOf(
            BooleanConfiguration.WidgetShowDeclinedEvents,
            BooleanConfiguration.WidgetFocusOnCurrentWeek
        )

        private fun String.asPreference() =
            preferenceManager.findPreference<Preference>(this) as Preference

        private fun <E> Configuration<E>.asListPreference() =
            preferenceManager.findPreference<Preference>(this.key) as ListPreference

        private fun <E> Configuration<E>.asCheckBoxPreference() =
            preferenceManager.findPreference<Preference>(this.key) as CheckBoxPreference

        private fun <E> Configuration<E>.asSeekBarPreference() =
            preferenceManager.findPreference<Preference>(this.key) as SeekBarPreference
    }
}
