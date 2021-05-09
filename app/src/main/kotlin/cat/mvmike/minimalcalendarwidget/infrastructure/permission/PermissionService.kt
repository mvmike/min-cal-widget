// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.permission

import android.Manifest.permission.READ_CALENDAR
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat.checkSelfPermission
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity

object PermissionService {

    fun hasPermissions(context: Context) = checkSelfPermission(context, READ_CALENDAR) == PERMISSION_GRANTED

    fun launchPermissionsActivity(context: Context) =
        context.startActivity(
            Intent(context, PermissionsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
}
