// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.action.user

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView

object AddListenersUseCase {

    fun execute(context: Context, remoteViews: RemoteViews) {
        ActionableView.values().forEach {
            it.addListener(context, remoteViews)
        }
    }
}
