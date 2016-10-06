// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.receiver;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cat.mvmike.minimalcalendarwidget.MonthWidget;

public class DateChangeReceiver extends BroadcastReceiver {

    private final Calendar lastChecked = Calendar.getInstance();

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.YEAR) != lastChecked.get(Calendar.YEAR)
                || now.get(Calendar.DAY_OF_YEAR) != lastChecked.get(Calendar.DAY_OF_YEAR)) {
            MonthWidget.forceRedraw(context);
        }

        lastChecked.setTime(now.getTime());
    }
}
