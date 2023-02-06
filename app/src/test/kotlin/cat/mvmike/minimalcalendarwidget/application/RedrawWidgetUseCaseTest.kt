// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application

import android.appwidget.AppWidgetManager
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.component.DaysHeaderService
import cat.mvmike.minimalcalendarwidget.domain.component.DaysService
import cat.mvmike.minimalcalendarwidget.domain.component.LayoutService
import cat.mvmike.minimalcalendarwidget.domain.component.MonthAndYearHeaderService
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.getFormat
import io.mockk.EqMatcher
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class RedrawWidgetUseCaseTest : BaseTest() {

    private val appWidgetManager = mockk<AppWidgetManager>()

    private val appWidgetIds = intArrayOf(1, 2, 3)

    @Test
    fun shouldFetchAllAppWidgetIdsAndRedrawWidgets() {
        mockkObject(RedrawWidgetUseCase)
        mockkStatic(AppWidgetManager::class)

        every { AppWidgetManager.getInstance(context) } returns appWidgetManager
        every { appWidgetManager.getAppWidgetIds(any()) } returns appWidgetIds

        justRun { RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetIds) }
        every { RedrawWidgetUseCase.execute(context) } answers { callOriginal() }

        RedrawWidgetUseCase.execute(context)

        verify { RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetIds) }
        verify { RedrawWidgetUseCase.execute(context) }
        confirmVerified(RedrawWidgetUseCase)
    }

    @Test
    fun shouldRedrawAllWidgetIds() {
        mockkObject(RedrawWidgetUseCase)

        appWidgetIds.forEach {
            justRun { RedrawWidgetUseCase.execute(context, appWidgetManager, it) }
        }
        every { RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetIds) } answers { callOriginal() }

        RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetIds)

        appWidgetIds.forEach {
            verify { RedrawWidgetUseCase.execute(context, appWidgetManager, it) }
        }
        verify { RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetIds) }
        confirmVerified(RedrawWidgetUseCase)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 5, 7, 14])
    fun shouldRedrawWidgetAndUpsertFormat(appWidgetId: Int) {
        mockkObject(
            LayoutService,
            MonthAndYearHeaderService,
            DaysHeaderService,
            DaysService
        )
        mockkStatic(
            ::getFormat
        )

        val packageName = "mincalWidget"
        every { context.packageName } returns packageName
        mockkConstructor(RemoteViews::class)
        justRun { constructedWith<RemoteViews>(EqMatcher(packageName), EqMatcher(2131427390)).removeAllViews(any()) }

        val format = Format(120)
        every { getFormat(context, appWidgetManager, appWidgetId) } returns format
        mockSharedPreferences()

        justRun { LayoutService.draw(context, any()) }
        justRun { MonthAndYearHeaderService.draw(context, any(), format) }
        justRun { DaysHeaderService.draw(context, any(), format) }
        justRun { DaysService.draw(context, any(), format) }

        justRun { appWidgetManager.updateAppWidget(appWidgetId, any()) }

        RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetId, true)

        verifySharedPreferencesAccess()
        verifySharedPreferencesEdit()
        verify { editor.putInt("${ConfigurationItem.WidgetFormat.key}_${appWidgetId}", format.width) }
        verify { editor.apply() }
        verify { context.packageName }
        verify { LayoutService.draw(context, any()) }
        verify { MonthAndYearHeaderService.draw(context, any(), format) }
        verify { DaysHeaderService.draw(context, any(), format) }
        verify { DaysService.draw(context, any(), format) }

        verify { appWidgetManager.updateAppWidget(appWidgetId, any()) }

        confirmVerified(
            appWidgetManager,
            LayoutService,
            MonthAndYearHeaderService,
            DaysHeaderService,
            DaysService
        )
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 5, 7, 14])
    fun shouldRedrawWidgetWithPreviousExistingFormat(appWidgetId: Int) {
        mockkObject(
            LayoutService,
            MonthAndYearHeaderService,
            DaysHeaderService,
            DaysService
        )
        mockSharedPreferences()

        val format = Format(220)
        mockWidgetFormat(Format(220), appWidgetId)

        val packageName = "mincalWidget"
        every { context.packageName } returns packageName
        mockkConstructor(RemoteViews::class)
        justRun { constructedWith<RemoteViews>(EqMatcher(packageName), EqMatcher(2131427390)).removeAllViews(any()) }

        every { getFormat(context, appWidgetManager, appWidgetId) } returns format
        justRun { LayoutService.draw(context, any()) }
        justRun { MonthAndYearHeaderService.draw(context, any(), format) }
        justRun { DaysHeaderService.draw(context, any(), format) }
        justRun { DaysService.draw(context, any(), format) }

        justRun { appWidgetManager.updateAppWidget(appWidgetId, any()) }

        RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetId)

        verifyWidgetFormat(appWidgetId)
        verify { context.packageName }
        verify { LayoutService.draw(context, any()) }
        verify { MonthAndYearHeaderService.draw(context, any(), format) }
        verify { DaysHeaderService.draw(context, any(), format) }
        verify { DaysService.draw(context, any(), format) }

        verify { appWidgetManager.updateAppWidget(appWidgetId, any()) }

        confirmVerified(
            appWidgetManager,
            LayoutService,
            MonthAndYearHeaderService,
            DaysHeaderService,
            DaysService
        )
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 5, 7, 14])
    fun shouldRedrawWidgetWithPreviousExistingFormat_whenCurrentFormatIsInvalid(appWidgetId: Int) {
        mockkObject(
            LayoutService,
            MonthAndYearHeaderService,
            DaysHeaderService,
            DaysService
        )
        mockSharedPreferences()

        val format = Format(220)
        mockWidgetFormat(format, appWidgetId)

        val packageName = "mincalWidget"
        every { context.packageName } returns packageName
        mockkConstructor(RemoteViews::class)
        justRun { constructedWith<RemoteViews>(EqMatcher(packageName), EqMatcher(2131427390)).removeAllViews(any()) }

        every { getFormat(context, appWidgetManager, appWidgetId) } returns null
        justRun { LayoutService.draw(context, any()) }
        justRun { MonthAndYearHeaderService.draw(context, any(), format) }
        justRun { DaysHeaderService.draw(context, any(), format) }
        justRun { DaysService.draw(context, any(), format) }

        justRun { appWidgetManager.updateAppWidget(appWidgetId, any()) }

        RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetId)

        verifyWidgetFormat(appWidgetId)
        verify { context.packageName }
        verify { LayoutService.draw(context, any()) }
        verify { MonthAndYearHeaderService.draw(context, any(), format) }
        verify { DaysHeaderService.draw(context, any(), format) }
        verify { DaysService.draw(context, any(), format) }

        confirmVerified(
            LayoutService,
            MonthAndYearHeaderService,
            DaysHeaderService,
            DaysService
        )
    }
}
