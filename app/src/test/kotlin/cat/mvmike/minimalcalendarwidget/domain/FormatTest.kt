// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.assertj.core.api.Assertions.assertThat
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class FormatTest : BaseTest() {

    @ParameterizedTest
    @MethodSource("getWidgetCurrentFormatAndExpectedOutput")
    fun fitsSize_shouldCheckIfItFitsItsWidthAndHeight(formatTestProperties: FormatTestProperties) {
        assertThat(
            formatTestProperties.format.fitsSize(
                width = formatTestProperties.width,
                height = formatTestProperties.height
            )
        ).isEqualTo(formatTestProperties.shouldFitSize)
    }

    companion object {

        @JvmStatic
        @Suppress("unused")
        fun getWidgetCurrentFormatAndExpectedOutput(): Stream<FormatTestProperties> = Stream.of(
            FormatTestProperties(180, 70, Format.STANDARD, true),
            FormatTestProperties(180, 70, Format.REDUCED, true),
            FormatTestProperties(180, 71, Format.STANDARD, true),
            FormatTestProperties(180, 71, Format.REDUCED, true),
            FormatTestProperties(181, 70, Format.STANDARD, true),
            FormatTestProperties(181, 70, Format.REDUCED, true),
            FormatTestProperties(200, 90, Format.STANDARD, true),
            FormatTestProperties(200, 90, Format.REDUCED, true),
            FormatTestProperties(179, 70, Format.STANDARD, false),
            FormatTestProperties(179, 70, Format.REDUCED, true),
            FormatTestProperties(180, 69, Format.STANDARD, false),
            FormatTestProperties(180, 69, Format.REDUCED, true),
            FormatTestProperties(180, 50, Format.STANDARD, false),
            FormatTestProperties(180, 50, Format.REDUCED, true),
            FormatTestProperties(0, 0, Format.STANDARD, false),
            FormatTestProperties(0, 0, Format.REDUCED, true),
            FormatTestProperties(-1, 0, Format.STANDARD, false),
            FormatTestProperties(-1, 0, Format.REDUCED, false),
            FormatTestProperties(0, -1, Format.STANDARD, false),
            FormatTestProperties(0, -1, Format.REDUCED, false),
            FormatTestProperties(-320, -180, Format.STANDARD, false),
            FormatTestProperties(-320, -180, Format.REDUCED, false),
        )
    }

    internal data class FormatTestProperties(
        val width: Int,
        val height: Int,
        val format: Format,
        val shouldFitSize: Boolean
    )
}
