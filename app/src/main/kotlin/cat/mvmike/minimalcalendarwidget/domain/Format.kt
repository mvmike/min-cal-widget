// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

enum class Format(
    private val minWidth: Int,
    private val minHeight: Int,
    private val monthHeaderLabelLength: Int,
    private val dayHeaderLabelLength: Int,
    val dayCellValueRelativeSize: Float
) {
    STANDARD(
        minWidth = 180,
        minHeight = 70,
        monthHeaderLabelLength = Int.MAX_VALUE,
        dayHeaderLabelLength = 3,
        dayCellValueRelativeSize = 1f
    ),
    REDUCED(
        minWidth = 0,
        minHeight = 0,
        monthHeaderLabelLength = 3,
        dayHeaderLabelLength = 1,
        dayCellValueRelativeSize = 0.6f
    );

    fun fitsSize(width: Int, height: Int) = minWidth <= width && minHeight <= height

    fun getMonthHeaderLabel(value: String) = value.take(monthHeaderLabelLength)

    fun getDayHeaderLabel(value: String) = value.take(dayHeaderLabelLength)


}
