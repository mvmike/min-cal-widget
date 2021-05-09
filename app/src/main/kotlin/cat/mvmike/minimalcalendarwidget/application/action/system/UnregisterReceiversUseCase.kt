// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.action.system

import android.content.Context
import android.util.Log
import cat.mvmike.minimalcalendarwidget.infrastructure.receiver.DateChangeReceiver
import cat.mvmike.minimalcalendarwidget.infrastructure.receiver.InstanceChangeReceiver

object UnregisterReceiversUseCase {

    fun execute(context: Context) {
        try {
            context.applicationContext.unregisterReceiver(DateChangeReceiver())
            context.applicationContext.unregisterReceiver(InstanceChangeReceiver())
        } catch (iae: IllegalArgumentException) {
            // if coming from old version, receiver might have not been initialized
            Log.w(UnregisterReceiversUseCase::class.java.name, iae)
        }
    }
}
