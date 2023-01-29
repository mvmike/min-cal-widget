// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.user

import android.content.Context
import android.content.Intent
import android.os.Build
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.OPEN_CALENDAR
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView.OPEN_CONFIGURATION
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver.getRuntimeSDK
import java.io.Serializable
import java.time.Instant

object ProcessIntentUseCase {

    fun execute(context: Context, intent: Intent) = when {
        intent.action == OPEN_CONFIGURATION.action -> { { ConfigurationActivity.start(context) } }
        intent.action?.startsWith(OPEN_CALENDAR.action) ?: false -> { { CalendarActivity.start(context, intent.supportGetSerializableExtra("extra", Instant::class.java)) } }
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

    @Suppress("UNCHECKED_CAST", "DEPRECATION", "NewApi")
    private fun <T: Serializable>Intent.supportGetSerializableExtra(name: String, clazz: Class<T>): T {
        return if (getRuntimeSDK() >= Build.VERSION_CODES.TIRAMISU) {
            getSerializableExtra(name, clazz)!!
        } else {
            getSerializableExtra(name) as T
        }
    }
}
