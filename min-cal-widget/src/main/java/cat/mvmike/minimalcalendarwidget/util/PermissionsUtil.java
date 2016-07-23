// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import cat.mvmike.minimalcalendarwidget.activity.PermissionsActivity;

public abstract class PermissionsUtil {

    public static void checkPermissions(final Context context) {

        if (checkPermStatus(context))
            return;

        launchPermissionsActivity(context);
    }

    public static boolean checkPermStatus(final Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    private static void launchPermissionsActivity(final Context context) {

        Intent permissionIntent = new Intent(context, PermissionsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(permissionIntent);
    }

}
