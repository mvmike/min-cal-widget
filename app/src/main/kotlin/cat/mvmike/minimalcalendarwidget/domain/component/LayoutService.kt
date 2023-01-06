// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.withTransparency
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver

object LayoutService {

    fun draw(context: Context, widgetRemoteView: RemoteViews) {
        val theme = EnumConfigurationItem.WidgetTheme.get(context)
        val backgroundColour = GraphicResolver.getColourAsString(context, theme.mainBackground)
        val transparency = ConfigurationItem.WidgetTransparency.get(context)
        val transparencyRange = TransparencyRange.COMPLETE

        GraphicResolver.setBackgroundColor(
            remoteViews = widgetRemoteView,
            viewId = R.id.widget_layout,
            colour = backgroundColour.withTransparency(
                transparency = transparency,
                transparencyRange = transparencyRange
            )
        )
    }
}
