// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.MonthWidget

class InstanceChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        MonthWidget.forceRedraw(context)
    }
}
