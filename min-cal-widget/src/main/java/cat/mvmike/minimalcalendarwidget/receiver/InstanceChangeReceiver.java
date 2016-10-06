// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cat.mvmike.minimalcalendarwidget.MonthWidget;

public class InstanceChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        MonthWidget.forceRedraw(context);
    }
}
