// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import android.text.Layout
import java.time.DayOfWeek

data class Cell(
    val text: String,
    val colour: Colour,
    val relativeSize: Float,
    val background: Int? = null,
    val alignment: Layout.Alignment? = null,
    val bold: Boolean = false,
    val highlightDrawable: Int? = null
)

data class CellStyle(
    val textColour: Colour,
    val background: Colour.Resource? = null
)

data class CellStylePack(
    val textColour: Colour,
    val weekdayBackground: Colour.Resource? = null,
    val saturdayBackground: Colour.Resource? = null,
    val sundayBackground: Colour.Resource? = null
) {
    fun get(dayOfWeek: DayOfWeek) = CellStyle(
        textColour = textColour,
        background = when (dayOfWeek) {
            DayOfWeek.SATURDAY -> saturdayBackground
            DayOfWeek.SUNDAY -> sundayBackground
            else -> weekdayBackground
        }
    )
}

data class CellHighlightDrawableStylePack(
    val rightSingle: Int,
    val rightDouble: Int,
    val centeredSingle: Int,
    val centeredDouble: Int
) {
    fun get(
        text: String,
        isCentered: Boolean
    ) = when (text.length) {
        1 -> when {
            isCentered -> centeredSingle
            else -> rightSingle
        }
        else -> when {
            isCentered -> centeredDouble
            else -> rightDouble
        }
    }
}