// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.withTransparency
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
        val transparency = Configuration.WidgetTransparency.get(context)
        val firstDayOfWeek = EnumConfiguration.FirstDayOfWeek.get(context)
        val widgetTheme = EnumConfiguration.WidgetTheme.get(context)
        val instancesSymbolSet = EnumConfiguration.InstancesSymbolSet.get(context)
        val instancesColour = EnumConfiguration.InstancesColour.get(context)
        val initialLocalDate = getInitialLocalDate(systemLocalDate, firstDayOfWeek)

        for (week in 0 until NUM_WEEKS) {
            val weekRow: RemoteViews = SystemResolver.get().createDaysRow(context)

            for (weekDay in 0 until DAYS_IN_WEEK) {
                val currentDay = Day(
                    systemLocalDate = systemLocalDate,
                    dayLocalDate = initialLocalDate.toCurrentWeekAndWeekDay(week, weekDay)
                )
                val dayCell = widgetTheme.getCellDay(
                    isToday = currentDay.isToday(),
                    inMonth = currentDay.isInMonth(),
                    dayOfWeek = currentDay.getDayOfWeek()
                )
                val instancesSymbol = currentDay.getNumberOfInstances(instanceSet).getSymbol(instancesSymbolSet)
                val dayInstancesColour = currentDay.getInstancesColor(context, instancesColour)
                val backgroundWithTransparency = dayCell.background
                    ?.let { SystemResolver.get().getColourAsString(context, it) }
                    ?.withTransparency(
                        transparency = transparency,
                        transparencyRange = when (currentDay.getDayOfWeek()) {
                            DayOfWeek.SATURDAY,
                            DayOfWeek.SUNDAY -> TransparencyRange.MODERATE
                            else -> TransparencyRange.LOW
                        }
                    )

                SystemResolver.get().addToDaysRow(
                    context = context,
                    weekRow = weekRow,
                    dayLayout = dayCell.layout,
                    viewId = dayCell.id,
                    dayBackgroundColour = backgroundWithTransparency,
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

    internal fun getInitialLocalDate(systemLocalDate: LocalDate, firstDayOfWeek: DayOfWeek): LocalDate {
        val firstDayOfMonth = LocalDate.of(systemLocalDate.year, systemLocalDate.monthValue, MONTH_FIRST_DAY)
        val difference = firstDayOfWeek.ordinal - firstDayOfMonth[ChronoField.DAY_OF_WEEK] + 1
        val adjustedInitialLocalDate = firstDayOfMonth.plus(difference.toLong(), ChronoUnit.DAYS)

        // overlap month manually if dayOfMonth is in current month and greater than 1
        val dayOfMonth = adjustedInitialLocalDate[ChronoField.DAY_OF_MONTH]
        return when {
            dayOfMonth > MONTH_FIRST_DAY && dayOfMonth < MAXIMUM_DAYS_IN_MONTH / 2 -> adjustedInitialLocalDate.minusDays(DAYS_IN_WEEK.toLong())
            else -> adjustedInitialLocalDate
        }
    }

    private fun LocalDate.toCurrentWeekAndWeekDay(week: Int, weekDay: Int) =
        this.plus((week * DAYS_IN_WEEK + weekDay).toLong(), ChronoUnit.DAYS)

    private fun Day.getNumberOfInstances(instanceSet: Set<Instance>) = instanceSet
        .filter { it.isInDay(this.dayLocalDate) }
        .count()

    private fun Int.getSymbol(symbolSet: SymbolSet) = symbolSet.get(this)

    private fun Day.getInstancesColor(context: Context, colour: Colour) =
        SystemResolver.get().getColour(context, colour.getInstancesColour(isToday()))
}
