// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import java.util.Locale

// ranges from 0 (fully transparent) to 255 (fully opaque)
private const val MIN_ALPHA = 0
private const val MAX_ALPHA = 255

private const val MIN_PERCENTAGE = 0
private const val MAX_PERCENTAGE = 100

private const val HEX_STRING_FORMAT = "%02X"

data class Transparency(
    val percentage: Int
) {
    init {
        require(percentage in MIN_PERCENTAGE..MAX_PERCENTAGE)
    }

    internal fun getAlpha(
        transparencyRange: TransparencyRange
    ) = (transparencyRange.maxAlpha - transparencyRange.minAlpha).toFloat()
        .div(MAX_PERCENTAGE)
        .times(MAX_PERCENTAGE - percentage)
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
        minAlpha = MIN_ALPHA,
        maxAlpha = MAX_ALPHA
    ),
    MODERATE(
        minAlpha = MIN_ALPHA,
        maxAlpha = 80
    ),
    LOW(
        minAlpha = MIN_ALPHA,
        maxAlpha = 30
    )
}

internal fun String.withTransparency(
    transparency: Transparency,
    transparencyRange: TransparencyRange
) = GraphicResolver.parseColour("#${transparency.getAlphaInHex(transparencyRange)}${takeLast(6)}")
