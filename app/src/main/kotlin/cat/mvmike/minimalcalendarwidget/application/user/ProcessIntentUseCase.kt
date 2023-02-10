// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.user

import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.ConfigurationIcon
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.MonthAndYearHeader
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.RowHeader
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.CellDay
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.CellDay.getExtraInstant
import cat.mvmike.minimalcalendarwidget.domain.intent.toActionableView
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver

object ProcessIntentUseCase {

    fun execute(context: Context, intent: Intent) = when (intent.toActionableView()) {
        ConfigurationIcon -> { { ConfigurationActivity.start(context) } }
        MonthAndYearHeader,
        RowHeader -> { { CalendarActivity.start(context, SystemResolver.getSystemInstant()) } }
        CellDay -> { { CalendarActivity.start(context, intent.getExtraInstant()) } }
        else -> null
    }?.let { context.executeAndRedrawOrAskForPermissions(it) }

    private fun Context.executeAndRedrawOrAskForPermissions(function: () -> Unit) =
        when (CalendarResolver.isReadCalendarPermitted(this)) {
            true -> {
                function.invoke()
                RedrawWidgetUseCase.execute(this, true)
            }
            else -> PermissionsActivity.start(this)
        }
}
