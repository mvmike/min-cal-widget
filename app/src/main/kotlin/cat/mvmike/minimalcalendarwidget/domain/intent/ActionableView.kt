// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.intent

import android.app.PendingIntent
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.Widget
import cat.mvmike.minimalcalendarwidget.domain.Variant
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import java.time.Instant
import java.time.Instant.ofEpochSecond

private const val MINCAL_INTENT_ACTION = "action.mincal"

sealed class ActionableView(
    internal open val viewId: Int,
    open val action: String
) {

    data object ConfigurationIcon : ActionableView(
        viewId = R.id.configuration_icon,
        action = "$MINCAL_INTENT_ACTION.configuration_icon_click"
    )

    data object MonthAndYearHeader : ActionableView(
        viewId = R.id.month_and_year_header,
        action = "$MINCAL_INTENT_ACTION.month_and_year_header_click"
    )

    data object RowHeader : ActionableView(
        viewId = R.id.row,
        action = "$MINCAL_INTENT_ACTION.row_header_click"
    )

    data object CellDay : ActionableView(
        viewId = R.id.cell,
        action = "$MINCAL_INTENT_ACTION.cell_day_click"
    ) {

        override fun addListener(
            context: Context,
            remoteViews: RemoteViews,
            appWidgetId: Int,
            variant: Variant
        ) = throw UnsupportedOperationException("must call overloaded addListener method")

        fun addListener(
            context: Context,
            remoteViews: Array<RemoteViews?>,
            appWidgetId: Int,
            variant: Variant,
            startOfDay: Instant
        ) = remoteViews
            .filterNotNull()
            .forEach {
                it.setOnClickPendingIntent(
                    viewId,
                    PendingIntent.getBroadcast(
                        context,
                        appWidgetId,
                        Intent(context, Widget.getReceiverClass(variant))
                            .setAction("${getActionPrefix()}${startOfDay.epochSecond}")
                            .putExtra(EXTRA_APPWIDGET_ID, appWidgetId),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

        fun Intent.getExtraInstant(): Instant {
            val systemInstant = SystemResolver.getSystemInstant()
            val systemZoneId = SystemResolver.getSystemZoneId()
            val extraInstant = ofEpochSecond(
                action?.removePrefix(getActionPrefix())?.toLongOrNull()
                    ?: systemInstant.epochSecond
            )

            // we return the instant of the clicked day with current zonedTime
            val systemLocalDateTime = systemInstant.atZone(systemZoneId)
            return extraInstant
                .atZone(systemZoneId)
                .withHour(systemLocalDateTime.hour)
                .withMinute(systemLocalDateTime.minute)
                .withSecond(systemLocalDateTime.second)
                .toInstant()
        }

        private fun getActionPrefix() = "$action."
    }

    internal open fun addListener(
        context: Context,
        remoteViews: RemoteViews,
        appWidgetId: Int,
        variant: Variant
    ) = remoteViews.setOnClickPendingIntent(
        viewId,
        PendingIntent.getBroadcast(
            context,
            appWidgetId,
            Intent(context, Widget.getReceiverClass(variant))
                .setAction(action)
                .putExtra(EXTRA_APPWIDGET_ID, appWidgetId),
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

fun Intent.getAppWidgetId() = getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)