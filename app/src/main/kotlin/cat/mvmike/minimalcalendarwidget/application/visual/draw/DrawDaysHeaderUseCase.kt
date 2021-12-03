// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual.draw

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.domain.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.withTransparency
import cat.mvmike.minimalcalendarwidget.domain.entry.getAbbreviatedDisplayValue
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import java.time.DayOfWeek
import java.util.Collections

object DrawDaysHeaderUseCase {

    fun execute(context: Context, widgetRemoteView: RemoteViews, format: Format) {
        val daysHeaderRow: RemoteViews = SystemResolver.createDaysHeaderRow(context)

        val transparency = Configuration.WidgetTransparency.get(context)
        val firstDayOfWeek = EnumConfiguration.FirstDayOfWeek.get(context)
        val theme = EnumConfiguration.WidgetTheme.get(context)

        getRotatedWeekDays(firstDayOfWeek).forEach { dayOfWeek ->
            val cellHeader = theme.getCellHeader(dayOfWeek)
            val backgroundWithTransparency = cellHeader.background
                ?.let { SystemResolver.getColourAsString(context, it) }
                ?.withTransparency(
                    transparency = transparency,
                    transparencyRange = TransparencyRange.MODERATE
                )

            SystemResolver.addToDaysHeaderRow(
                context = context,
                daysHeaderRow = daysHeaderRow,
                text = format.getDayHeaderLabel(dayOfWeek.getAbbreviatedDisplayValue(context)),
                layoutId = cellHeader.layout,
                viewId = cellHeader.id,
                dayHeaderBackgroundColour = backgroundWithTransparency
            )
        }

        SystemResolver.addToWidget(
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
