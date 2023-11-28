package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import android.text.Layout
import java.time.DayOfWeek

data class Cell(
    val id: Int,
    val layout: Int,
    val textColour: Int,
    val background: Int? = null
)

data class CellPack(
    val viewId: Int,
    val layout: Int,
    val textColour: Int,
    val weekdayBackground: Int? = null,
    val saturdayBackground: Int? = null,
    val sundayBackground: Int? = null
) {
    fun get(dayOfWeek: DayOfWeek) = Cell(
        id = viewId,
        layout = layout,
        textColour = textColour,
        background = when (dayOfWeek) {
            DayOfWeek.SATURDAY -> saturdayBackground
            DayOfWeek.SUNDAY -> sundayBackground
            else -> weekdayBackground
        }
    )
}

data class CellContent(
    val text: String,
    val colour: Int,
    val relativeSize: Float,
    val style: Int? = null,
    val alignment: Layout.Alignment? = null
)