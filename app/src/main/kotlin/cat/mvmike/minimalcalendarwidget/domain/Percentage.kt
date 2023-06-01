package cat.mvmike.minimalcalendarwidget.domain

val PERCENTAGE_RANGE = 0..100

open class Percentage(
    val value: Int
) {
    init {
        require(value in PERCENTAGE_RANGE)
    }
}