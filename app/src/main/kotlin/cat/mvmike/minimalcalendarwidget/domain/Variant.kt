// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

enum class Variant(
    val numWeeks: Int
) {
    MONTH(6),
    WEEK(1)
}