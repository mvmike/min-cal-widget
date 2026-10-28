// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.system

import android.content.Context
import cat.mvmike.minimalcalendarwidget.domain.Variant
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate

object DisableWidgetUseCase {

    fun execute(context: Context, variant: Variant) {
        AutoUpdate.cancel(context, variant)
    }
}