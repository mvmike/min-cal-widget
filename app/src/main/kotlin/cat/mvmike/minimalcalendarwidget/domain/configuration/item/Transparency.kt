// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.domain.PERCENTAGE_RANGE
import cat.mvmike.minimalcalendarwidget.domain.Percentage
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import java.util.Locale

private val alphaRange = 0..255 // fully transparent to fully opaque
private const val HEX_STRING_FORMAT = "%02X"

data class Transparency(
    val percentage: Int
) : Percentage(percentage) {

    internal fun getAlpha(
        transparencyRange: TransparencyRange
    ) = (transparencyRange.maxAlpha - transparencyRange.minAlpha)
        .toFloat()
        .div(PERCENTAGE_RANGE.last)
        .times(PERCENTAGE_RANGE.last - percentage)
        .plus(transparencyRange.minAlpha)
        .toInt()

    internal fun getAlphaInHex(
        transparencyRange: TransparencyRange
    ) = String.format(Locale.ENGLISH, HEX_STRING_FORMAT, getAlpha(transparencyRange))
}

enum class TransparencyRange(
    val minAlpha: Int,
    val maxAlpha: Int
) {
    COMPLETE(
        minAlpha = alphaRange.first,
        maxAlpha = alphaRange.last
    ),
    MODERATE(
        minAlpha = alphaRange.first,
        maxAlpha = 80
    ),
    LOW(
        minAlpha = alphaRange.first,
        maxAlpha = 30
    );

    init {
        require(minAlpha in alphaRange)
        require(maxAlpha in alphaRange)
        require(minAlpha <= maxAlpha)
    }
}

internal fun String.withTransparency(
    transparency: Transparency,
    transparencyRange: TransparencyRange
) = GraphicResolver.parseColour("#${transparency.getAlphaInHex(transparencyRange)}${takeLast(6)}")