// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import cat.mvmike.minimalcalendarwidget.MonthWidget

private const val READ_CALENDAR_PERM = 225

class PermissionsActivity : Activity() {

    override fun onStart() {
        super.onStart()
        setResult(RESULT_CANCELED)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR), READ_CALENDAR_PERM)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode.isReadCalendarPermission() && grantResults.isPermissionGranted()) {
            setResult(RESULT_OK)
            MonthWidget.forceRedraw(this)
        }
        finish()
    }

    private fun Int.isReadCalendarPermission() = this == READ_CALENDAR_PERM

    private fun IntArray.isPermissionGranted() =
        this.size == 1 && this[0] == PackageManager.PERMISSION_GRANTED
}
