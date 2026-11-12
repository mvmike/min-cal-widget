// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.MONTHS
import java.time.temporal.ChronoUnit.WEEKS

enum class Variant(
    val numWeeks: Int,
    val paginationTemporalUnit: ChronoUnit
) {
    MONTH(
        numWeeks = 6,
        paginationTemporalUnit = MONTHS
    ),
    WEEK(
        numWeeks = 1,
        paginationTemporalUnit = WEEKS
    )
}