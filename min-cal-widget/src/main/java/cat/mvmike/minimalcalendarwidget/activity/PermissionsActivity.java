// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import cat.mvmike.minimalcalendarwidget.MonthWidget;

public final class PermissionsActivity extends Activity {

    private static final int READ_CALENDAR_PERM = 225;

    public static boolean isPermitted(final Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onStart() {

        super.onStart();

        setResult(Activity.RESULT_CANCELED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, READ_CALENDAR_PERM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_CALENDAR_PERM && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setResult(Activity.RESULT_OK);
            MonthWidget.forceRedraw(this);
        }

        this.finish();
    }
}
