// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.domain.entry.getAbbreviatedDisplayValue
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import java.time.DayOfWeek
import java.util.Collections

object DrawDaysHeaderUseCase {

    fun execute(context: Context, widgetRemoteView: RemoteViews) {
        val daysHeaderRow: RemoteViews = SystemResolver.get().createDaysHeaderRow(context)
        val firstDayOfWeek = Configuration.FirstDayOfWeek.get(context)
        val theme = Configuration.CalendarTheme.get(context)

        getRotatedWeekDays(firstDayOfWeek).forEach {
            SystemResolver.get().addToDaysHeaderRow(
                context = context,
                daysHeaderRow = daysHeaderRow,
                text = it.getAbbreviatedDisplayValue(context),
                layoutId = theme.getCellHeader(it)
            )
        }

        SystemResolver.get().addToWidget(
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
