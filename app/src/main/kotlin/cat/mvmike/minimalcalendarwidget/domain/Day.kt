// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import android.content.Context
import cat.mvmike.minimalcalendarwidget.R
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDate

private const val DAY_OF_MONTH_DF_PATTERN: String = "00"

data class Day(
    val dayLocalDate: LocalDate
) {

    fun getDayOfWeek(): DayOfWeek = dayLocalDate.dayOfWeek

    fun getDayOfMonthString(): String = DecimalFormat(DAY_OF_MONTH_DF_PATTERN)
        .format(dayLocalDate.dayOfMonth.toLong())

    fun isInMonth(systemLocalDate: LocalDate) = dayLocalDate.year == systemLocalDate.year
            && dayLocalDate.month == systemLocalDate.month

    fun isToday(systemLocalDate: LocalDate) = isInMonth(systemLocalDate)
            && dayLocalDate.dayOfYear == systemLocalDate.dayOfYear

    fun isSingleDigitDay() = dayLocalDate.dayOfMonth < 10
}

fun getDayOfWeekDisplayValues(context: Context) =
    DayOfWeek.values()
        .map { it.getDisplayValue(context) }
        .toTypedArray()

fun DayOfWeek.getDisplayValue(context: Context) =
    context.getString(
        when (this) {
            DayOfWeek.MONDAY -> R.string.monday
            DayOfWeek.TUESDAY -> R.string.tuesday
            DayOfWeek.WEDNESDAY -> R.string.wednesday
            DayOfWeek.THURSDAY -> R.string.thursday
            DayOfWeek.FRIDAY -> R.string.friday
            DayOfWeek.SATURDAY -> R.string.saturday
            DayOfWeek.SUNDAY -> R.string.sunday
        }
    ).replaceFirstChar { it.uppercase() }

fun DayOfWeek.getAbbreviatedDisplayValue(context: Context) =
    context.getString(
        when (this) {
            DayOfWeek.MONDAY -> R.string.monday_abb
            DayOfWeek.TUESDAY -> R.string.tuesday_abb
            DayOfWeek.WEDNESDAY -> R.string.wednesday_abb
            DayOfWeek.THURSDAY -> R.string.thursday_abb
            DayOfWeek.FRIDAY -> R.string.friday_abb
            DayOfWeek.SATURDAY -> R.string.saturday_abb
            DayOfWeek.SUNDAY -> R.string.sunday_abb
        }
    ).replaceFirstChar { it.uppercase() }
