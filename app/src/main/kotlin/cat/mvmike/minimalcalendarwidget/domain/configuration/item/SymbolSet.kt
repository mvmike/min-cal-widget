// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

private const val INSTANCES_SYMBOLS_EMPTY = ' '

enum class SymbolSet(
    val relativeSize: Float = 1f,
    private val values: CharArray
) {
    MINIMAL(
        values = charArrayOf('·', '∶', '∴', '∷', '◇', '◈')
    ),
    VERTICAL(
        values = charArrayOf('·', '∶', '⁝', '⁞', '|')
    ),
    CIRCLES(
        values = charArrayOf('◔', '◑', '◕', '●', '๑')
    ),
    BLOCKS(
        values = charArrayOf('▁', '▂', '▃', '▄', '▅')
    ),
    NUMBERS(
        relativeSize = 0.6f,
        values = charArrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '+')
    ),
    ROMAN(
        relativeSize = 0.6f,
        values = charArrayOf('Ⅰ', 'Ⅱ', 'Ⅲ', 'Ⅳ', 'Ⅴ', 'Ⅵ', 'Ⅶ', 'Ⅷ', 'Ⅸ', 'Ⅹ', '∾')
    ),
    BINARY(
        relativeSize = 0.8f,
        values = charArrayOf('☱', '☲', '☳', '☴', '☵', '☶', '☷', '※')
    ),
    NONE(
        values = charArrayOf(' ')
    );

    private fun getMaxValue() = values.size - 1

    fun get(numOfInstances: Int): Char = when (numOfInstances) {
        0 -> INSTANCES_SYMBOLS_EMPTY
        in 1..getMaxValue() -> values[numOfInstances - 1]
        else -> values[getMaxValue()]
    }

    fun getDisplayValue() = values.joinToString(" ")
}

fun getSymbolSetDisplayValues() =
    SymbolSet.entries.map { it.getDisplayValue() }