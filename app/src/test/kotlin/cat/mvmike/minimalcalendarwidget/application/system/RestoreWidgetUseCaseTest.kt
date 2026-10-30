// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.system

import cat.mvmike.minimalcalendarwidget.BaseTest
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class RestoreWidgetUseCaseTest : BaseTest() {

    @Test
    fun execute_shouldMoveConfigurationFromOldToNewWidgets() {
        val oldAppWidgetIds = intArrayOf(3)
        val newAppWidgetIds = intArrayOf(21)
        mockSharedPreferences(3)
        mockSharedPreferences(21)
        every { sharedPreferences.all } returns mapOf("WIDGET_TEXT_SIZE" to 60)
        every { context.deleteSharedPreferences("mincal_prefs_3") } returns true

        RestoreWidgetUseCase.execute(context, oldAppWidgetIds, newAppWidgetIds)

        verifySharedPreferencesAccess(3)
        verifySharedPreferencesAccess(21)
        verify { sharedPreferences.all }
        verify { sharedPreferences.edit() }
        verify { editor.putInt("WIDGET_TEXT_SIZE", 60) }
        verify { editor.commit() }
        verify { context.deleteSharedPreferences("mincal_prefs_3") }
    }
}