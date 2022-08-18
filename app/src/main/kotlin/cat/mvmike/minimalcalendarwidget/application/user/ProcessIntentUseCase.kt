// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.user

import android.content.Context
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.config.ClockConfig
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver

object ProcessIntentUseCase {

    fun execute(context: Context, action: String?) {
        when (action) {
            ActionableView.OPEN_CONFIGURATION.action -> context.executeAndRedrawOrAskForPermissions { ConfigurationActivity.start(context) }
            ActionableView.OPEN_CALENDAR.action -> context.executeAndRedrawOrAskForPermissions { CalendarActivity.start(context, ClockConfig.getInstant()) }
            else -> {}
        }
    }

    private fun Context.executeAndRedrawOrAskForPermissions(function: () -> Unit) =
        when (CalendarResolver.isReadCalendarPermitted(this)) {
            true -> {
                function.invoke()
                RedrawWidgetUseCase.execute(this, true)
            }
            else -> PermissionsActivity.start(this)
        }
}
