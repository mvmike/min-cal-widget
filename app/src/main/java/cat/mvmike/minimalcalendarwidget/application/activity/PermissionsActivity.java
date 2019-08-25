// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.application.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import cat.mvmike.minimalcalendarwidget.application.MonthWidget;

public final class PermissionsActivity extends Activity {

    private static final int READ_CALENDAR_PERM = 225;

    @Override
    protected void onStart() {

        super.onStart();

        setResult(Activity.RESULT_CANCELED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, READ_CALENDAR_PERM);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {

        if (requestCode == READ_CALENDAR_PERM && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setResult(Activity.RESULT_OK);
            MonthWidget.forceRedraw(this);
        }

        this.finish();
    }
}
