// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.domain.Day
import cat.mvmike.minimalcalendarwidget.domain.Instance
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.PercentageConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.withTransparency
import cat.mvmike.minimalcalendarwidget.domain.getInstances
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.CellDay
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

object DaysService {

    private const val NUM_WEEKS = 6

    private const val DAYS_IN_WEEK = 7

    private const val INSTANCES_QUERY_DAYS_SPAN = 45L

    fun draw(context: Context, widgetRemoteView: RemoteViews, textSize: TextSize) {
        val systemLocalDate: LocalDate = SystemResolver.getSystemLocalDate()
        val firstDayOfWeek = EnumConfigurationItem.FirstDayOfWeek.get(context)
        val initialLocalDate = when (BooleanConfigurationItem.FocusOnCurrentWeek.get(context)) {
            true -> getFocusedOnCurrentWeekInitialLocalDate(systemLocalDate, firstDayOfWeek)
            else -> getNaturalMonthInitialLocalDate(systemLocalDate, firstDayOfWeek)
        }

        val instanceSet = getInstances(
            context = context,
            from = systemLocalDate.minusDays(INSTANCES_QUERY_DAYS_SPAN),
            to = systemLocalDate.plusDays(INSTANCES_QUERY_DAYS_SPAN)
        )
        val widgetTheme = EnumConfigurationItem.WidgetTheme.get(context)
        val instancesSymbolSet = EnumConfigurationItem.InstancesSymbolSet.get(context)
        val instancesColour = EnumConfigurationItem.InstancesColour.get(context)
        val transparency = PercentageConfigurationItem.WidgetTransparency.get(context)
        val showDeclinedEvents = BooleanConfigurationItem.ShowDeclinedEvents.get(context)

        for (week in 0 until NUM_WEEKS) {
            val weekRowRemoteView: RemoteViews = GraphicResolver.createDaysRow(context)

            for (weekDay in 0 until DAYS_IN_WEEK) {
                val currentDay = Day(initialLocalDate.toCurrentWeekAndWeekDay(week, weekDay))
                val isToday = currentDay.isToday(systemLocalDate)
                val dayCell = widgetTheme.getCellDay(
                    isToday = isToday,
                    inMonth = currentDay.isInMonth(systemLocalDate),
                    dayOfWeek = currentDay.getDayOfWeek()
                )
                val instancesSymbol = currentDay
                    .getNumberOfInstances(instanceSet, showDeclinedEvents)
                    .let { instancesSymbolSet.get(it) }
                val dayInstancesColour = getInstancesColor(context, instancesColour, widgetTheme, isToday)
                val backgroundWithTransparency = dayCell.background
                    ?.let { GraphicResolver.getColourAsString(context, it) }
                    ?.withTransparency(
                        transparency = transparency,
                        transparencyRange = when (currentDay.getDayOfWeek()) {
                            DayOfWeek.SATURDAY,
                            DayOfWeek.SUNDAY -> TransparencyRange.MODERATE
                            else -> TransparencyRange.LOW
                        }
                    )

                val dayOfMonthRemoteView = GraphicResolver.createDayLayout(context, dayCell.layout)
                val instancesSymbolRemoteView = GraphicResolver.createDayLayout(context, dayCell.layout)
                GraphicResolver.addToDaysRow(
                    context = context,
                    weekRowRemoteView = weekRowRemoteView,
                    dayOfMonthRemoteView = dayOfMonthRemoteView,
                    instancesSymbolRemoteView = instancesSymbolRemoteView,
                    viewId = dayCell.id,
                    dayOfMonth = currentDay.getDayOfMonthString(),
                    dayOfMonthColour = dayCell.textColour,
                    dayOfMonthInBold = isToday,
                    instancesSymbol = instancesSymbol,
                    instancesSymbolColour = dayInstancesColour,
                    instancesRelativeSize = instancesSymbolSet.relativeSize,
                    dayBackgroundColour = backgroundWithTransparency,
                    textRelativeSize = textSize.relativeValue
                )
                CellDay.addListener(
                    context = context,
                    remoteViews = arrayOf(dayOfMonthRemoteView, instancesSymbolRemoteView),
                    startOfDay = currentDay.dayLocalDate.atStartOfDay(SystemResolver.getSystemZoneId()).toInstant()
                )
            }

            GraphicResolver.addToWidget(
                widgetRemoteView = widgetRemoteView,
                remoteView = weekRowRemoteView
            )
        }
    }

    internal fun getFocusedOnCurrentWeekInitialLocalDate(
        systemLocalDate: LocalDate,
        firstDayOfWeek: DayOfWeek
    ): LocalDate {
        val systemLocalDateWithFirstDayOfWeek = systemLocalDate.with(firstDayOfWeek)
        return when (systemLocalDateWithFirstDayOfWeek.isAfter(systemLocalDate)) {
            true -> systemLocalDateWithFirstDayOfWeek.minusWeeks(2)
            false -> systemLocalDateWithFirstDayOfWeek.minusWeeks(1)
        }
    }

    internal fun getNaturalMonthInitialLocalDate(
        systemLocalDate: LocalDate,
        firstDayOfWeek: DayOfWeek
    ): LocalDate {
        val firstDayOfMonth = LocalDate.of(systemLocalDate.year, systemLocalDate.monthValue, 1)
        val difference = firstDayOfWeek.ordinal - firstDayOfMonth[ChronoField.DAY_OF_WEEK] + 1
        val adjustedInitialLocalDate = firstDayOfMonth.plus(difference.toLong(), ChronoUnit.DAYS)

        return when (adjustedInitialLocalDate[ChronoField.DAY_OF_MONTH]) {
            in 2..15 -> adjustedInitialLocalDate.minusDays(DAYS_IN_WEEK.toLong())
            else -> adjustedInitialLocalDate
        }
    }

    internal fun Day.getNumberOfInstances(
        instanceSet: Set<Instance>,
        includeDeclinedEvents: Boolean
    ) = instanceSet
        .filter { it.isInDay(dayLocalDate) }
        .count { includeDeclinedEvents || !it.isDeclined }

    private fun LocalDate.toCurrentWeekAndWeekDay(week: Int, weekDay: Int) =
        plus((week * DAYS_IN_WEEK + weekDay).toLong(), ChronoUnit.DAYS)

    private fun getInstancesColor(
        context: Context,
        colour: Colour,
        widgetTheme: Theme,
        isToday: Boolean
    ) = GraphicResolver.getColour(
        context = context,
        id = colour.getInstancesColour(isToday, widgetTheme)
    )
}