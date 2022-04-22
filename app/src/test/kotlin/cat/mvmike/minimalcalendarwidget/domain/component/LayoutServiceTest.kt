// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.Random
import java.util.stream.Stream

private const val darkThemeMainLayout = 2131034144
private const val lightThemeMainLayout = 2131034145

internal class LayoutServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndTransparencyLevels")
    fun execute(theme: Theme, transparency: Transparency, mainLayout: Int, expectedMainLayoutHexTransparency: String) {
        mockSharedPreferences()
        mockWidgetTheme(theme)
        mockWidgetTransparency(transparency)
        every { GraphicResolver.getColourAsString(context, mainLayout) } returns "expectedMainLayout"

        val layoutWithTransparency = Random().nextInt()
        val mainLayoutWithTransparency = "#${expectedMainLayoutHexTransparency}Layout"
        every { GraphicResolver.parseColour(mainLayoutWithTransparency) } returns layoutWithTransparency

        justRun {
            GraphicResolver.setBackgroundColor(
                remoteViews = widgetRv,
                viewId = R.id.widget_layout,
                colour = layoutWithTransparency
            )
        }

        LayoutService.draw(context, widgetRv)

        verify {
            GraphicResolver.setBackgroundColor(
                remoteViews = widgetRv,
                viewId = R.id.widget_layout,
                colour = layoutWithTransparency
            )
        }

        verifyWidgetTheme()
        verifyWidgetTransparency()
        verify { GraphicResolver.getColourAsString(context, mainLayout) }
        verify { GraphicResolver.parseColour(mainLayoutWithTransparency) }
        confirmVerified(widgetRv)
    }

    @Suppress("unused")
    private fun getCombinationOfThemesAndTransparencyLevels() = Stream.of(
        Arguments.of(Theme.DARK, Transparency(0), darkThemeMainLayout, "FF"),
        Arguments.of(Theme.DARK, Transparency(1), darkThemeMainLayout, "FC"),
        Arguments.of(Theme.DARK, Transparency(20), darkThemeMainLayout, "CC"),
        Arguments.of(Theme.DARK, Transparency(50), darkThemeMainLayout, "7F"),
        Arguments.of(Theme.DARK, Transparency(79), darkThemeMainLayout, "35"),
        Arguments.of(Theme.DARK, Transparency(90), darkThemeMainLayout, "19"),
        Arguments.of(Theme.DARK, Transparency(100), darkThemeMainLayout, "00"),
        Arguments.of(Theme.LIGHT, Transparency(0), lightThemeMainLayout, "FF"),
        Arguments.of(Theme.LIGHT, Transparency(5), lightThemeMainLayout, "F2"),
        Arguments.of(Theme.LIGHT, Transparency(70), lightThemeMainLayout, "4C"),
        Arguments.of(Theme.LIGHT, Transparency(72), lightThemeMainLayout, "47"),
        Arguments.of(Theme.LIGHT, Transparency(98), lightThemeMainLayout, "05"),
        Arguments.of(Theme.LIGHT, Transparency(99), lightThemeMainLayout, "02"),
        Arguments.of(Theme.LIGHT, Transparency(100), lightThemeMainLayout, "00")
    )!!
}
