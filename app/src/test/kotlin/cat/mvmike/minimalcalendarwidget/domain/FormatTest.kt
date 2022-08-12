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
    fun getFormat_shouldReturnExpectedFormatBasedOnCurrentWidth(formatTestProperties: FormatTestProperties) {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { context.resources.configuration } returns Configuration()
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) } returns formatTestProperties.width

        val result = getFormat(context, appWidgetManager, appWidgetId)

        assertThat(result).isEqualTo(formatTestProperties.expectedFormat)
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
    private fun getWidgetSizeAndExpectedFormat(): Stream<FormatTestProperties> = Stream.of(
        FormatTestProperties(261, Format(dayCellTextRelativeSize = 1.2f)),
        FormatTestProperties(260, Format(dayCellTextRelativeSize = 1.2f)),
        FormatTestProperties(259, Format(dayCellTextRelativeSize = 1.1f)),
        FormatTestProperties(241, Format(dayCellTextRelativeSize = 1.1f)),
        FormatTestProperties(240, Format(dayCellTextRelativeSize = 1.1f)),
        FormatTestProperties(239, Format()),
        FormatTestProperties(221, Format()),
        FormatTestProperties(220, Format()),
        FormatTestProperties(219, Format(headerTextRelativeSize = 0.9f, dayCellTextRelativeSize = 0.9f)),
        FormatTestProperties(201, Format(headerTextRelativeSize = 0.9f, dayCellTextRelativeSize = 0.9f)),
        FormatTestProperties(200, Format(headerTextRelativeSize = 0.9f, dayCellTextRelativeSize = 0.9f)),
        FormatTestProperties(199, Format(headerTextRelativeSize = 0.8f, dayCellTextRelativeSize = 0.8f)),
        FormatTestProperties(181, Format(headerTextRelativeSize = 0.8f, dayCellTextRelativeSize = 0.8f)),
        FormatTestProperties(180, Format(headerTextRelativeSize = 0.8f, dayCellTextRelativeSize = 0.8f)),
        FormatTestProperties(179, Format(monthHeaderLabelLength = 3, dayHeaderLabelLength = 1, headerTextRelativeSize = 0.8f, dayCellTextRelativeSize = 0.8f)),
        FormatTestProperties(0, Format(monthHeaderLabelLength = 3, dayHeaderLabelLength = 1, headerTextRelativeSize = 0.8f, dayCellTextRelativeSize = 0.8f)),
        FormatTestProperties(-1, Format(monthHeaderLabelLength = 3, dayHeaderLabelLength = 1, headerTextRelativeSize = 0.8f, dayCellTextRelativeSize = 0.8f)),
        FormatTestProperties(-320, Format(monthHeaderLabelLength = 3, dayHeaderLabelLength = 1, headerTextRelativeSize = 0.8f, dayCellTextRelativeSize = 0.8f))
    )

    internal data class FormatTestProperties(
        val width: Int,
        val expectedFormat: Format
    )
}
