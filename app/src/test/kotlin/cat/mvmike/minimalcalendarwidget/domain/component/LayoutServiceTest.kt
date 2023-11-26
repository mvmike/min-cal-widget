// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver.setAsBackground
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

private const val DARK_THEME_MAIN_LAYOUT = 2131034143
private const val LIGHT_THEME_MAIN_LAYOUT = 2131034144

internal class LayoutServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndTransparencyLevels")
    fun draw_shouldSetLayoutWithThemeBackgroundColourAndTransparency(
        widgetTheme: Theme,
        transparency: Transparency,
        mainLayout: Int
    ) {
        mockTransparency(mainLayout, transparency, TransparencyRange.COMPLETE)

        justRun {
            mainLayout.setAsBackground(
                remoteViews = widgetRv,
                viewId = R.id.widget_layout
            )
        }

        LayoutService.draw(context, widgetRv, widgetTheme, transparency)

        verifyTransparency(mainLayout, transparency, TransparencyRange.COMPLETE)
        verify {
            mainLayout.setAsBackground(
                remoteViews = widgetRv,
                viewId = R.id.widget_layout
            )
        }
        confirmVerified(widgetRv)
    }

    private fun getCombinationOfThemesAndTransparencyLevels() = listOf(
        Arguments.of(Theme.DARK, Transparency(0), DARK_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.DARK, Transparency(1), DARK_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.DARK, Transparency(20), DARK_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.DARK, Transparency(50), DARK_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.DARK, Transparency(79), DARK_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.DARK, Transparency(90), DARK_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.DARK, Transparency(100), DARK_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.LIGHT, Transparency(0), LIGHT_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.LIGHT, Transparency(5), LIGHT_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.LIGHT, Transparency(70), LIGHT_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.LIGHT, Transparency(72), LIGHT_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.LIGHT, Transparency(98), LIGHT_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.LIGHT, Transparency(99), LIGHT_THEME_MAIN_LAYOUT),
        Arguments.of(Theme.LIGHT, Transparency(100), LIGHT_THEME_MAIN_LAYOUT)
    )
}