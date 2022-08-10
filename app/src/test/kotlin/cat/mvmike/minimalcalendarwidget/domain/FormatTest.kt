// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import android.appwidget.AppWidgetManager
import android.content.res.Configuration
import android.os.Bundle
import cat.mvmike.minimalcalendarwidget.BaseTest
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class FormatTest : BaseTest() {

    private val appWidgetManager = mockk<AppWidgetManager>()

    private val bundle = mockk<Bundle>()

    private val appWidgetId = 2304985

    @ParameterizedTest
    @MethodSource("getWidgetSizeAndExpectedFormat")
    fun getFormat(getWidgetSizeUseCaseTestProperties: GetWidgetSizeUseCaseTestProperties) {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { context.resources.configuration } returns Configuration()
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) } returns getWidgetSizeUseCaseTestProperties.width

        val result = getFormat(context, appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(getWidgetSizeUseCaseTestProperties.expectedFormat)
        verify { context.resources.configuration }
        verify { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) }
        confirmVerified(bundle)
    }

    @Test
    fun getFormat_shouldReturnStandardWhenAnExceptionIsThrown_whenGettingBundle() {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } throws Exception()

        val result = getFormat(context, appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(Format())
        confirmVerified(bundle)
    }

    @Test
    fun getFormat_shouldReturnStandardWhenAnExceptionIsThrown_whenGettingWidth() {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { context.resources.configuration } returns Configuration()
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) } throws Exception()

        val result = getFormat(context, appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(Format())
        verify { context.resources.configuration }
        verify { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) }
        confirmVerified(bundle)
    }

    @Test
    fun getFormat_shouldReturnStandardWhenAnExceptionIsThrown_whenGettingOrientation() {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { context.resources.configuration } throws Exception()

        val result = getFormat(context, appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(Format())
        verify { context.resources.configuration }
        confirmVerified(bundle)
    }

    @Suppress("UnusedPrivateMember")
    private fun getWidgetSizeAndExpectedFormat(): Stream<GetWidgetSizeUseCaseTestProperties> = Stream.of(
        GetWidgetSizeUseCaseTestProperties(180, Format()),
        GetWidgetSizeUseCaseTestProperties(181, Format()),
        GetWidgetSizeUseCaseTestProperties(200, Format()),
        GetWidgetSizeUseCaseTestProperties(179, Format(monthHeaderLabelLength = 3, dayHeaderLabelLength = 1, dayCellValueRelativeSize = 0.6f)),
        GetWidgetSizeUseCaseTestProperties(0, Format(monthHeaderLabelLength = 3, dayHeaderLabelLength = 1, dayCellValueRelativeSize = 0.6f)),
        GetWidgetSizeUseCaseTestProperties(-1, Format(monthHeaderLabelLength = 3, dayHeaderLabelLength = 1, dayCellValueRelativeSize = 0.6f)),
        GetWidgetSizeUseCaseTestProperties(-320, Format(monthHeaderLabelLength = 3, dayHeaderLabelLength = 1, dayCellValueRelativeSize = 0.6f))
    )

    internal data class GetWidgetSizeUseCaseTestProperties(
        val width: Int,
        val expectedFormat: Format
    )
}
