// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.widget.RemoteViews;

import cat.mvmike.minimalcalendarwidget.activity.PermissionsActivity;
import cat.mvmike.minimalcalendarwidget.service.DayHeaderService;
import cat.mvmike.minimalcalendarwidget.service.DayService;
import cat.mvmike.minimalcalendarwidget.service.IntentService;
import cat.mvmike.minimalcalendarwidget.service.MonthYearHeaderService;
import cat.mvmike.minimalcalendarwidget.service.ReceiverService;
import cat.mvmike.minimalcalendarwidget.service.configuration.ConfigurationService;

public final class MonthWidget extends AppWidgetProvider {

    private static void drawWidgets(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds, final RemoteViews remoteViews) {

        for (int appWidgetId : appWidgetIds) {
            drawWidget(context, appWidgetManager, appWidgetId, remoteViews);
        }
    }

    private static void drawWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId, final RemoteViews widgetRemoteView) {

        // SET MONTH AND YEAR
        SpannableString ss = MonthYearHeaderService.setMonthYearHeader(widgetRemoteView);

        // SET DAY HEADERS AND DAYS
        widgetRemoteView.removeAllViews(R.id.calendar_widget);
        DayHeaderService.setDayHeaders(context, widgetRemoteView);
        DayService.setDays(context, ss, widgetRemoteView);

        // LISTENER FOR WIDGET PRESS AND CONFIGURATION
        IntentService.addListeners(context, widgetRemoteView);

        appWidgetManager.updateAppWidget(appWidgetId, widgetRemoteView);
    }

    public static void forceRedraw(final Context context) {

        if (!PermissionsActivity.isPermitted(context)) {
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
}
