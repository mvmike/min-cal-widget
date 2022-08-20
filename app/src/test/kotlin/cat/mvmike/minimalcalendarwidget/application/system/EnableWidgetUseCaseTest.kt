// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.system

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate
import io.mockk.justRun
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class EnableWidgetUseCaseTest : BaseTest() {

    @Test
    fun shouldSetAutoUpdate() {
        mockkObject(AutoUpdate)
        justRun { AutoUpdate.set(context, INTERVAL_MILLIS) }

        EnableWidgetUseCase.execute(context)

        verify { AutoUpdate.set(context, INTERVAL_MILLIS) }
    }
}
