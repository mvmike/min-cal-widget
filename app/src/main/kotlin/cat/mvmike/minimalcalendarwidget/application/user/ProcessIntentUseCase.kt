// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.user

import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.CellDay
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.CellDay.getExtraInstant
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.ConfigurationIcon
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.MonthAndYearHeader
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.RowHeader
import cat.mvmike.minimalcalendarwidget.domain.intent.toActionableView
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver

object ProcessIntentUseCase {

    fun execute(
        context: Context,
        intent: Intent
    ) = when (intent.toActionableView()) {
        ConfigurationIcon -> startConfigurationActivity(context)
        MonthAndYearHeader,
        RowHeader -> startCalendarActivity(context)
        CellDay -> startCalendarActivity(context, intent)
        else -> null
    }?.let { context.executeAndRedrawOrAskForPermissions(it) }

    private fun startConfigurationActivity(context: Context): () -> Unit = {
        ConfigurationActivity.start(context)
    }

    private fun startCalendarActivity(context: Context): () -> Unit = {
        CalendarActivity.start(context, SystemResolver.getSystemInstant())
    }

    private fun startCalendarActivity(
        context: Context,
        intent: Intent
    ): () -> Unit = {
        CalendarActivity.start(
            context,
            when {
                BooleanConfigurationItem.OpenCalendarOnClickedDay.get(context) -> intent.getExtraInstant()
                else -> SystemResolver.getSystemInstant()
            }
        )
    }

    private fun Context.executeAndRedrawOrAskForPermissions(function: () -> Unit) =
        when (CalendarResolver.isReadCalendarPermitted(this)) {
            true -> {
                function.invoke()
                RedrawWidgetUseCase.execute(this)
            }
            else -> PermissionsActivity.start(this)
        }
}