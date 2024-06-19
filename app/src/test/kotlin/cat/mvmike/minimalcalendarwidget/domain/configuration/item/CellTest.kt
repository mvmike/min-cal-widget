// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.CellHighlightDrawableStylePack
import cat.mvmike.minimalcalendarwidget.domain.CellStyle
import cat.mvmike.minimalcalendarwidget.domain.CellStylePack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.DayOfWeek

private const val TEXT_COLOUR = 1000000000
private const val WEEKDAY_BACKGROUND_COLOUR = 1000000001
private const val SATURDAY_BACKGROUND_COLOUR = 1000000002
private const val SUNDAY_BACKGROUND_COLOUR = 1000000003

private const val HIGHLIGHT_RIGHT_SINGLE = 2000000000
private const val HIGHLIGHT_RIGHT_DOUBLE = 2000000001
private const val HIGHLIGHT_CENTERED_SINGLE = 2000000002
private const val HIGHLIGHT_CENTERED_DOUBLE = 2000000003

internal class CellTest : BaseTest() {

    @ParameterizedTest
    @CsvSource(
        "MONDAY,$WEEKDAY_BACKGROUND_COLOUR",
        "TUESDAY,$WEEKDAY_BACKGROUND_COLOUR",
        "WEDNESDAY,$WEEKDAY_BACKGROUND_COLOUR",
        "THURSDAY,$WEEKDAY_BACKGROUND_COLOUR",
        "FRIDAY,$WEEKDAY_BACKGROUND_COLOUR",
        "SATURDAY,$SATURDAY_BACKGROUND_COLOUR",
        "SUNDAY,$SUNDAY_BACKGROUND_COLOUR"
    )
    fun getCellStylePack_shouldReturnSameTextColourAndDifferentBackground(
        dayOfWeek: DayOfWeek,
        expectedBackground: Int
    ) {
        val cellStylePack = CellStylePack(
            textColour = TEXT_COLOUR,
            weekdayBackground = WEEKDAY_BACKGROUND_COLOUR,
            saturdayBackground = SATURDAY_BACKGROUND_COLOUR,
            sundayBackground = SUNDAY_BACKGROUND_COLOUR
        )

        val result = cellStylePack.get(dayOfWeek)

        assertThat(result).isEqualTo(CellStyle(TEXT_COLOUR, expectedBackground))
    }

    @ParameterizedTest
    @CsvSource(
        "1,false,$HIGHLIGHT_RIGHT_SINGLE",
        "7,false,$HIGHLIGHT_RIGHT_SINGLE",
        "9,false,$HIGHLIGHT_RIGHT_SINGLE",
        "10,false,$HIGHLIGHT_RIGHT_DOUBLE",
        "513,false,$HIGHLIGHT_RIGHT_DOUBLE",
        "1,true,$HIGHLIGHT_CENTERED_SINGLE",
        "7,true,$HIGHLIGHT_CENTERED_SINGLE",
        "9,true,$HIGHLIGHT_CENTERED_SINGLE",
        "10,true,$HIGHLIGHT_CENTERED_DOUBLE",
        "513,true,$HIGHLIGHT_CENTERED_DOUBLE"
    )
    fun getCellHighlightDrawableStylePack(
        text: String,
        isCentered: Boolean,
        expectedResource: Int
    ) {
        val cellHighlightDrawableStylePack = CellHighlightDrawableStylePack(
            rightSingle = HIGHLIGHT_RIGHT_SINGLE,
            rightDouble = HIGHLIGHT_RIGHT_DOUBLE,
            centeredSingle = HIGHLIGHT_CENTERED_SINGLE,
            centeredDouble = HIGHLIGHT_CENTERED_DOUBLE
        )

        val result = cellHighlightDrawableStylePack.get(text, isCentered)

        assertThat(result).isEqualTo(expectedResource)
    }
}