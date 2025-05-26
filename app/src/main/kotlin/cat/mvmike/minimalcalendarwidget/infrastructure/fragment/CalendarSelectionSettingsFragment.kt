// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.annotation.Keep
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.children
import androidx.preference.forEach
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem.CalendarVisibilitySelection
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem.DefaultVisibleCalendars
import cat.mvmike.minimalcalendarwidget.domain.configuration.PREFERENCE_KEY
import cat.mvmike.minimalcalendarwidget.domain.getCalendars
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver.isReadCalendarPermitted

@Keep
class CalendarSelectionSettingsFragment : PreferenceFragmentCompat(),
    OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        val context = requireContext()
        if (!isReadCalendarPermitted(context)) {
            PermissionsActivity.start(context)
            activity?.supportFragmentManager?.popBackStack()
            return
        }

        preferenceManager.sharedPreferencesName = PREFERENCE_KEY
        setPreferencesFromResource(R.xml.preferences_calendars, rootKey)
        fillInstanceCalendarSelection(context)
        preferenceManager.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(
        p0: SharedPreferences?,
        p1: String?
    ) {
        val context = requireContext()
        updateInstanceCalendarSelection(context)
        RedrawWidgetUseCase.execute(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun fillInstanceCalendarSelection(context: Context) {
        val defaultCalendarVisibility = getDefaultCalendarVisibility()
        val calendars = getCalendars(context)
        val accountNames = calendars.map { it.accountName }.toSet()
        val calendarVisibilityConfiguration = calendars.associate { it.id to CalendarVisibilitySelection(it.id) }

        // always reset all calendar selection preferences since we could have changes in accounts/calendars
        preferenceScreen.children
            .filterNot { it.key == DefaultVisibleCalendars.key }
            .forEach { preferenceScreen.removePreference(it) }

        accountNames.forEach { accountName ->
            val preferenceCategory = PreferenceCategory(preferenceScreen.context)
            preferenceCategory.title = accountName
            preferenceCategory.isIconSpaceReserved = false
            preferenceScreen.addPreference(preferenceCategory)

            calendars
                .filter { it.accountName == accountName }
                .forEach { calendar ->
                    val checkBoxPreference = CheckBoxPreference(preferenceScreen.context)
                    checkBoxPreference.title = calendar.displayName
                    checkBoxPreference.key = CalendarVisibilitySelection(calendar.id).key
                    if (defaultCalendarVisibility) {
                        checkBoxPreference.isChecked = calendar.isVisible
                        checkBoxPreference.isEnabled = false
                    } else {
                        checkBoxPreference.isChecked = calendarVisibilityConfiguration[calendar.id]
                            ?.get(context) ?: calendar.isVisible
                        checkBoxPreference.isEnabled = true
                    }
                    preferenceCategory.addPreference(checkBoxPreference)
                }
        }
    }

    private fun updateInstanceCalendarSelection(context: Context) {
        val defaultCalendarVisibility = getDefaultCalendarVisibility()
        val calendars = getCalendars(context)
        val calendarVisibilityConfiguration = calendars.associateBy { CalendarVisibilitySelection(it.id).key }

        preferenceScreen.forEach { account ->
            when (account) {
                is PreferenceCategory -> {
                    account.forEach { calendar ->
                        val calendarCheckBoxPreference = (calendar as CheckBoxPreference)
                        if (defaultCalendarVisibility) {
                            calendarCheckBoxPreference.isChecked = calendarVisibilityConfiguration[calendar.key]?.isVisible ?: true
                            calendarCheckBoxPreference.isEnabled = false
                        } else {
                            calendarCheckBoxPreference.isEnabled = true
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun getDefaultCalendarVisibility() = preferenceManager
        .findPreference<CheckBoxPreference>(DefaultVisibleCalendars.key)
        ?.isChecked == true
}