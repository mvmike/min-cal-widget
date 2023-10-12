// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.domain.PERCENTAGE_RANGE
import cat.mvmike.minimalcalendarwidget.domain.Percentage
import java.math.RoundingMode

private val relativeValueRange = 0.5f..1.8f

data class TextSize(
    val percentage: Int
) : Percentage(percentage) {

    val monthHeaderLabelLength: Int = when (percentage) {
        in 0..24 -> 3
        else -> Int.MAX_VALUE
    }

    val dayHeaderLabelLength: Int = when (percentage) {
        in 0..24 -> 1
        else -> 3
    }

    val relativeValue: Float = (
        relativeValueRange.start +
            ((relativeValueRange.endInclusive - relativeValueRange.start) / PERCENTAGE_RANGE.last) * percentage
    ).rounded(3)
}

private fun Float.rounded(
    decimalPlaces: Int = 3
) = toBigDecimal()
    .setScale(decimalPlaces, RoundingMode.HALF_EVEN)
    .toFloat()