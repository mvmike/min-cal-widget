// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Cell
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.withTransparency
import cat.mvmike.minimalcalendarwidget.domain.getAbbreviatedDisplayValue
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import java.time.DayOfWeek
import java.util.Collections

object DaysHeaderService {

    fun draw(
        context: Context,
        widgetRemoteView: RemoteViews,
        firstDayOfWeek: DayOfWeek,
        widgetTheme: Theme,
        transparency: Transparency,
        textSize: TextSize
    ) {
        val daysHeaderRow: RemoteViews = GraphicResolver.createDaysHeaderRow(context)

        getRotatedWeekDays(firstDayOfWeek).forEach { dayOfWeek ->
            val cellHeader = widgetTheme.getCellHeader(dayOfWeek)
            val backgroundWithTransparency = cellHeader.background
                ?.let { GraphicResolver.getColourAsString(context, it) }
                ?.withTransparency(
                    transparency = transparency,
                    transparencyRange = TransparencyRange.MODERATE
                )

            GraphicResolver.addToDaysHeaderRow(
                context = context,
                daysHeaderRowRemoteView = daysHeaderRow,
                dayHeaderBackgroundColour = backgroundWithTransparency,
                cell = Cell(
                    text = dayOfWeek.getAbbreviatedDisplayValue(context).take(textSize.dayHeaderLabelLength),
                    colour = cellHeader.textColour,
                    relativeSize = textSize.relativeValue
                )
            )
        }

        GraphicResolver.addToWidget(
            widgetRemoteView = widgetRemoteView,
            remoteView = daysHeaderRow
        )
        ActionableView.RowHeader.addListener(context, widgetRemoteView)
    }

    private fun getRotatedWeekDays(startDayOfWeek: DayOfWeek): List<DayOfWeek> {
        val daysOfWeek = DayOfWeek.entries.toMutableList()
        Collections.rotate(daysOfWeek, -startDayOfWeek.ordinal)
        return daysOfWeek.toList()
    }
}