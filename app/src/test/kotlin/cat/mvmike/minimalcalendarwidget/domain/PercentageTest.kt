// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class PercentageTest {

    @ParameterizedTest
    @ValueSource(
        ints = [0, 1, 8, 15, 26, 87, 99, 100]
    )
    fun shouldCreatePercentageWhenValueIsInRange(percentage: Int) {
        assertThat(Percentage(percentage).value).isEqualTo(percentage)
    }

    @ParameterizedTest
    @ValueSource(ints = [Integer.MIN_VALUE, -1, 101, Integer.MAX_VALUE])
    fun constructorShouldNotAllowIntsOutsideRange(percentage: Int) {
        assertThrows<IllegalArgumentException> {
            Percentage(percentage)
        }
    }
}