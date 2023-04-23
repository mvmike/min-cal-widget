// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.domain.MAX_PERCENTAGE
import cat.mvmike.minimalcalendarwidget.domain.Percentage
import java.math.RoundingMode

private const val MIN_RELATIVE_VALUE = 0.5f
private const val MAX_RELATIVE_VALUE = 2.0f

data class TextSize(
    val percentage: Int
) : Percentage(percentage) {

    val monthHeaderLabelLength: Int = when {
        percentage < 30 -> 3
        else -> Int.MAX_VALUE
    }

    val dayHeaderLabelLength: Int = when {
        percentage < 30 -> 1
        else -> 3
    }

    val relativeValue: Float = (
        MIN_RELATIVE_VALUE +
            ((MAX_RELATIVE_VALUE - MIN_RELATIVE_VALUE) / MAX_PERCENTAGE) * percentage
        ).rounded(3)
}

private fun Float.rounded(decimalPlaces: Int) =
    this.toBigDecimal().setScale(decimalPlaces, RoundingMode.HALF_EVEN).toFloat()