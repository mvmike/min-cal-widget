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
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.intent.ActionableView
import io.mockk.EqMatcher
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.TimeoutException
import kotlin.system.measureTimeMillis

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
    fun shouldRedrawWidgetRegardlessOfBinderProxyTransactionTimeout(appWidgetId: Int) {
        mockkObject(
            ActionableView.ConfigurationIcon,
            ActionableView.MonthAndYearHeader,
            LayoutService,
            MonthAndYearHeaderService,
            DaysHeaderService,
            DaysService
        )

        val packageName = "mincalWidget"
        every { context.packageName } returns packageName
        mockkConstructor(RemoteViews::class)
        justRun { constructedWith<RemoteViews>(EqMatcher(packageName), EqMatcher(2131427390)).removeAllViews(any()) }

        justRun { ActionableView.ConfigurationIcon.addListener(context, any()) }
        justRun { ActionableView.MonthAndYearHeader.addListener(context, any()) }

        val textSize = TextSize(40)
        mockSharedPreferences()
        mockWidgetTextSize(textSize)

        justRun { LayoutService.draw(context, any()) }
        justRun { MonthAndYearHeaderService.draw(context, any(), textSize) }
        justRun { DaysHeaderService.draw(context, any(), textSize) }
        justRun { DaysService.draw(context, any(), textSize) }

        val binderProxyTransactionTimeoutInMillis = 1000L
        every { appWidgetManager.updateAppWidget(appWidgetId, any()) } answers {
            Thread.sleep(binderProxyTransactionTimeoutInMillis)
            throw TimeoutException("android.os.BinderProxy.transactNative timeout")
        }

        val executionTime = measureTimeMillis {
            RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetId)
        }

        assertThat(executionTime).isLessThan(binderProxyTransactionTimeoutInMillis)
        verifySharedPreferencesAccess()
        verifyWidgetTextSize()
        verify { context.packageName }
        verify { ActionableView.ConfigurationIcon.addListener(context, any()) }
        verify { ActionableView.MonthAndYearHeader.addListener(context, any()) }
        verify { LayoutService.draw(context, any()) }
        verify { MonthAndYearHeaderService.draw(context, any(), textSize) }
        verify { DaysHeaderService.draw(context, any(), textSize) }
        verify { DaysService.draw(context, any(), textSize) }

        verify { appWidgetManager.updateAppWidget(appWidgetId, any()) }

        confirmVerified(
            appWidgetManager,
            LayoutService,
            MonthAndYearHeaderService,
            DaysHeaderService,
            DaysService
        )
    }
}