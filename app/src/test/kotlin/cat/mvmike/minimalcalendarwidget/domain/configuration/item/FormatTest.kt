// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

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
import org.junit.jupiter.params.provider.ValueSource
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

        val result = getFormat(context, appWidgetManager, appWidgetId)!!

        val headerLabel = "someLongHeaderLabel"
        assertThat(result.getMonthHeaderLabel(headerLabel)).isEqualTo(headerLabel.take(formatTestProperties.expectedMonthHeaderLabelLength))
        assertThat(result.getDayHeaderLabel(headerLabel)).isEqualTo(headerLabel.take(formatTestProperties.expectedDayHeaderLabelLength))
        assertThat(result.headerTextRelativeSize).isEqualTo(formatTestProperties.expectedHeaderTextRelativeSize)
        assertThat(result.dayCellTextRelativeSize).isEqualTo(formatTestProperties.expectedDayCellTextRelativeSize)
        verify { context.resources.configuration }
        verify { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) }
        confirmVerified(bundle)
    }

    @Test
    fun getFormat_shouldReturnNullWhenAnExceptionIsThrown_whenGettingBundle() {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } throws Exception()

        val result = getFormat(context, appWidgetManager, appWidgetId)

        assertThat(result).isNull()
        confirmVerified(bundle)
    }

    @Test
    fun getFormat_shouldReturnNullWhenAnExceptionIsThrown_whenGettingWidth() {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { context.resources.configuration } returns Configuration()
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) } throws Exception()

        val result = getFormat(context, appWidgetManager, appWidgetId)

        assertThat(result).isNull()
        verify { context.resources.configuration }
        verify { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) }
        confirmVerified(bundle)
    }

    @Test
    fun getFormat_shouldReturnNullWhenAnExceptionIsThrown_whenGettingOrientation() {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { context.resources.configuration } throws Exception()

        val result = getFormat(context, appWidgetManager, appWidgetId)

        assertThat(result).isNull()
        verify { context.resources.configuration }
        confirmVerified(bundle)
    }

    @ParameterizedTest
    @ValueSource(ints = [-500, -1, 0])
    fun getFormat_shouldReturnNullWhenInvalidWidth(width: Int) {
        every { appWidgetManager.getAppWidgetOptions(appWidgetId) } returns bundle
        every { context.resources.configuration } returns Configuration()
        every { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) } returns width

        val result = getFormat(context, appWidgetManager, appWidgetId)

        assertThat(result).isNull()
        verify { context.resources.configuration }
        verify { bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) }
        confirmVerified(bundle)
    }

    @Suppress("UnusedPrivateMember")
    private fun getWidgetSizeAndExpectedFormat(): Stream<FormatTestProperties> = Stream.of(
        FormatTestProperties(width = 261, expectedDayCellTextRelativeSize = 1.2f),
        FormatTestProperties(width = 260, expectedDayCellTextRelativeSize = 1.2f),
        FormatTestProperties(width = 259, expectedDayCellTextRelativeSize = 1.1f),
        FormatTestProperties(width = 241, expectedDayCellTextRelativeSize = 1.1f),
        FormatTestProperties(width = 240, expectedDayCellTextRelativeSize = 1.1f),
        FormatTestProperties(width = 239),
        FormatTestProperties(width = 221),
        FormatTestProperties(width = 220),
        FormatTestProperties(width = 219, expectedHeaderTextRelativeSize = 0.9f, expectedDayCellTextRelativeSize = 0.9f),
        FormatTestProperties(width = 201, expectedHeaderTextRelativeSize = 0.9f, expectedDayCellTextRelativeSize = 0.9f),
        FormatTestProperties(width = 200, expectedHeaderTextRelativeSize = 0.9f, expectedDayCellTextRelativeSize = 0.9f),
        FormatTestProperties(width = 199, expectedHeaderTextRelativeSize = 0.8f, expectedDayCellTextRelativeSize = 0.8f),
        FormatTestProperties(width = 181, expectedHeaderTextRelativeSize = 0.8f, expectedDayCellTextRelativeSize = 0.8f),
        FormatTestProperties(width = 180, expectedHeaderTextRelativeSize = 0.8f, expectedDayCellTextRelativeSize = 0.8f),
        FormatTestProperties(
            width = 179,
            expectedMonthHeaderLabelLength = 3,
            expectedDayHeaderLabelLength = 1,
            expectedHeaderTextRelativeSize = 0.8f,
            expectedDayCellTextRelativeSize = 0.8f
        ),
        FormatTestProperties(
            width = 150,
            expectedMonthHeaderLabelLength = 3,
            expectedDayHeaderLabelLength = 1,
            expectedHeaderTextRelativeSize = 0.8f,
            expectedDayCellTextRelativeSize = 0.8f
        ),
        FormatTestProperties(
            width = 1,
            expectedMonthHeaderLabelLength = 3,
            expectedDayHeaderLabelLength = 1,
            expectedHeaderTextRelativeSize = 0.8f,
            expectedDayCellTextRelativeSize = 0.8f
        )
    )

    internal data class FormatTestProperties(
        val width: Int,
        val expectedMonthHeaderLabelLength: Int = Int.MAX_VALUE,
        val expectedDayHeaderLabelLength: Int = 3,
        val expectedHeaderTextRelativeSize: Float = 1f,
        val expectedDayCellTextRelativeSize: Float = 1f
    )
}
