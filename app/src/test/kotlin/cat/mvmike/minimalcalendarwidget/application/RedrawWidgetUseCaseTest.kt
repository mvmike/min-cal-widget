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
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
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
import org.junit.jupiter.params.provider.CsvSource
import java.time.DayOfWeek
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
    fun shouldDoNothingWhenUserIsNotUnlockedAndCanNotRetrieveAppWidgetIds() {
        mockkObject(RedrawWidgetUseCase)
        mockkStatic(AppWidgetManager::class)

        every { AppWidgetManager.getInstance(context) } returns appWidgetManager
        every {
            appWidgetManager.getAppWidgetIds(any())
        } throws IllegalStateException("User 0 must be unlocked for widgets to be available")

        every { RedrawWidgetUseCase.execute(context) } answers { callOriginal() }

        RedrawWidgetUseCase.execute(context)

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
    @CsvSource(
        "1,32,DARK,20,33,MONDAY,false",
        "5,100,LIGHT,5,33,SUNDAY,false",
        "7,0,DARK,100,34,THURSDAY,true",
        "14,50,DARK,32,35,MONDAY,true"
    )
    fun shouldRedrawWidgetRegardlessOfBinderProxyTransactionTimeout(
        appWidgetId: Int,
        textSizePercentage: Int,
        widgetTheme: Theme,
        transparencyPercentage: Int,
        runtimeSDK: Int,
        firstDayOfWeek: DayOfWeek,
        shouldHaveFirstWeekLocalPreferenceEnabled: Boolean
    ) {
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
        justRun { constructedWith<RemoteViews>(EqMatcher(packageName), EqMatcher(2131427392)).removeAllViews(any()) }

        justRun { ActionableView.ConfigurationIcon.addListener(context, any()) }
        justRun { ActionableView.MonthAndYearHeader.addListener(context, any()) }

        val textSize = TextSize(textSizePercentage)
        val transparency = Transparency(transparencyPercentage)
        mockWidgetTextSize(textSize)
        mockWidgetTheme(widgetTheme)
        mockWidgetTransparency(transparency)

        mockGetRuntimeSDK(runtimeSDK)
        if (shouldHaveFirstWeekLocalPreferenceEnabled) {
            mockGetSystemFirstDayOfWeek(firstDayOfWeek)
        } else {
            mockFirstDayOfWeek(firstDayOfWeek)
        }

        justRun { LayoutService.draw(context, any(), widgetTheme, transparency) }
        justRun { MonthAndYearHeaderService.draw(context, any(), textSize, widgetTheme) }
        justRun { DaysHeaderService.draw(context, any(), firstDayOfWeek, widgetTheme, transparency, textSize) }
        justRun { DaysService.draw(context, any(), firstDayOfWeek, widgetTheme, transparency, textSize) }

        val binderProxyTransactionTimeoutInMillis = 1000L
        every { appWidgetManager.updateAppWidget(appWidgetId, any()) } answers {
            Thread.sleep(binderProxyTransactionTimeoutInMillis)
            throw TimeoutException("android.os.BinderProxy.transactNative timeout")
        }

        val executionTime = measureTimeMillis {
            RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetId)
        }

        assertThat(executionTime).isLessThan(binderProxyTransactionTimeoutInMillis)
        verifyWidgetTextSize()
        verifyWidgetTheme()
        verifyWidgetTransparency()
        verifyGetRuntimeSDK()
        when (shouldHaveFirstWeekLocalPreferenceEnabled) {
            true -> verifyGetSystemFirstDayOfWeek()
            else -> verifyFirstDayOfWeek()
        }
        verify { context.packageName }
        verify { ActionableView.ConfigurationIcon.addListener(context, any()) }
        verify { ActionableView.MonthAndYearHeader.addListener(context, any()) }

        verify { LayoutService.draw(context, any(), widgetTheme, transparency) }
        verify { MonthAndYearHeaderService.draw(context, any(), textSize, widgetTheme) }
        verify { DaysHeaderService.draw(context, any(), firstDayOfWeek, widgetTheme, transparency, textSize) }
        verify { DaysService.draw(context, any(), firstDayOfWeek, widgetTheme, transparency, textSize) }

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