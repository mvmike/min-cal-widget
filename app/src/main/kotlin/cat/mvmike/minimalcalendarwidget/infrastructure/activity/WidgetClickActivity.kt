// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.activity

import android.app.Activity
import android.os.Bundle
import cat.mvmike.minimalcalendarwidget.application.user.ProcessIntentUseCase
import cat.mvmike.minimalcalendarwidget.domain.Variant
import cat.mvmike.minimalcalendarwidget.domain.Variant.MONTH
import cat.mvmike.minimalcalendarwidget.domain.Variant.WEEK

class MonthWidgetClickActivity : WidgetClickActivity(MONTH)

class WeekWidgetClickActivity : WidgetClickActivity(WEEK)

/**
 * Invisible trampoline activity that handles widget clicks since starting activities from a BroadcastReceiver is blocked as a background activity launch
 * https://developer.android.com/guide/components/activities/background-starts
 */
sealed class WidgetClickActivity(
    private val variant: Variant
) : Activity() {

    companion object {
        fun getActivityClass(variant: Variant): Class<out WidgetClickActivity> = when (variant) {
            MONTH -> MonthWidgetClickActivity::class.java
            WEEK -> WeekWidgetClickActivity::class.java
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessIntentUseCase.execute(this, variant, intent)
        finish()
    }
}