// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.os.Build

object SystemResolver {
    fun getRuntimeSDK() = Build.VERSION.SDK_INT
}
