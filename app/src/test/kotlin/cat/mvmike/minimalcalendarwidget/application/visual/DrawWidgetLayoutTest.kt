// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.visual

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import java.util.Random
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class DrawWidgetLayoutTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndTransparencyLevels")
    fun execute(theme: Theme, transparency: Transparency, mainLayout: Int, expectedMainLayoutHexTransparency: String) {
        mockSharedPreferences()
        mockCalendarTheme(theme)
        mockWidgetTransparency(transparency)
        every { systemResolver.getColourAsString(context, mainLayout) } returns "expectedMainLayout"

        val layoutWithTransparency = Random().nextInt()
        val mainLayoutWithTransparency = "#${expectedMainLayoutHexTransparency}Layout"
        every { systemResolver.parseColour(mainLayoutWithTransparency) } returns layoutWithTransparency

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

        private const val blackThemeMainLayout = 2131034144
        private const val whiteThemeMainLayout = 2131034145

        @JvmStatic
        @Suppress("unused")
        fun getCombinationOfThemesAndTransparencyLevels() = Stream.of(
            Arguments.of(Theme.BLACK, Transparency(0), blackThemeMainLayout, "FF"),
            Arguments.of(Theme.BLACK, Transparency(1), blackThemeMainLayout, "FC"),
            Arguments.of(Theme.BLACK, Transparency(20), blackThemeMainLayout, "CC"),
            Arguments.of(Theme.BLACK, Transparency(50), blackThemeMainLayout, "7F"),
            Arguments.of(Theme.BLACK, Transparency(79), blackThemeMainLayout, "35"),
            Arguments.of(Theme.BLACK, Transparency(90), blackThemeMainLayout, "19"),
            Arguments.of(Theme.BLACK, Transparency(100), blackThemeMainLayout, "00"),
            Arguments.of(Theme.WHITE, Transparency(0), whiteThemeMainLayout, "FF"),
            Arguments.of(Theme.WHITE, Transparency(5), whiteThemeMainLayout, "F2"),
            Arguments.of(Theme.WHITE, Transparency(70), whiteThemeMainLayout, "4C"),
            Arguments.of(Theme.WHITE, Transparency(72), whiteThemeMainLayout, "47"),
            Arguments.of(Theme.WHITE, Transparency(98), whiteThemeMainLayout, "05"),
            Arguments.of(Theme.WHITE, Transparency(99), whiteThemeMainLayout, "02"),
            Arguments.of(Theme.WHITE, Transparency(100), whiteThemeMainLayout, "00")
        )!!
    }
}
