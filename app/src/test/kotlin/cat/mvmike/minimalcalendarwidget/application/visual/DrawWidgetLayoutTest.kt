// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class DrawWidgetLayoutTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndTransparencyLevels")
    fun execute(theme: Theme, transparency: Transparency, mainLayout: Int) {
        mockSharedPreferences()
        mockCalendarTheme(theme)
        mockWidgetTransparency(transparency)
        every { systemResolver.getColourAsString(context, mainLayout) } returns "expectedMainLayout"

        val layoutWithTransparency = 55555
        val mainLayoutWithTransparency = "#${transparency.getAlphaInHex(TransparencyRange.COMPLETE)}Layout"
        every { systemResolver.parseColour(mainLayoutWithTransparency) } returns 55555

        justRun {
            systemResolver.setBackgroundColor(
                context = context,
                remoteViews = widgetRv,
                viewId = R.id.main_linear_layout,
                colour = layoutWithTransparency
            )
        }

        DrawWidgetLayout.execute(context, widgetRv)

        verify {
            systemResolver.setBackgroundColor(
                context = context,
                remoteViews = widgetRv,
                viewId = R.id.main_linear_layout,
                colour = layoutWithTransparency
            )
        }

        verifyCalendarTheme()
        verifyWidgetTransparency()
        verify { systemResolver.getColourAsString(context, mainLayout) }
        verify { systemResolver.parseColour(mainLayoutWithTransparency) }
        confirmVerified(widgetRv)
    }

    companion object {

        @JvmStatic
        @Suppress("unused")
        fun getCombinationOfThemesAndTransparencyLevels() = Stream.of(
            Arguments.of(Theme.BLACK, Transparency(0), 2131034144),
            Arguments.of(Theme.BLACK, Transparency(1), 2131034144),
            Arguments.of(Theme.BLACK, Transparency(20), 2131034144),
            Arguments.of(Theme.BLACK, Transparency(50), 2131034144),
            Arguments.of(Theme.BLACK, Transparency(79), 2131034144),
            Arguments.of(Theme.BLACK, Transparency(90), 2131034144),
            Arguments.of(Theme.BLACK, Transparency(100), 2131034144),
            Arguments.of(Theme.GREY, Transparency(0), 2131034145),
            Arguments.of(Theme.GREY, Transparency(3), 2131034145),
            Arguments.of(Theme.GREY, Transparency(10), 2131034145),
            Arguments.of(Theme.GREY, Transparency(45), 2131034145),
            Arguments.of(Theme.GREY, Transparency(49), 2131034145),
            Arguments.of(Theme.GREY, Transparency(57), 2131034145),
            Arguments.of(Theme.GREY, Transparency(100), 2131034145),
            Arguments.of(Theme.WHITE, Transparency(0), 2131034146),
            Arguments.of(Theme.WHITE, Transparency(5), 2131034146),
            Arguments.of(Theme.WHITE, Transparency(70), 2131034146),
            Arguments.of(Theme.WHITE, Transparency(72), 2131034146),
            Arguments.of(Theme.WHITE, Transparency(98), 2131034146),
            Arguments.of(Theme.WHITE, Transparency(99), 2131034146),
            Arguments.of(Theme.WHITE, Transparency(100), 2131034146)
        )!!
    }
}
