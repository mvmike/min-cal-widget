// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

sealed class Colour {

    data class Hex(
        val value: String
    ) : Colour()

    data class Resource(
        val value: Int
    ) : Colour()
}