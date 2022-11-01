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

private const val DARK_THEME_MAIN_LAYOUT = 2131034143
private const val LIGHT_THEME_MAIN_LAYOUT = 2131034144

internal class LayoutServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndTransparencyLevels")
    fun draw_shouldSetLayoutWithThemeBackgroundColourAndTransparency(
        theme: Theme,
        transparency: Transparency,
        mainLayout: Int,
        expectedMainLayoutHexTransparency: String
    ) {
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

    @Suppress("UnusedPrivateMember")
    private fun getCombinationOfThemesAndTransparencyLevels() = Stream.of(
        Arguments.of(Theme.DARK, Transparency(0), DARK_THEME_MAIN_LAYOUT, "FF"),
        Arguments.of(Theme.DARK, Transparency(1), DARK_THEME_MAIN_LAYOUT, "FC"),
        Arguments.of(Theme.DARK, Transparency(20), DARK_THEME_MAIN_LAYOUT, "CC"),
        Arguments.of(Theme.DARK, Transparency(50), DARK_THEME_MAIN_LAYOUT, "7F"),
        Arguments.of(Theme.DARK, Transparency(79), DARK_THEME_MAIN_LAYOUT, "35"),
        Arguments.of(Theme.DARK, Transparency(90), DARK_THEME_MAIN_LAYOUT, "19"),
        Arguments.of(Theme.DARK, Transparency(100), DARK_THEME_MAIN_LAYOUT, "00"),
        Arguments.of(Theme.LIGHT, Transparency(0), LIGHT_THEME_MAIN_LAYOUT, "FF"),
        Arguments.of(Theme.LIGHT, Transparency(5), LIGHT_THEME_MAIN_LAYOUT, "F2"),
        Arguments.of(Theme.LIGHT, Transparency(70), LIGHT_THEME_MAIN_LAYOUT, "4C"),
        Arguments.of(Theme.LIGHT, Transparency(72), LIGHT_THEME_MAIN_LAYOUT, "47"),
        Arguments.of(Theme.LIGHT, Transparency(98), LIGHT_THEME_MAIN_LAYOUT, "05"),
        Arguments.of(Theme.LIGHT, Transparency(99), LIGHT_THEME_MAIN_LAYOUT, "02"),
        Arguments.of(Theme.LIGHT, Transparency(100), LIGHT_THEME_MAIN_LAYOUT, "00")
    )!!
}
