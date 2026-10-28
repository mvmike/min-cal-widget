// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.system

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.Variant
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class DisableWidgetUseCaseTest : BaseTest() {

    @ParameterizedTest
    @EnumSource(value = Variant::class)
    fun execute_shouldCancelAutoUpdate(variant: Variant) {
        mockkObject(AutoUpdate)
        justRun { AutoUpdate.cancel(context, variant) }

        DisableWidgetUseCase.execute(context, variant)

        verify { AutoUpdate.cancel(context, variant) }
        confirmVerified(AutoUpdate)
    }
}