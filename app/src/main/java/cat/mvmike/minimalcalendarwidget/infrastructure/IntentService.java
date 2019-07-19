// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.infrastructure;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import cat.mvmike.minimalcalendarwidget.application.MonthWidget;
import cat.mvmike.minimalcalendarwidget.R;
import cat.mvmike.minimalcalendarwidget.application.activity.CalendarActivity;
import cat.mvmike.minimalcalendarwidget.application.activity.ConfigurationActivity;

public final class IntentService {

    private static final int INTENT_CODE_CONFIGURATION = 98;

    private static final int INTENT_CODE_CALENDAR = 99;

    private static final String WIDGET_PRESS = "action.WIDGET_PRESS";

    private static final String CONFIGURATION_PRESS = "action.WIDGET_CONFIGURATION";

    public static void addListeners(final Context context, final RemoteViews widgetRemoteView) {

        // for all widget → open calendar
        widgetRemoteView.setOnClickPendingIntent(R.id.calendar_widget, PendingIntent.getBroadcast(context, INTENT_CODE_CALENDAR,
            new Intent(context, MonthWidget.class).setAction(WIDGET_PRESS), PendingIntent.FLAG_UPDATE_CURRENT));

        // for configuration icon → open config
        widgetRemoteView.setOnClickPendingIntent(R.id.configuration_icon, PendingIntent.getBroadcast(context, INTENT_CODE_CONFIGURATION,
            new Intent(context, MonthWidget.class).setAction(CONFIGURATION_PRESS), PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public static void processIntent(final Context context, final Intent intent) {

        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {

                case WIDGET_PRESS:
                    CalendarActivity.start(context);
                    break;

                case CONFIGURATION_PRESS:
                    ConfigurationActivity.start(context);
                    break;
            }
        }
    }

}
