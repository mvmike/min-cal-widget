// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.application.RedrawWidgetUseCase

class CalendarChangeReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) = when (intent.action) {
        Intent.ACTION_TIME_CHANGED,
        Intent.ACTION_DATE_CHANGED,
        Intent.ACTION_TIMEZONE_CHANGED,
        Intent.ACTION_PROVIDER_CHANGED -> RedrawWidgetUseCase.execute(context)
        else -> {}
    }
}