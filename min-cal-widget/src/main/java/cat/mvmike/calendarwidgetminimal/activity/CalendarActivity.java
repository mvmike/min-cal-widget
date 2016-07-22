// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.calendarwidgetminimal.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;

public final class CalendarActivity {

    private static final String TIME_APPEND_PATH = "time";

    public static void startCalendarApplication(final Context context) {

        long startMillis = System.currentTimeMillis();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath(TIME_APPEND_PATH);
        ContentUris.appendId(builder, startMillis);

        Intent calendarIntent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        calendarIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(calendarIntent);
    }
}
