// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.entry.Day
import cat.mvmike.minimalcalendarwidget.domain.entry.Instance
import cat.mvmike.minimalcalendarwidget.domain.entry.getInstances
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

object DrawDaysUseCase {

    private const val PADDING: String = " "

    private const val MONTH_FIRST_DAY = 1

    private const val MAXIMUM_DAYS_IN_MONTH = 31

    private const val NUM_WEEKS = 6

    private const val DAYS_IN_WEEK = 7

    private const val INSTANCES_QUERY_DAYS_SPAN = 45L

    fun execute(context: Context, widgetRemoteView: RemoteViews) {
        val systemLocalDate: LocalDate = SystemResolver.get().getSystemLocalDate()
        val instanceSet = getInstances(
            context = context,
            from = systemLocalDate.minusDays(INSTANCES_QUERY_DAYS_SPAN),
            to = systemLocalDate.plusDays(INSTANCES_QUERY_DAYS_SPAN)
        )
        val firstDayOfWeek = Configuration.FirstDayOfWeek.get(context)
        val theme = Configuration.CalendarTheme.get(context)
        val instancesSymbolSet = Configuration.InstancesSymbolSet.get(context)
        val instancesColour = Configuration.InstancesColour.get(context)
        val initialLocalDate = getInitialLocalDate(systemLocalDate, firstDayOfWeek)

        for (week in 0 until NUM_WEEKS) {
            val weekRow: RemoteViews = SystemResolver.get().createDaysRow(context)

            for (weekDay in 0 until DAYS_IN_WEEK) {
                val currentDay = Day(
                    systemLocalDate = systemLocalDate,
                    dayLocalDate = initialLocalDate.toCurrentWeekAndWeekDay(week, weekDay)
                )
                val dayLayout = theme.getCellDay(
                    isToday = currentDay.isToday(),
                    inMonth = currentDay.isInMonth(),
                    dayOfWeek = currentDay.getDayOfWeek()
                )
                val instancesSymbol = currentDay.getNumberOfInstances(instanceSet).getSymbol(instancesSymbolSet)
                val dayInstancesColour = currentDay.getInstancesColor(context, instancesColour)

                SystemResolver.get().addToDaysRow(
                    context = context,
                    weekRow = weekRow,
                    dayLayout = dayLayout,
                    spanText = PADDING + currentDay.getDayOfMonthString() + PADDING + instancesSymbol,
                    isToday = currentDay.isToday(),
                    isSingleDigitDay = currentDay.isSingleDigitDay(),
                    symbolRelativeSize = instancesSymbolSet.relativeSize,
                    instancesColour = dayInstancesColour
                )
            }

            SystemResolver.get().addToWidget(
                widgetRemoteView = widgetRemoteView,
                remoteView = weekRow
            )
        }
    }

    private fun getInitialLocalDate(systemLocalDate: LocalDate, firstDayOfWeek: DayOfWeek): LocalDate {
        val firstDayOfMonth = LocalDate.of(systemLocalDate.year, systemLocalDate.monthValue, MONTH_FIRST_DAY)
        val difference = firstDayOfWeek.ordinal - firstDayOfMonth[ChronoField.DAY_OF_WEEK] + 1
        val localDate = firstDayOfMonth.plus(difference.toLong(), ChronoUnit.DAYS)

        // overlap month manually if dayOfMonth is in current month and greater than 1
        return if (localDate[ChronoField.DAY_OF_MONTH] > MONTH_FIRST_DAY
            && localDate[ChronoField.DAY_OF_MONTH] < MAXIMUM_DAYS_IN_MONTH / 2
        ) {
            localDate.minus(DAYS_IN_WEEK.toLong(), ChronoUnit.DAYS)
        } else {
            localDate
        }
    }

    private fun LocalDate.toCurrentWeekAndWeekDay(week: Int, weekDay: Int) =
        this.plus((week * DAYS_IN_WEEK + weekDay).toLong(), ChronoUnit.DAYS)

    private fun Day.getNumberOfInstances(instanceSet: Set<Instance>) = instanceSet
        .filter { it.isInDay(this.dayLocalDate) }
        .count()

    private fun Int.getSymbol(symbolSet: SymbolSet) = symbolSet.get(this)

    private fun Day.getInstancesColor(context: Context, colour: Colour): Int {
        return when {
            this.isToday() -> SystemResolver.get().getInstancesColorTodayId(context)
            else -> SystemResolver.get().getInstancesColorId(context, colour)
        }
    }
}


