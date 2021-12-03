// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual.get

import android.appwidget.AppWidgetManager
import android.os.Bundle
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.Format
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class GetWidgetSizeUseCaseTest : BaseTest() {

    private val appWidgetManager = mockk<AppWidgetManager>()

    private val bundle = mockk<Bundle>()

    private val appWidgetId = 2304985

    @ParameterizedTest
    @MethodSource("getWidgetCurrentSizeAndExpectedOutput")
    fun execute(getWidgetSizeUseCaseTestProperties: GetWidgetSizeUseCaseTestProperties) {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) } returns getWidgetSizeUseCaseTestProperties.width
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) } returns getWidgetSizeUseCaseTestProperties.height

        val result = GetWidgetFormatUseCase.execute(appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(getWidgetSizeUseCaseTestProperties.expectedSize)
    }

    @Test
    fun execute_shouldReturnStandardWhenAnExceptionIsThrown_whenGettingBundle() {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } throws Exception()

        val result = GetWidgetFormatUseCase.execute(appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(Format.STANDARD)
    }

    @Test
    fun execute_shouldReturnStandardWhenAnExceptionIsThrown_whenGettingWidth() {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) } throws Exception()

        val result = GetWidgetFormatUseCase.execute(appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(Format.STANDARD)
    }

    @Test
    fun execute_shouldReturnStandardWhenAnExceptionIsThrown_whenGettingHeight() {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) } returns 200
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) } throws Exception()

        val result = GetWidgetFormatUseCase.execute(appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(Format.STANDARD)
    }

    companion object {

        @JvmStatic
        @Suppress("unused")
        fun getWidgetCurrentSizeAndExpectedOutput(): Stream<GetWidgetSizeUseCaseTestProperties> = Stream.of(
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
    }

    internal data class GetWidgetSizeUseCaseTestProperties(
        val width: Int,
        val height: Int,
        val expectedSize: Format
    )

}
