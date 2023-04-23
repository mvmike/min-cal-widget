package cat.mvmike.minimalcalendarwidget.domain

const val MIN_PERCENTAGE = 0
const val MAX_PERCENTAGE = 100

open class Percentage(
    val value: Int
) {
    init {
        require(value in MIN_PERCENTAGE..MAX_PERCENTAGE)
    }
}
