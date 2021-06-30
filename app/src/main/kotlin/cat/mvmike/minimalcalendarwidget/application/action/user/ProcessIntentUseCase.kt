// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.action.user

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.content.Context
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate.ACTION_AUTO_UPDATE
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity

object ProcessIntentUseCase {

    fun execute(context: Context, action: String?) {

        when (action){
            ACTION_AUTO_UPDATE,
            ACTION_APPWIDGET_UPDATE,
            null -> return
        }

        if (!SystemResolver.get().isReadCalendarPermitted(context)) {
            PermissionsActivity.start(context)
            return
        }

        when (action) {
            ActionableView.OPEN_CONFIGURATION.action -> ConfigurationActivity.start(context)
            ActionableView.OPEN_CALENDAR.action -> CalendarActivity.start(context)
        }
    }
}
