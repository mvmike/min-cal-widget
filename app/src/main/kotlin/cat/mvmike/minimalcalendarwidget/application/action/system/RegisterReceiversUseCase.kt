// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.action.system

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.CalendarContract
import cat.mvmike.minimalcalendarwidget.infrastructure.receiver.DateChangeReceiver
import cat.mvmike.minimalcalendarwidget.infrastructure.receiver.InstanceChangeReceiver

object RegisterReceiversUseCase {

    private val DATE_CHANGE_INTENT_FILTER = IntentFilter()
        .withAction(Intent.ACTION_TIME_TICK)
        .withAction(Intent.ACTION_TIMEZONE_CHANGED)
        .withAction(Intent.ACTION_TIME_CHANGED)

    private val INSTANCE_CHANGE_INTENT_FILTER = IntentFilter()
        .withAction(Intent.ACTION_PROVIDER_CHANGED)
        .withDataScheme(ContentResolver.SCHEME_CONTENT)
        .withDataAuthority(CalendarContract.AUTHORITY, null)

    fun execute(context: Context) {
        try {
            context.applicationContext.registerReceiver(DateChangeReceiver(), DATE_CHANGE_INTENT_FILTER)
            context.applicationContext.registerReceiver(InstanceChangeReceiver(), INSTANCE_CHANGE_INTENT_FILTER)
        } catch (ignored: IllegalArgumentException) {
            // receiver is already registered
        }
    }

    private fun IntentFilter.withAction(action: String): IntentFilter {
        this.addAction(action)
        return this
    }

    private fun IntentFilter.withDataScheme(scheme: String): IntentFilter {
        this.addDataScheme(scheme)
        return this
    }

    private fun IntentFilter.withDataAuthority(host: String, port: String?): IntentFilter {
        this.addDataAuthority(host, port)
        return this
    }
}
