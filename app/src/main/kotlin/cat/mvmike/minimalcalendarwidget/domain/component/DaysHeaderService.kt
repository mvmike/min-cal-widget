// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.domain.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.withTransparency
import cat.mvmike.minimalcalendarwidget.domain.getAbbreviatedDisplayValue
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import java.time.DayOfWeek
import java.util.Collections

object DaysHeaderService {

    fun draw(context: Context, widgetRemoteView: RemoteViews, format: Format) {
        val daysHeaderRow: RemoteViews = GraphicResolver.createDaysHeaderRow(context)

        val transparency = Configuration.WidgetTransparency.get(context)
        val firstDayOfWeek = EnumConfiguration.FirstDayOfWeek.get(context)
        val theme = EnumConfiguration.WidgetTheme.get(context)

        getRotatedWeekDays(firstDayOfWeek).forEach { dayOfWeek ->
            val cellHeader = theme.getCellHeader(dayOfWeek)
            val backgroundWithTransparency = cellHeader.background
                ?.let { GraphicResolver.getColourAsString(context, it) }
                ?.withTransparency(
                    transparency = transparency,
                    transparencyRange = TransparencyRange.MODERATE
                )

            GraphicResolver.addToDaysHeaderRow(
                context = context,
                daysHeaderRow = daysHeaderRow,
                text = format.getDayHeaderLabel(dayOfWeek.getAbbreviatedDisplayValue(context)),
                textColour = cellHeader.textColour,
                layoutId = cellHeader.layout,
                viewId = cellHeader.id,
                dayHeaderBackgroundColour = backgroundWithTransparency,
                textRelativeSize = format.headerTextRelativeSize
            )
        }

        GraphicResolver.addToWidget(
            widgetRemoteView = widgetRemoteView,
            remoteView = daysHeaderRow
        )
    }

    private fun getRotatedWeekDays(startDayOfWeek: DayOfWeek): List<DayOfWeek> {
        val daysOfWeek = DayOfWeek.values().toMutableList()
        Collections.rotate(daysOfWeek, -startDayOfWeek.ordinal)
        return daysOfWeek.toList()
    }
}
