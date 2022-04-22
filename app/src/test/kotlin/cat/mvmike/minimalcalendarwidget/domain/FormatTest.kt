// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import android.appwidget.AppWidgetManager
import android.os.Bundle
import cat.mvmike.minimalcalendarwidget.BaseTest
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class FormatTest : BaseTest() {

    private val appWidgetManager = mockk<AppWidgetManager>()

    private val bundle = mockk<Bundle>()

    private val appWidgetId = 2304985

    @Suppress("unused")
    private fun getWidgetCurrentSizeAndExpectedOutput(): Stream<GetWidgetSizeUseCaseTestProperties> = Stream.of(
        GetWidgetSizeUseCaseTestProperties(
            width = 180,
            height = 120,
            expectedSize = Format.STANDARD
        ),
        GetWidgetSizeUseCaseTestProperties(
            width = 179,
            height = 120,
            expectedSize = Format.REDUCED
        )
    )

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

    @Suppress("unused")
    private fun getWidgetCurrentFormatAndExpectedOutput(): Stream<FormatTestProperties> = Stream.of(
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

    @ParameterizedTest
    @MethodSource("getWidgetCurrentSizeAndExpectedOutput")
    fun getFormat(getWidgetSizeUseCaseTestProperties: GetWidgetSizeUseCaseTestProperties) {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) } returns getWidgetSizeUseCaseTestProperties.width
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) } returns getWidgetSizeUseCaseTestProperties.height

        val result = getFormat(appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(getWidgetSizeUseCaseTestProperties.expectedSize)
    }

    @Test
    fun getFormat_shouldReturnStandardWhenAnExceptionIsThrown_whenGettingBundle() {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } throws Exception()

        val result = getFormat(appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(Format.STANDARD)
    }

    @Test
    fun getFormat_shouldReturnStandardWhenAnExceptionIsThrown_whenGettingWidth() {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) } throws Exception()

        val result = getFormat(appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(Format.STANDARD)
    }

    @Test
    fun getFormat_shouldReturnStandardWhenAnExceptionIsThrown_whenGettingHeight() {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) } returns 200
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) } throws Exception()

        val result = getFormat(appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(Format.STANDARD)
    }

    internal data class FormatTestProperties(
        val width: Int,
        val height: Int,
        val format: Format,
        val shouldFitSize: Boolean
    )

    internal data class GetWidgetSizeUseCaseTestProperties(
        val width: Int,
        val height: Int,
        val expectedSize: Format
    )
}
