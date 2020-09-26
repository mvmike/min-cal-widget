// Copyright (c) 2020, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.infrastructure;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import cat.mvmike.minimalcalendarwidget.application.activity.PermissionsActivity;

public final class PermissionService {

    public static boolean hasPermissions(final Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    public static void launchPermissionsActivity(final Context context) {

        Intent permissionIntent = new Intent(context, PermissionsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(permissionIntent);
    }

}
