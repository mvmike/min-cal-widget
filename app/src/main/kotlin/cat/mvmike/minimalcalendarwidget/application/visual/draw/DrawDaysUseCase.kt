// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual.draw

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.domain.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfiguration
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

    private const val NUM_WEEKS = 6

    private const val DAYS_IN_WEEK = 7

    private const val INSTANCES_QUERY_DAYS_SPAN = 45L

    fun execute(context: Context, widgetRemoteView: RemoteViews, format: Format) {
        val systemLocalDate: LocalDate = SystemResolver.getSystemLocalDate()
        val firstDayOfWeek = EnumConfiguration.FirstDayOfWeek.get(context)
        val initialLocalDate = when(BooleanConfiguration.WidgetFocusOnCurrentWeek.get(context)) {
            true -> getFocusedOnCurrentWeekInitialLocalDate(systemLocalDate, firstDayOfWeek)
            else -> getNaturalMonthInitialLocalDate(systemLocalDate, firstDayOfWeek)
        }

        val instanceSet = getInstances(
            context = context,
            from = systemLocalDate.minusDays(INSTANCES_QUERY_DAYS_SPAN),
            to = systemLocalDate.plusDays(INSTANCES_QUERY_DAYS_SPAN)
        )
        val widgetTheme = EnumConfiguration.WidgetTheme.get(context)
        val instancesSymbolSet = EnumConfiguration.InstancesSymbolSet.get(context)
        val instancesColour = EnumConfiguration.InstancesColour.get(context)
        val transparency = Configuration.WidgetTransparency.get(context)
        val showDeclinedEvents = BooleanConfiguration.WidgetShowDeclinedEvents.get(context)

        for (week in 0 until NUM_WEEKS) {
            val weekRow: RemoteViews = SystemResolver.createDaysRow(context)

            for (weekDay in 0 until DAYS_IN_WEEK) {
                val currentDay = Day(initialLocalDate.toCurrentWeekAndWeekDay(week, weekDay))
                val dayCell = widgetTheme.getCellDay(
                    isToday = currentDay.isToday(systemLocalDate),
                    inMonth = currentDay.isInMonth(systemLocalDate),
                    dayOfWeek = currentDay.getDayOfWeek()
                )
                val instancesSymbol = currentDay.getNumberOfInstances(instanceSet, showDeclinedEvents).getSymbol(instancesSymbolSet)
                val dayInstancesColour = currentDay.getInstancesColor(context, instancesColour, systemLocalDate)
                val backgroundWithTransparency = dayCell.background
                    ?.let { SystemResolver.getColourAsString(context, it) }
                    ?.withTransparency(
                        transparency = transparency,
                        transparencyRange = when (currentDay.getDayOfWeek()) {
                            DayOfWeek.SATURDAY,
                            DayOfWeek.SUNDAY -> TransparencyRange.MODERATE
                            else -> TransparencyRange.LOW
                        }
                    )

                SystemResolver.addToDaysRow(
                    context = context,
                    weekRow = weekRow,
                    dayLayout = dayCell.layout,
                    viewId = dayCell.id,
                    dayBackgroundColour = backgroundWithTransparency,
                    spanText = PADDING + currentDay.getDayOfMonthString() + PADDING + instancesSymbol,
                    isToday = currentDay.isToday(systemLocalDate),
                    isSingleDigitDay = currentDay.isSingleDigitDay(),
                    symbolRelativeSize = instancesSymbolSet.relativeSize,
                    generalRelativeSize = format.dayCellValueRelativeSize,
                    instancesColour = dayInstancesColour
                )
            }

            SystemResolver.addToWidget(
                widgetRemoteView = widgetRemoteView,
                remoteView = weekRow
            )
        }
    }

    internal fun getFocusedOnCurrentWeekInitialLocalDate(systemLocalDate: LocalDate, firstDayOfWeek: DayOfWeek): LocalDate {
        val systemLocalDateWithFirstDayOfWeek = systemLocalDate.with(firstDayOfWeek)
        return when (systemLocalDateWithFirstDayOfWeek.isAfter(systemLocalDate)) {
            true -> systemLocalDateWithFirstDayOfWeek.minusWeeks(2)
            false -> systemLocalDateWithFirstDayOfWeek.minusWeeks(1)
        }
    }

    internal fun getNaturalMonthInitialLocalDate(systemLocalDate: LocalDate, firstDayOfWeek: DayOfWeek): LocalDate {
        val firstDayOfMonth = LocalDate.of(systemLocalDate.year, systemLocalDate.monthValue, MONTH_FIRST_DAY)
        val difference = firstDayOfWeek.ordinal - firstDayOfMonth[ChronoField.DAY_OF_WEEK] + 1
        val adjustedInitialLocalDate = firstDayOfMonth.plus(difference.toLong(), ChronoUnit.DAYS)

        return when (adjustedInitialLocalDate[ChronoField.DAY_OF_MONTH]) {
            in 2..15 -> adjustedInitialLocalDate.minusDays(DAYS_IN_WEEK.toLong())
            else -> adjustedInitialLocalDate
        }
    }

    internal fun Day.getNumberOfInstances(instanceSet: Set<Instance>, includeDeclinedEvents: Boolean) = instanceSet
        .filter { it.isInDay(this.dayLocalDate) }
        .filter { includeDeclinedEvents || !it.isDeclined }
        .count()

    private fun LocalDate.toCurrentWeekAndWeekDay(week: Int, weekDay: Int) =
        this.plus((week * DAYS_IN_WEEK + weekDay).toLong(), ChronoUnit.DAYS)

    private fun Int.getSymbol(symbolSet: SymbolSet) = symbolSet.get(this)

    private fun Day.getInstancesColor(context: Context, colour: Colour, systemLocalDate: LocalDate) =
        SystemResolver.getColour(context, colour.getInstancesColour(isToday(systemLocalDate)))
}
