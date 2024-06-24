// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.withTransparency
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver.setAsBackground

object LayoutService {

    fun draw(
        context: Context,
        widgetRemoteView: RemoteViews,
        widgetTheme: Theme,
        transparency: Transparency
    ) {
        val backgroundColour = GraphicResolver.getColourAsString(context, widgetTheme.mainBackground)
        val transparencyRange = TransparencyRange.COMPLETE

        backgroundColour
            .withTransparency(
                transparency = transparency,
                transparencyRange = transparencyRange
            ).setAsBackground(
                remoteViews = widgetRemoteView,
                viewId = R.id.widget_layout
            )
    }
}