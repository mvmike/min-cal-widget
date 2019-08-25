// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.infrastructure;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.CalendarContract;
import android.util.Log;

import cat.mvmike.minimalcalendarwidget.infrastructure.receiver.DateChangeReceiver;
import cat.mvmike.minimalcalendarwidget.infrastructure.receiver.InstanceChangeReceiver;

public final class ReceiverService {

    private static final DateChangeReceiver DATE_CHANGE_RECEIVER = new DateChangeReceiver();

    private static final InstanceChangeReceiver INSTANCE_CHANGE_RECEIVER = new InstanceChangeReceiver();

    private static final IntentFilter DATE_CHANGE_INTENT_FILTER;

    private static final IntentFilter INSTANCE_CHANGE_INTENT_FILTER;

    static {

        DATE_CHANGE_INTENT_FILTER = new IntentFilter();
        DATE_CHANGE_INTENT_FILTER.addAction(Intent.ACTION_TIME_TICK);
        DATE_CHANGE_INTENT_FILTER.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        DATE_CHANGE_INTENT_FILTER.addAction(Intent.ACTION_TIME_CHANGED);

        INSTANCE_CHANGE_INTENT_FILTER = new IntentFilter();
        INSTANCE_CHANGE_INTENT_FILTER.addAction(Intent.ACTION_PROVIDER_CHANGED);
        INSTANCE_CHANGE_INTENT_FILTER.addDataScheme(ContentResolver.SCHEME_CONTENT);
        INSTANCE_CHANGE_INTENT_FILTER.addDataAuthority(CalendarContract.AUTHORITY, null);
    }

    public static void registerReceivers(final Context context) {

        context.getApplicationContext().registerReceiver(DATE_CHANGE_RECEIVER, DATE_CHANGE_INTENT_FILTER);
        context.getApplicationContext().registerReceiver(INSTANCE_CHANGE_RECEIVER, INSTANCE_CHANGE_INTENT_FILTER);
    }

    public static void unregisterReceivers(final Context context) {

        try {
            context.getApplicationContext().unregisterReceiver(DATE_CHANGE_RECEIVER);
            context.getApplicationContext().unregisterReceiver(INSTANCE_CHANGE_RECEIVER);

        } catch (IllegalArgumentException iae) {

            // if coming from old version, receiver might have not been initialized
            Log.w(ReceiverService.class.getName(), iae);
        }
    }
}
