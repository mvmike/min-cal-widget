// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.activity;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;

public final class PermissionsActivity extends Activity {

    private static final int READ_CALENDAR_PERM = 225;

    @Override
    protected void onStart() {

        super.onStart();
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CALENDAR}, READ_CALENDAR_PERM);
    }
}
