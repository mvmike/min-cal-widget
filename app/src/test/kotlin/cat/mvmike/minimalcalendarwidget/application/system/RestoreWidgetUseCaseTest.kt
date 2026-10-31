// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.system

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.PercentageConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.getConfigurationName
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
        every { sharedPreferences.all } returns mapOf(PercentageConfigurationItem.WidgetTextSize.key to 60)
        every { context.deleteSharedPreferences(getConfigurationName(3)) } returns true

        RestoreWidgetUseCase.execute(context, oldAppWidgetIds, newAppWidgetIds)

        verifySharedPreferencesAccess(3)
        verifySharedPreferencesAccess(21)
        verify { sharedPreferences.all }
        verify { sharedPreferences.edit() }
        verify { editor.putInt(PercentageConfigurationItem.WidgetTextSize.key, 60) }
        verify { editor.commit() }
        verify { context.deleteSharedPreferences(getConfigurationName(3)) }
    }
}