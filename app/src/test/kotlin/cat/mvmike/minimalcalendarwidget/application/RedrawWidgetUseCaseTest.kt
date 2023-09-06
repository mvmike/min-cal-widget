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
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.util.concurrent.TimeoutException
import java.util.stream.Stream
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
    @MethodSource("getWidgetIdsWithDrawingConfigurations")
    fun shouldRedrawWidgetRegardlessOfBinderProxyTransactionTimeout(testProperties: RedrawWidgetUseCaseTestProperties) {
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
        justRun { constructedWith<RemoteViews>(EqMatcher(packageName), EqMatcher(2131427393)).removeAllViews(any()) }

        justRun { ActionableView.ConfigurationIcon.addListener(context, any()) }
        justRun { ActionableView.MonthAndYearHeader.addListener(context, any()) }

        mockSharedPreferences()
        mockWidgetTextSize(testProperties.textSize)
        mockWidgetTheme(testProperties.widgetTheme)
        mockWidgetTransparency(testProperties.transparency)
        mockFirstDayOfWeek(testProperties.firstDayOfWeek)

        justRun {
            LayoutService.draw(
                context = context,
                widgetRemoteView = any(),
                widgetTheme = testProperties.widgetTheme,
                transparency = testProperties.transparency
            )
        }
        justRun {
            MonthAndYearHeaderService.draw(
                context = context,
                widgetRemoteView = any(),
                textSize = testProperties.textSize,
                widgetTheme = testProperties.widgetTheme
            )
        }
        justRun {
            DaysHeaderService.draw(
                context = context,
                widgetRemoteView = any(),
                firstDayOfWeek = testProperties.firstDayOfWeek,
                widgetTheme = testProperties.widgetTheme,
                transparency = testProperties.transparency,
                textSize = testProperties.textSize
            )
        }
        justRun {
            DaysService.draw(
                context = context,
                widgetRemoteView = any(),
                firstDayOfWeek = testProperties.firstDayOfWeek,
                widgetTheme = testProperties.widgetTheme,
                transparency = testProperties.transparency,
                textSize = testProperties.textSize
            )
        }

        val binderProxyTransactionTimeoutInMillis = 1000L
        every { appWidgetManager.updateAppWidget(testProperties.appWidgetId, any()) } answers {
            Thread.sleep(binderProxyTransactionTimeoutInMillis)
            throw TimeoutException("android.os.BinderProxy.transactNative timeout")
        }

        val executionTime = measureTimeMillis {
            RedrawWidgetUseCase.execute(context, appWidgetManager, testProperties.appWidgetId)
        }

        assertThat(executionTime).isLessThan(binderProxyTransactionTimeoutInMillis)
        verifySharedPreferencesAccess()
        verifyWidgetTextSize()
        verifyWidgetTheme()
        verifyWidgetTransparency()
        verifyFirstDayOfWeek()
        verify { context.packageName }
        verify { ActionableView.ConfigurationIcon.addListener(context, any()) }
        verify { ActionableView.MonthAndYearHeader.addListener(context, any()) }
        verify {
            LayoutService.draw(
                context = context,
                widgetRemoteView = any(),
                widgetTheme = testProperties.widgetTheme,
                transparency = testProperties.transparency
            )
        }
        verify {
            MonthAndYearHeaderService.draw(
                context = context,
                widgetRemoteView = any(),
                textSize = testProperties.textSize,
                widgetTheme = testProperties.widgetTheme
            )
        }
        verify {
            DaysHeaderService.draw(
                context = context,
                widgetRemoteView = any(),
                firstDayOfWeek = testProperties.firstDayOfWeek,
                widgetTheme = testProperties.widgetTheme,
                transparency = testProperties.transparency,
                textSize = testProperties.textSize
            )
        }
        verify {
            DaysService.draw(
                context = context,
                widgetRemoteView = any(),
                firstDayOfWeek = testProperties.firstDayOfWeek,
                widgetTheme = testProperties.widgetTheme,
                transparency = testProperties.transparency,
                textSize = testProperties.textSize
            )
        }

        verify { appWidgetManager.updateAppWidget(testProperties.appWidgetId, any()) }

        confirmVerified(
            appWidgetManager,
            LayoutService,
            MonthAndYearHeaderService,
            DaysHeaderService,
            DaysService
        )
    }

    private fun getWidgetIdsWithDrawingConfigurations() = Stream.of(
        RedrawWidgetUseCaseTestProperties(1, TextSize(32), Theme.DARK, Transparency(20), DayOfWeek.MONDAY),
        RedrawWidgetUseCaseTestProperties(5, TextSize(100), Theme.LIGHT, Transparency(5), DayOfWeek.SUNDAY),
        RedrawWidgetUseCaseTestProperties(7, TextSize(0), Theme.DARK, Transparency(100), DayOfWeek.THURSDAY),
        RedrawWidgetUseCaseTestProperties(14, TextSize(50), Theme.DARK, Transparency(32), DayOfWeek.MONDAY)
    )

    internal data class RedrawWidgetUseCaseTestProperties(
        val appWidgetId: Int,
        val textSize: TextSize,
        val widgetTheme: Theme,
        val transparency: Transparency,
        val firstDayOfWeek: DayOfWeek
    )
}