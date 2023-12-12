// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.user

import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem.InstancesSymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.CellDay
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.CellDay.getExtraInstant
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.ConfigurationIcon
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.MonthAndYearHeader
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.RowHeader
import cat.mvmike.minimalcalendarwidget.domain.intent.toActionableView
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver.isReadCalendarPermitted
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver

object ProcessIntentUseCase {

    fun execute(
        context: Context,
        intent: Intent
    ) = when (intent.toActionableView()) {
        ConfigurationIcon -> ConfigurationActivity.start(context)
        MonthAndYearHeader,
        RowHeader -> context.askForPermissionsIfNeededOrExecuteAndRedraw { startCalendarActivity(context) }
        CellDay -> context.askForPermissionsIfNeededOrExecuteAndRedraw { startCalendarActivity(context, intent) }
        else -> null
    }

    private fun startCalendarActivity(context: Context) = CalendarActivity.start(
        context = context,
        startTime = SystemResolver.getSystemInstant()
    )

    private fun startCalendarActivity(
        context: Context,
        intent: Intent
    ) = CalendarActivity.start(
        context = context,
        startTime = when {
            BooleanConfigurationItem.OpenCalendarOnClickedDay.get(context) -> intent.getExtraInstant()
            else -> SystemResolver.getSystemInstant()
        }
    )

    private fun Context.askForPermissionsIfNeededOrExecuteAndRedraw(function: () -> Unit) = when {
        isReadCalendarPermitted(this) || InstancesSymbolSet.get(this) == SymbolSet.NONE -> {
            function.invoke()
            RedrawWidgetUseCase.execute(this)
        }
        else -> PermissionsActivity.start(this)
    }
}