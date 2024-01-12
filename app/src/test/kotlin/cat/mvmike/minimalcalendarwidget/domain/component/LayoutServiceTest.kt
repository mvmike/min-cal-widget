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
import org.junit.jupiter.params.provider.CsvSource

private const val DARK_THEME_MAIN_LAYOUT = 2131034143
private const val LIGHT_THEME_MAIN_LAYOUT = 2131034144

internal class LayoutServiceTest : BaseTest() {

    private val widgetRv = mockk<RemoteViews>()

    @ParameterizedTest
    @CsvSource(
        "DARK,0,$DARK_THEME_MAIN_LAYOUT",
        "DARK,1,$DARK_THEME_MAIN_LAYOUT",
        "DARK,20,$DARK_THEME_MAIN_LAYOUT",
        "DARK,50,$DARK_THEME_MAIN_LAYOUT",
        "DARK,79,$DARK_THEME_MAIN_LAYOUT",
        "DARK,90,$DARK_THEME_MAIN_LAYOUT",
        "DARK,100,$DARK_THEME_MAIN_LAYOUT",
        "LIGHT,0,$LIGHT_THEME_MAIN_LAYOUT",
        "LIGHT,5,$LIGHT_THEME_MAIN_LAYOUT",
        "LIGHT,70,$LIGHT_THEME_MAIN_LAYOUT",
        "LIGHT,72,$LIGHT_THEME_MAIN_LAYOUT",
        "LIGHT,98,$LIGHT_THEME_MAIN_LAYOUT",
        "LIGHT,99,$LIGHT_THEME_MAIN_LAYOUT",
        "LIGHT,100,$LIGHT_THEME_MAIN_LAYOUT"
    )
    fun draw_shouldSetLayoutWithThemeBackgroundColourAndTransparency(
        widgetTheme: Theme,
        transparencyPercentage: Int,
        mainLayout: Int
    ) {
        val transparency = Transparency(transparencyPercentage)
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
}