package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.text.Layout
import java.time.DayOfWeek

data class Cell(
    val text: String,
    val colour: Int,
    val relativeSize: Float,
    val background: Int? = null,
    val alignment: Layout.Alignment? = null,
    val bold: Boolean = false,
    val highlightDrawable: Int? = null
)

data class CellTheme(
    val textColour: Int,
    val background: Int? = null
)

data class CellThemePack(
    val textColour: Int,
    val weekdayBackground: Int? = null,
    val saturdayBackground: Int? = null,
    val sundayBackground: Int? = null
) {
    fun get(dayOfWeek: DayOfWeek) = CellTheme(
        textColour = textColour,
        background = when (dayOfWeek) {
            DayOfWeek.SATURDAY -> saturdayBackground
            DayOfWeek.SUNDAY -> sundayBackground
            else -> weekdayBackground
        }
    )
}

data class CellHighlightDrawableThemePack(
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