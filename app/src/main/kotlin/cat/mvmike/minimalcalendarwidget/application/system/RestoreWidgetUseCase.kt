// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.system

import android.content.Context
import cat.mvmike.minimalcalendarwidget.domain.configuration.moveConfiguration

object RestoreWidgetUseCase {

    fun execute(context: Context, oldAppWidgetIds: IntArray, newAppWidgetIds: IntArray) {
        moveConfiguration(context, oldAppWidgetIds, newAppWidgetIds)
    }
}