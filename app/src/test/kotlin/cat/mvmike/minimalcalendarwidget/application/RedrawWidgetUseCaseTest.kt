// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application

import android.appwidget.AppWidgetManager
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.Widget
import cat.mvmike.minimalcalendarwidget.application.system.MigrateConfigurationUseCase
import cat.mvmike.minimalcalendarwidget.domain.Variant
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
import org.junit.jupiter.params.provider.EnumSource
import java.time.DayOfWeek
import java.util.concurrent.TimeoutException
import kotlin.system.measureTimeMillis

internal class RedrawWidgetUseCaseTest : BaseTest() {

    private val appWidgetManager = mockk<AppWidgetManager>()

    private val appWidgetIds = intArrayOf(1, 2, 3)

    @Test
    fun execute_shouldRedrawAllVariants() {
        mockkObject(RedrawWidgetUseCase)

        Variant.entries.forEach { variant ->
            justRun { RedrawWidgetUseCase.execute(context, variant) }
        }
        every { RedrawWidgetUseCase.execute(context) } answers { callOriginal() }

        RedrawWidgetUseCase.execute(context)

        Variant.entries.forEach { variant ->
            verify { RedrawWidgetUseCase.execute(context, variant) }
        }
        verify { RedrawWidgetUseCase.execute(context) }
        confirmVerified(RedrawWidgetUseCase)
    }

    @ParameterizedTest
    @EnumSource(value = Variant::class)
    fun execute_shouldFetchVariantAppWidgetIdsAndRedrawWidgets(variant: Variant) {
        mockkObject(RedrawWidgetUseCase)
        mockkStatic(AppWidgetManager::class)

        every { AppWidgetManager.getInstance(context) } returns appWidgetManager
        every { appWidgetManager.getAppWidgetIds(any()) } returns appWidgetIds

        justRun { RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetIds, variant) }
        every { RedrawWidgetUseCase.execute(context, variant) } answers { callOriginal() }

        RedrawWidgetUseCase.execute(context, variant)

        verify { AppWidgetManager.getInstance(context) }
        verify { appWidgetManager.getAppWidgetIds(any()) }
        verify { Widget.getReceiverClass(variant) }
        verify { RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetIds, variant) }
        verify { RedrawWidgetUseCase.execute(context, variant) }
        confirmVerified(
            appWidgetManager,
            RedrawWidgetUseCase
        )
    }

    @ParameterizedTest
    @EnumSource(value = Variant::class)
    fun execute_shouldDoNothingWhenUserIsNotUnlockedAndCanNotRetrieveAppWidgetIds(variant: Variant) {
        mockkObject(RedrawWidgetUseCase)
        mockkStatic(AppWidgetManager::class)

        every { AppWidgetManager.getInstance(context) } returns appWidgetManager
        every {
            appWidgetManager.getAppWidgetIds(any())
        } throws IllegalStateException("User 0 must be unlocked for widgets to be available")

        every { RedrawWidgetUseCase.execute(context, variant) } answers { callOriginal() }

        RedrawWidgetUseCase.execute(context, variant)

        verify { AppWidgetManager.getInstance(context) }
        verify { appWidgetManager.getAppWidgetIds(any()) }
        verify { Widget.getReceiverClass(variant) }
        verify { RedrawWidgetUseCase.execute(context, variant) }
        confirmVerified(
            appWidgetManager,
            RedrawWidgetUseCase
        )
    }

    @ParameterizedTest
    @EnumSource(value = Variant::class)
    fun execute_shouldRedrawAllWidgetIds(variant: Variant) {
        mockkObject(RedrawWidgetUseCase)

        appWidgetIds.forEach {
            justRun { RedrawWidgetUseCase.execute(context, appWidgetManager, it, variant) }
        }
        every { RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetIds, variant) } answers { callOriginal() }

        RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetIds, variant)

        appWidgetIds.forEach {
            verify { RedrawWidgetUseCase.execute(context, appWidgetManager, it, variant) }
        }
        verify { RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetIds, variant) }
        confirmVerified(RedrawWidgetUseCase)
    }

    @ParameterizedTest
    @CsvSource(
        "1,32,DARK,20,33,MONDAY,false,MONTH",
        "5,100,LIGHT,5,33,SUNDAY,false,WEEK",
        "7,0,DARK,100,34,THURSDAY,true,MONTH",
        "14,50,DARK,32,35,MONDAY,true,WEEK"
    )
    fun execute_shouldRedrawWidgetRegardlessOfBinderProxyTransactionTimeout(
        appWidgetId: Int,
        textSizePercentage: Int,
        widgetTheme: Theme,
        transparencyPercentage: Int,
        runtimeSDK: Int,
        firstDayOfWeek: DayOfWeek,
        shouldHaveFirstWeekLocalPreferenceEnabled: Boolean,
        variant: Variant
    ) {
        mockkObject(
            MigrateConfigurationUseCase,
            ActionableView.ConfigurationIcon,
            ActionableView.MonthAndYearHeader,
            LayoutService,
            MonthAndYearHeaderService,
            DaysHeaderService,
            DaysService
        )

        val packageName = "mincalWidget"
        val layoutId = when (variant) {
            Variant.MONTH -> R.layout.widget_month
            Variant.WEEK -> R.layout.widget_week
        }
        every { context.packageName } returns packageName
        mockkConstructor(RemoteViews::class)
        justRun { constructedWith<RemoteViews>(EqMatcher(packageName), EqMatcher(layoutId)).removeAllViews(any()) }

        justRun { MigrateConfigurationUseCase.execute(context, appWidgetManager) }
        justRun { ActionableView.ConfigurationIcon.addListener(context, any(), appWidgetId, variant) }
        justRun { ActionableView.MonthAndYearHeader.addListener(context, any(), appWidgetId, variant) }

        val textSize = TextSize(textSizePercentage)
        val transparency = Transparency(transparencyPercentage)
        mockWidgetTextSize(textSize, appWidgetId)
        mockWidgetTheme(widgetTheme, appWidgetId)
        mockWidgetTransparency(transparency, appWidgetId)

        mockGetRuntimeSDK(runtimeSDK)
        if (shouldHaveFirstWeekLocalPreferenceEnabled) {
            mockGetSystemFirstDayOfWeek(firstDayOfWeek)
        } else {
            mockFirstDayOfWeek(firstDayOfWeek, appWidgetId)
        }

        justRun { LayoutService.draw(context, any(), widgetTheme, transparency) }
        justRun { MonthAndYearHeaderService.draw(context, any(), appWidgetId, textSize, widgetTheme) }
        justRun { DaysHeaderService.draw(context, any(), appWidgetId, variant, firstDayOfWeek, widgetTheme, transparency, textSize) }
        justRun { DaysService.draw(context, any(), appWidgetId, variant, firstDayOfWeek, widgetTheme, transparency, textSize) }

        val binderProxyTransactionTimeoutInMillis = 1000L
        every { appWidgetManager.updateAppWidget(appWidgetId, any()) } answers {
            Thread.sleep(binderProxyTransactionTimeoutInMillis)
            throw TimeoutException("android.os.BinderProxy.transactNative timeout")
        }

        val executionTime = measureTimeMillis {
            RedrawWidgetUseCase.execute(context, appWidgetManager, appWidgetId, variant)
        }

        assertThat(executionTime).isLessThan(binderProxyTransactionTimeoutInMillis)
        verify { MigrateConfigurationUseCase.execute(context, appWidgetManager) }
        verifyWidgetTextSize(appWidgetId)
        verifyWidgetTheme(appWidgetId)
        verifyWidgetTransparency(appWidgetId)
        verifyGetRuntimeSDK()
        when (shouldHaveFirstWeekLocalPreferenceEnabled) {
            true -> verifyGetSystemFirstDayOfWeek()
            else -> verifyFirstDayOfWeek(appWidgetId)
        }
        verify { context.packageName }
        verify { ActionableView.ConfigurationIcon.addListener(context, any(), appWidgetId, variant) }
        verify { ActionableView.MonthAndYearHeader.addListener(context, any(), appWidgetId, variant) }

        verify { LayoutService.draw(context, any(), widgetTheme, transparency) }
        verify { MonthAndYearHeaderService.draw(context, any(), appWidgetId, textSize, widgetTheme) }
        verify { DaysHeaderService.draw(context, any(), appWidgetId, variant, firstDayOfWeek, widgetTheme, transparency, textSize) }
        verify { DaysService.draw(context, any(), appWidgetId, variant, firstDayOfWeek, widgetTheme, transparency, textSize) }

        verify { appWidgetManager.updateAppWidget(appWidgetId, any()) }

        confirmVerified(
            MigrateConfigurationUseCase,
            appWidgetManager,
            LayoutService,
            MonthAndYearHeaderService,
            DaysHeaderService,
            DaysService
        )
    }
}