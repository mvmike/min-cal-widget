// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.system

import android.content.Context
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate

internal const val INTERVAL_MILLIS = 900000L // 1000*60*15 === 15'

object EnableWidgetUseCase {

    fun execute(context: Context) {
        AutoUpdate.set(
            context = context,
            intervalInMillis = INTERVAL_MILLIS
        )
    }
}
