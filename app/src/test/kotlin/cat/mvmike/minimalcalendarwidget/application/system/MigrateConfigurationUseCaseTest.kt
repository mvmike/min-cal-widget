// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.system

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.SharedPreferences
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.Widget
import cat.mvmike.minimalcalendarwidget.domain.Variant
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class MigrateConfigurationUseCaseTest : BaseTest() {

    private val appWidgetManager = mockk<AppWidgetManager>()

    private val legacySharedPreferences = mockk<SharedPreferences>()

    private val legacyEditor = mockk<SharedPreferences.Editor>()

    @Test
    fun execute_shouldDoNothingWhenThereIsNoLegacyConfiguration() {
        mockLegacyConfiguration(emptyMap())

        MigrateConfigurationUseCase.execute(context, appWidgetManager)

        verifyLegacyConfigurationAccess()
        confirmVerified(appWidgetManager, legacySharedPreferences, legacyEditor)
    }

    @Test
    fun execute_shouldMigrateLegacyConfigurationToAllWidgetsOfAllVariants() {
        mockLegacyConfiguration(mapOf("WIDGET_THEME" to "LIGHT"))
        every { appWidgetManager.getAppWidgetIds(any()) } returnsMany listOf(intArrayOf(3), intArrayOf(8))
        mockSharedPreferences(3)
        mockSharedPreferences(8)

        MigrateConfigurationUseCase.execute(context, appWidgetManager)

        verify(exactly = 2) { context.getSharedPreferences("mincal_prefs", Context.MODE_PRIVATE) }
        verify(exactly = 2) { legacySharedPreferences.all }
        verify(exactly = Variant.entries.size) { appWidgetManager.getAppWidgetIds(any()) }
        Variant.entries.forEach { verify { Widget.getReceiverClass(it) } }
        verifySharedPreferencesAccess(3)
        verifySharedPreferencesAccess(8)
        verify(exactly = 2) { sharedPreferences.edit() }
        verify(exactly = 2) { editor.putString("WIDGET_THEME", "LIGHT") }
        verify(exactly = 2) { editor.commit() }
        verify { legacySharedPreferences.edit() }
        verify { legacyEditor.clear() }
        verify { legacyEditor.commit() }
        confirmVerified(appWidgetManager, legacySharedPreferences, legacyEditor)
    }

    @Test
    fun execute_shouldKeepLegacyConfigurationWhenAppWidgetIdsCanNotBeRetrieved() {
        mockLegacyConfiguration(mapOf("WIDGET_THEME" to "LIGHT"))
        every {
            appWidgetManager.getAppWidgetIds(any())
        } throws IllegalStateException("User 0 must be unlocked for widgets to be available")

        MigrateConfigurationUseCase.execute(context, appWidgetManager)

        verifyLegacyConfigurationAccess()
        verify { appWidgetManager.getAppWidgetIds(any()) }
        verify { Widget.getReceiverClass(Variant.entries.first()) }
        confirmVerified(appWidgetManager, legacySharedPreferences, legacyEditor)
    }

    private fun mockLegacyConfiguration(configuration: Map<String, Any>) {
        every { context.getSharedPreferences("mincal_prefs", Context.MODE_PRIVATE) } returns legacySharedPreferences
        every { legacySharedPreferences.all } returns configuration
        every { legacySharedPreferences.edit() } returns legacyEditor
        every { legacyEditor.clear() } returns legacyEditor
        every { legacyEditor.commit() } returns true
        justRun { legacyEditor.apply() }
    }

    private fun verifyLegacyConfigurationAccess() {
        verify { context.getSharedPreferences("mincal_prefs", Context.MODE_PRIVATE) }
        verify { legacySharedPreferences.all }
    }
}