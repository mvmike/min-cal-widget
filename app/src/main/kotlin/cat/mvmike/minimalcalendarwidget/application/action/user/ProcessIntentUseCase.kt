// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.action.user

import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.permission.PermissionService

object ProcessIntentUseCase {

    fun execute(context: Context, intent: Intent) {
        if (!PermissionService.hasPermissions(context)) {
            PermissionService.launchPermissionsActivity(context)
            return
        }

        when (intent.action) {
            ActionableView.OPEN_CONFIGURATION.action -> ConfigurationActivity.start(context)
            ActionableView.OPEN_CALENDAR.action -> CalendarActivity.start(context)
        }
    }
}
