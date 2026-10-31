// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.component

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme.DARK
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme.LIGHT
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver.setAsBackground
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource

internal class LayoutServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @MethodSource("getThemeAndTransparencyWithExpectedMainBackground")
    fun draw_shouldSetLayoutWithThemeBackgroundColourAndTransparency(
        widgetTheme: Theme,
        transparency: Transparency,
        mainBackground: Int
    ) {
        mockTransparency(mainBackground, transparency, TransparencyRange.COMPLETE)

        justRun {
            mainBackground.setAsBackground(
                remoteViews = widgetRv,
                viewId = R.id.widget_layout
            )
        }

        LayoutService.draw(context, widgetRv, widgetTheme, transparency)

        verifyTransparency(mainBackground, transparency, TransparencyRange.COMPLETE)
        verify {
            mainBackground.setAsBackground(
                remoteViews = widgetRv,
                viewId = R.id.widget_layout
            )
        }
        confirmVerified(widgetRv)
    }

    private fun getThemeAndTransparencyWithExpectedMainBackground() = listOf(
        of(DARK, Transparency(0), R.color.background_full_dark),
        of(DARK, Transparency(1), R.color.background_full_dark),
        of(DARK, Transparency(20), R.color.background_full_dark),
        of(DARK, Transparency(50), R.color.background_full_dark),
        of(DARK, Transparency(79), R.color.background_full_dark),
        of(DARK, Transparency(90), R.color.background_full_dark),
        of(DARK, Transparency(100), R.color.background_full_dark),
        of(LIGHT, Transparency(0), R.color.background_full_light),
        of(LIGHT, Transparency(5), R.color.background_full_light),
        of(LIGHT, Transparency(70), R.color.background_full_light),
        of(LIGHT, Transparency(72), R.color.background_full_light),
        of(LIGHT, Transparency(98), R.color.background_full_light),
        of(LIGHT, Transparency(99), R.color.background_full_light),
        of(LIGHT, Transparency(100), R.color.background_full_light)
    )
}