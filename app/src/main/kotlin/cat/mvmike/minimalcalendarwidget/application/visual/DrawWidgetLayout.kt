// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.withTransparency
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver

object DrawWidgetLayout {

    fun execute(context: Context, widgetRemoteView: RemoteViews) {
        val theme = EnumConfiguration.WidgetTheme.get(context)
        val backgroundColour = SystemResolver.get().getColourAsString(context, theme.mainBackground)
        val transparency = Configuration.WidgetTransparency.get(context)
        val transparencyRange = TransparencyRange.COMPLETE

        SystemResolver.get().setBackgroundColor(
            context = context,
            remoteViews = widgetRemoteView,
            viewId = R.id.main_linear_layout,
            colour = backgroundColour.withTransparency(
                transparency = transparency,
                transparencyRange = transparencyRange
            )
        )
    }
}
