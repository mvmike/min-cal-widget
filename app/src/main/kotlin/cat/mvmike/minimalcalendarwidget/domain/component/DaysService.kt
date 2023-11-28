// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.content.Context
import android.graphics.Typeface
import android.text.Layout
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.domain.Day
import cat.mvmike.minimalcalendarwidget.domain.Instance
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.CellContent
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
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

    private const val NUMBER_OF_DAYS = (NUM_WEEKS * DAYS_IN_WEEK).toLong()

    fun draw(
        context: Context,
        widgetRemoteView: RemoteViews,
        firstDayOfWeek: DayOfWeek,
        widgetTheme: Theme,
        transparency: Transparency,
        textSize: TextSize
    ) {
        val systemLocalDate: LocalDate = SystemResolver.getSystemLocalDate()
        val initialLocalDate = when (BooleanConfigurationItem.FocusOnCurrentWeek.get(context)) {
            true -> getFocusedOnCurrentWeekInitialLocalDate(systemLocalDate, firstDayOfWeek)
            else -> getNaturalMonthInitialLocalDate(systemLocalDate, firstDayOfWeek)
        }

        val instanceSet = getInstances(
            context = context,
            from = initialLocalDate,
            to = initialLocalDate.plusDays(NUMBER_OF_DAYS)
        )
        val instancesSymbolSet = EnumConfigurationItem.InstancesSymbolSet.get(context)
        val instancesColour = EnumConfigurationItem.InstancesColour.get(context)
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
                val dayInstancesColour = instancesColour.getInstancesColour(isToday, widgetTheme)
                val backgroundWithTransparency = dayCell.background
                    ?.let { GraphicResolver.getColourAsString(context, it) }
                    ?.withTransparency(
                        transparency = transparency,
                        transparencyRange = when {
                            isToday -> TransparencyRange.HIGH
                            currentDay.isWeekend() -> TransparencyRange.MODERATE
                            else -> TransparencyRange.LOW
                        }
                    )

                val dayOfMonthRemoteView = GraphicResolver.createDayLayout(context, dayCell.layout)
                val instancesSymbolRemoteView = when {
                    instancesSymbolSet == SymbolSet.NONE -> null
                    instanceSet.isEmpty() -> null
                    else -> GraphicResolver.createDayLayout(context, dayCell.layout)
                }
                GraphicResolver.addToDaysRow(
                    context = context,
                    weekRowRemoteView = weekRowRemoteView,
                    viewId = dayCell.id,
                    backgroundColour = backgroundWithTransparency,
                    cells = listOf(
                        dayOfMonthRemoteView to CellContent(
                            text = currentDay.getDayOfMonthString(),
                            colour = dayCell.textColour,
                            relativeSize = textSize.relativeValue,
                            style = when {
                                isToday -> Typeface.BOLD
                                else -> null
                            },
                            alignment = instancesSymbolRemoteView?.let {
                                Layout.Alignment.ALIGN_OPPOSITE
                            }
                        ),
                        instancesSymbolRemoteView to CellContent(
                            text = instancesSymbol.toString(),
                            colour = dayInstancesColour,
                            relativeSize = instancesSymbolSet.relativeSize * textSize.relativeValue,
                            style = Typeface.BOLD
                        )
                    )
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
            else -> systemLocalDateWithFirstDayOfWeek.minusWeeks(1)
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

    private fun LocalDate.toCurrentWeekAndWeekDay(
        week: Int,
        weekDay: Int
    ) = plus((week * DAYS_IN_WEEK + weekDay).toLong(), ChronoUnit.DAYS)
}