// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.content.Context
import cat.mvmike.minimalcalendarwidget.R

private const val INSTANCES_SYMBOLS_EMPTY = ' '

// https://unicode-table.com
enum class SymbolSet(
    val displayString: Int,
    val relativeSize: Float,
    private val values: CharArray
) {
    MINIMAL(
        displayString = R.string.minimal,
        relativeSize = 1.1f,
        values = charArrayOf('·', '∶', '∴', '∷', '◇', '◈')
    ),
    VERTICAL(
        displayString = R.string.vertical,
        relativeSize = 1.1f,
        values = charArrayOf('·', '∶', '⁝', '⁞', '|')
    ),
    CIRCLES(
        displayString = R.string.circles,
        relativeSize = 1.1f,
        values = charArrayOf('◔', '◑', '◕', '●', '๑')
    ),
    NUMBERS(
        displayString = R.string.numbers,
        relativeSize = 0.7f,
        values = charArrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '+')
    ),
    ROMAN(
        displayString = R.string.roman,
        relativeSize = 0.7f,
        values = charArrayOf('Ⅰ', 'Ⅱ', 'Ⅲ', 'Ⅳ', 'Ⅴ', 'Ⅵ', 'Ⅶ', 'Ⅷ', 'Ⅸ', 'Ⅹ', '∾')
    ),
    BINARY(
        displayString = R.string.binary,
        relativeSize = 1f,
        values = charArrayOf('☱', '☲', '☳', '☴', '☵', '☶', '☷', '※')
    ),
    NONE(
        displayString = R.string.none,
        relativeSize = 1f,
        values = charArrayOf(' ')
    );

    private fun getMaxValue() = values.size - 1

    fun get(numOfInstances: Int): Char = when (numOfInstances) {
        0 -> INSTANCES_SYMBOLS_EMPTY
        in 1..getMaxValue() -> values[numOfInstances - 1]
        else -> values[getMaxValue()]
    }
}

fun getSymbolSetDisplayValues(context: Context) =
    SymbolSet.values().map { symbolSet ->
        context.getString(symbolSet.displayString).replaceFirstChar { it.uppercase() }
    }.toTypedArray()
