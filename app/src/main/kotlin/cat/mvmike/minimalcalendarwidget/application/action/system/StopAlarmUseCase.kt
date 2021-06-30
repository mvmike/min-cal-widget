// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.action.system

import android.content.Context
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate

object StopAlarmUseCase {

    fun execute(context: Context) {
        AutoUpdate.cancelAlarm(context)
    }
}
