// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import cat.mvmike.minimalcalendarwidget.receiver.DateChangeReceiver;

public class CalendarChangeUtil {

    private static final DateChangeReceiver DATE_CHANGE_RECEIVER = new DateChangeReceiver();

    private static final IntentFilter DATE_CHANGE_INTENT_FILTER;

    static {
        DATE_CHANGE_INTENT_FILTER = new IntentFilter();
        DATE_CHANGE_INTENT_FILTER.addAction(Intent.ACTION_TIME_TICK);
        DATE_CHANGE_INTENT_FILTER.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        DATE_CHANGE_INTENT_FILTER.addAction(Intent.ACTION_TIME_CHANGED);
    }

    public static void registerDateChangeReceiver(final Context context) {
        context.getApplicationContext().registerReceiver(DATE_CHANGE_RECEIVER, DATE_CHANGE_INTENT_FILTER);
    }

    public static void unregisterDateChangeReceiver(final Context context) {
        context.getApplicationContext().unregisterReceiver(DATE_CHANGE_RECEIVER);
    }
}
