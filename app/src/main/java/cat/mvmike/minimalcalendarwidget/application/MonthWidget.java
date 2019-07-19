// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.application;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import cat.mvmike.minimalcalendarwidget.R;
import cat.mvmike.minimalcalendarwidget.infrastructure.IntentService;
import cat.mvmike.minimalcalendarwidget.infrastructure.ReceiverService;
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver;
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService;
import cat.mvmike.minimalcalendarwidget.domain.header.DayHeaderService;
import cat.mvmike.minimalcalendarwidget.domain.entry.DayService;
import cat.mvmike.minimalcalendarwidget.domain.header.MonthYearHeaderService;

public final class MonthWidget extends AppWidgetProvider {

    public static void forceRedraw(final Context context) {

        if (!SystemResolver.get().isReadCalendarPermitted(context)) {
            return;
        }

        ComponentName name = new ComponentName(context, MonthWidget.class);
        RemoteViews rv = new RemoteViews(context.getPackageName(), ConfigurationService.getTheme(context).getMainLayout());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        drawWidgets(context, appWidgetManager, appWidgetManager.getAppWidgetIds(name), rv);
    }

    @Override
    public void onEnabled(final Context context) {

        super.onEnabled(context);
        ReceiverService.registerReceivers(context);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        RemoteViews rv = new RemoteViews(context.getPackageName(), ConfigurationService.getTheme(context).getMainLayout());
        drawWidgets(context, appWidgetManager, appWidgetIds, rv);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        super.onReceive(context, intent);
        IntentService.processIntent(context, intent);
        forceRedraw(context);
    }

    @Override
    public void onDeleted(final Context context, final int[] appWidgetIds) {

        super.onDeleted(context, appWidgetIds);
        ConfigurationService.clearConfiguration(context);
        ReceiverService.unregisterReceivers(context);
    }

    private static void drawWidgets(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds, final RemoteViews remoteViews) {

        for (int appWidgetId : appWidgetIds) {
            drawWidget(context, appWidgetManager, appWidgetId, remoteViews);
        }
    }

    private static void drawWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId, final RemoteViews widgetRemoteView) {

        // SET MONTH AND YEAR
        MonthYearHeaderService.setMonthYearHeader(context, widgetRemoteView);

        // SET DAY HEADERS AND DAYS
        widgetRemoteView.removeAllViews(R.id.calendar_widget);
        DayHeaderService.setDayHeaders(context, widgetRemoteView);
        DayService.setDays(context, widgetRemoteView);

        // LISTENER FOR WIDGET PRESS AND CONFIGURATION
        IntentService.addListeners(context, widgetRemoteView);

        appWidgetManager.updateAppWidget(appWidgetId, widgetRemoteView);
    }
}
