// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import cat.mvmike.minimalcalendarwidget.activity.CalendarActivity;
import cat.mvmike.minimalcalendarwidget.activity.PermissionsActivity;
import cat.mvmike.minimalcalendarwidget.resolver.CalendarResolver;
import cat.mvmike.minimalcalendarwidget.resolver.dto.InstanceDto;
import cat.mvmike.minimalcalendarwidget.util.ConfigurationUtil;
import cat.mvmike.minimalcalendarwidget.util.DayUtil;
import cat.mvmike.minimalcalendarwidget.util.ReceiverUtil;
import cat.mvmike.minimalcalendarwidget.util.ThemesUtil;
import cat.mvmike.minimalcalendarwidget.util.WeekDayHeaderUtil;

public class MonthWidget extends AppWidgetProvider {

    private static final int INTENT_CODE_CONFIGURATION = 98;

    private static final int INTENT_CODE_CALENDAR = 99;

    private static final String WIDGET_PRESS = "action.WIDGET_PRESS";

    private static final String CONFIGURATION_PRESS = "action.WIDGET_CONFIGURATION";

    private static final String MONTH_FORMAT = "MMMM";

    private static final String YEAR_FORMAT = "yyyy";

    private static final String HEADER_DATE_FORMAT = MONTH_FORMAT + " " + YEAR_FORMAT;

    private static final float HEADER_RELATIVE_YEAR_SIZE = 0.8f;

    private static void drawWidgets(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds,
                                    final RemoteViews rv) {

        for (int appWidgetId : appWidgetIds) {
            drawWidget(context, appWidgetManager, appWidgetId, rv);
        }
    }

    private static void drawWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId,
                                   final RemoteViews rv) {

        Calendar cal = Calendar.getInstance();
        int firstDayOfWeek = ConfigurationUtil.getStartWeekDay(context);

        Calendar[] safeDateSpan = CalendarResolver.getSafeDateSpan(cal);
        Set<InstanceDto> instanceSet = PermissionsActivity.isPermitted(context) ?
            CalendarResolver.readAllInstances(context.getContentResolver(), safeDateSpan[0], safeDateSpan[1]) : new HashSet<>();

        // SET MONTH AND YEAR
        String monthAndYear = String.valueOf(DateFormat.format(HEADER_DATE_FORMAT, cal));
        SpannableString ss = new SpannableString(monthAndYear);
        ss.setSpan(new RelativeSizeSpan(HEADER_RELATIVE_YEAR_SIZE), monthAndYear.length() - YEAR_FORMAT.length(), monthAndYear.length(), 0);
        rv.setTextViewText(R.id.month_year_label, ss);

        rv.removeAllViews(R.id.calendar_widget);

        // SET DAYS OF WEEK (HEADERS)
        RemoteViews headerRowRv = new RemoteViews(context.getPackageName(), R.layout.row_header);
        WeekDayHeaderUtil.setCellHeaderWeekDays(headerRowRv, firstDayOfWeek, context);
        rv.addView(R.id.calendar_widget, headerRowRv);

        // SET INDIVIDUAL DAYS
        DayUtil.setDays(context, cal, firstDayOfWeek, ss, rv, instanceSet);

        // LISTENER FOR WIDGET PRESS AND CONFIGURATION
        rv.setOnClickPendingIntent(R.id.calendar_widget, PendingIntent.getBroadcast(context, INTENT_CODE_CALENDAR,
            new Intent(context, MonthWidget.class).setAction(WIDGET_PRESS), PendingIntent.FLAG_UPDATE_CURRENT));
        rv.setOnClickPendingIntent(R.id.configuration_icon, PendingIntent.getBroadcast(context, INTENT_CODE_CONFIGURATION,
            new Intent(context, MonthWidget.class).setAction(CONFIGURATION_PRESS), PendingIntent.FLAG_UPDATE_CURRENT));

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    public static void forceRedraw(final Context context) {
        forceRedraw(context, ConfigurationUtil.getTheme(context));
    }

    private static void forceRedraw(final Context context, final ThemesUtil.Theme theme) {

        if (!PermissionsActivity.isPermitted(context)) {
            return;
        }

        RemoteViews rv = new RemoteViews(context.getPackageName(), theme.getMainLayout());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName name = new ComponentName(context, MonthWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(name);

        drawWidgets(context, appWidgetManager, appWidgetIds, rv);
    }

    @Override
    public void onEnabled(final Context context) {

        super.onEnabled(context);
        ReceiverUtil.registerReceivers(context);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        RemoteViews rv = new RemoteViews(context.getPackageName(), ConfigurationUtil.getTheme(context).getMainLayout());
        drawWidgets(context, appWidgetManager, appWidgetIds, rv);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        super.onReceive(context, intent);

        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {

                case WIDGET_PRESS:
                    CalendarActivity.startCalendarApplication(context);
                    break;

                case CONFIGURATION_PRESS:
                    ConfigurationUtil.startConfigurationView(context);
                    break;
            }
        }

        forceRedraw(context);
    }

    @Override
    public void onDeleted(final Context context, final int[] appWidgetIds) {

        super.onDeleted(context, appWidgetIds);
        ConfigurationUtil.clearConfiguration(context);
        ReceiverUtil.unregisterReceivers(context);
    }
}
