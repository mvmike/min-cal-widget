// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.system

import cat.mvmike.minimalcalendarwidget.BaseTest
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class DeleteWidgetUseCaseTest : BaseTest() {

    @Test
    fun execute_shouldDeleteConfigurationOfEveryDeletedWidget() {
        val appWidgetIds = intArrayOf(3, 8)
        appWidgetIds.forEach {
            every { context.deleteSharedPreferences("mincal_prefs_$it") } returns true
        }

        DeleteWidgetUseCase.execute(context, appWidgetIds)

        appWidgetIds.forEach {
            verify { context.deleteSharedPreferences("mincal_prefs_$it") }
        }
    }
}