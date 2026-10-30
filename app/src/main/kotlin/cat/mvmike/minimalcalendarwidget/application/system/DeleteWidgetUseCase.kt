// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.system

import android.content.Context
import cat.mvmike.minimalcalendarwidget.domain.configuration.deleteConfiguration

object DeleteWidgetUseCase {

    fun execute(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { deleteConfiguration(context, it) }
    }
}