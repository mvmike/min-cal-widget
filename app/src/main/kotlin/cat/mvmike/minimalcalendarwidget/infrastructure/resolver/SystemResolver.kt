// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.os.Build
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

object SystemResolver {

    fun getRuntimeSDK() = Build.VERSION.SDK_INT

    fun getSystemInstant() = Clock.systemUTC().instant()!!

    fun getSystemLocalDate() = LocalDate.now(Clock.systemDefaultZone())!!

    fun getSystemZoneId() = ZoneId.systemDefault()!!
}