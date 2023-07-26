// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

val PERCENTAGE_RANGE = 0..100

open class Percentage(
    val value: Int
) {
    init {
        require(value in PERCENTAGE_RANGE)
    }
}