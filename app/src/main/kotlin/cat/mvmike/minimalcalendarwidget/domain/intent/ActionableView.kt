// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.MonthWidget
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import java.time.Instant
import java.time.Instant.ofEpochSecond

private const val MINCAL_INTENT_ACTION = "action.mincal"

sealed class ActionableView(
    internal open val viewId: Int,
    internal open val code: Int,
    open val action: String
) {

    data object ConfigurationIcon : ActionableView(
        viewId = R.id.configuration_icon,
        code = 90,
        action = "$MINCAL_INTENT_ACTION.configuration_icon_click"
    )

    data object MonthAndYearHeader : ActionableView(
        viewId = R.id.month_and_year_header,
        code = 91,
        action = "$MINCAL_INTENT_ACTION.month_and_year_header_click"
    )

    data object RowHeader : ActionableView(
        viewId = R.id.row_header,
        code = 92,
        action = "$MINCAL_INTENT_ACTION.row_header_click"
    )

    data object CellDay : ActionableView(
        viewId = R.id.cell_day,
        code = 93,
        action = "$MINCAL_INTENT_ACTION.cell_day_click"
    ) {

        private const val CELL_DAY_INTENT_EXTRA_NAME = "startOfDayInEpochSeconds"

        override fun addListener(
            context: Context,
            remoteViews: RemoteViews
        ) = throw UnsupportedOperationException("must call overloaded addListener method")

        fun addListener(
            context: Context,
            remoteViews: Array<RemoteViews?>,
            startOfDay: Instant
        ) = remoteViews
            .filterNotNull()
            .forEach {
                it.setOnClickPendingIntent(
                    viewId,
                    PendingIntent.getBroadcast(
                        context,
                        code,
                        Intent(context, MonthWidget::class.java)
                            .apply { action = "$action.${startOfDay.epochSecond}" }
                            .apply { putExtra(CELL_DAY_INTENT_EXTRA_NAME, startOfDay.epochSecond) },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

        fun Intent.getExtraInstant(): Instant {
            val systemInstant = SystemResolver.getSystemInstant()
            val systemZoneId = SystemResolver.getSystemZoneId()
            val extraInstant = ofEpochSecond(getLongExtra(CELL_DAY_INTENT_EXTRA_NAME, systemInstant.epochSecond))

            // we return the instant of the clicked day with current zonedTime
            val systemLocalDateTime = systemInstant.atZone(systemZoneId)
            return extraInstant
                .atZone(systemZoneId)
                .withHour(systemLocalDateTime.hour)
                .withMinute(systemLocalDateTime.minute)
                .withSecond(systemLocalDateTime.second)
                .toInstant()
        }
    }

    internal open fun addListener(
        context: Context,
        remoteViews: RemoteViews
    ) = remoteViews.setOnClickPendingIntent(
        viewId,
        PendingIntent.getBroadcast(
            context,
            code,
            Intent(context, MonthWidget::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )
}

fun Intent.toActionableView(): ActionableView? = listOf(
    ActionableView.ConfigurationIcon,
    ActionableView.MonthAndYearHeader,
    ActionableView.RowHeader
).firstOrNull { it.action == action } ?: when {
    action == null -> null
    action!!.startsWith(ActionableView.CellDay.action) -> ActionableView.CellDay
    else -> null
}