// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Resources
import android.os.Build
import androidx.core.text.util.LocalePreferences
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.WeekFields

object SystemResolver {

    fun getRuntimeSDK() = Build.VERSION.SDK_INT

    fun getSystemInstant() = Instant.now()!!

    fun getSystemLocalDate() = LocalDate.now(getSystemZoneId())!!

    fun getSystemZoneId() = ZoneId.systemDefault()!!

    fun getSystemLocale() = Resources.getSystem().configuration.locales[0]!!

    fun getSystemFirstDayOfWeek(): DayOfWeek = when (LocalePreferences.getFirstDayOfWeek()) {
        LocalePreferences.FirstDayOfWeek.MONDAY -> DayOfWeek.MONDAY
        LocalePreferences.FirstDayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
        LocalePreferences.FirstDayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
        LocalePreferences.FirstDayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
        LocalePreferences.FirstDayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
        LocalePreferences.FirstDayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
        LocalePreferences.FirstDayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
        else -> WeekFields.of(getSystemLocale()).firstDayOfWeek
    }

    fun Context.isDarkThemeEnabled() =
        resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}