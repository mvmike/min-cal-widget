// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.content.res.Resources
import android.os.Build
import androidx.core.text.util.LocalePreferences
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.WeekFields

object SystemResolver {

    fun getRuntimeSDK() = Build.VERSION.SDK_INT

    fun getSystemInstant() = Clock.systemUTC().instant()!!

    fun getSystemLocalDate() = LocalDate.now(Clock.systemDefaultZone())!!

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
}