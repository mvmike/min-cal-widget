// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import cat.mvmike.minimalcalendarwidget.domain.Percentage
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.PercentageConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.getConfigurationName
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Calendar
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TextSize
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.TransparencyRange
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionsActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.activity.WidgetClickActivity
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.nio.charset.StandardCharsets
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Random
import java.util.TimeZone

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseTest {

    protected val systemZoneId: ZoneId = ZoneId.of("Europe/Moscow")
    protected val systemLocalDate: LocalDate = LocalDate.parse("2018-12-04")
    protected val systemInstant: Instant = systemLocalDate
        .atTime(16, 32, 14)
        .atZone(systemZoneId)
        .toInstant()

    protected val appWidgetId = 2611

    protected val random = Random(appWidgetId.toLong())

    protected val context = mockk<Context>()

    protected val editor = mockk<SharedPreferences.Editor>()
    protected val sharedPreferences = mockk<SharedPreferences>()

    protected val intent = mockk<Intent>()
    protected val pendingIntent = mockk<PendingIntent>()

    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone(systemZoneId))
        clearAllMocks()
        unmockkAll()

        // needed to assert default value based on runtimeSDK
        EnumConfigurationItem.InstancesColour.let {}
        mockkObject(
            CalendarResolver,
            GraphicResolver,
            SystemResolver,
            CalendarActivity,
            ConfigurationActivity.Companion,
            PermissionsActivity.Companion,
            Widget.Companion,
            WidgetClickActivity.Companion
        )

        mockkStatic(PendingIntent::class)
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().setAction(any()) } returns intent
    }

    @AfterEach
    fun afterEach() {
        confirmVerified(
            CalendarResolver,
            GraphicResolver,
            SystemResolver,
            CalendarActivity,
            ConfigurationActivity.Companion,
            PermissionsActivity.Companion,
            Widget.Companion,
            WidgetClickActivity.Companion,
            context,
            editor,
            sharedPreferences,
            intent
        )
    }

    // SYSTEM

    protected fun mockGetRuntimeSDK(sdkVersion: Int) = every {
        SystemResolver.getRuntimeSDK()
    } returns sdkVersion

    protected fun verifyGetRuntimeSDK() = verify {
        SystemResolver.getRuntimeSDK()
    }

    protected fun mockGetSystemInstant(instant: Instant = systemInstant) = every {
        SystemResolver.getSystemInstant()
    } returns instant

    protected fun verifyGetSystemInstant() = verify {
        SystemResolver.getSystemInstant()
    }

    protected fun mockGetSystemLocalDate(localDate: LocalDate = systemLocalDate) = every {
        SystemResolver.getSystemLocalDate()
    } returns localDate

    protected fun verifyGetSystemLocalDate() = verify {
        SystemResolver.getSystemLocalDate()
    }

    protected fun mockGetSystemZoneId() = every {
        SystemResolver.getSystemZoneId()
    } returns systemZoneId

    protected fun verifyGetSystemZoneId(times: Int = 1) = verify(exactly = times) {
        SystemResolver.getSystemZoneId()
    }

    protected fun mockGetSystemFirstDayOfWeek(dayOfWeek: DayOfWeek) = every {
        SystemResolver.getSystemFirstDayOfWeek()
    } returns dayOfWeek

    protected fun verifyGetSystemFirstDayOfWeek() = verify {
        SystemResolver.getSystemFirstDayOfWeek()
    }

    protected fun mockIsReadCalendarPermitted(isPermitted: Boolean) = every {
        CalendarResolver.isReadCalendarPermitted(context)
    } returns isPermitted

    protected fun verifyIsReadCalendarPermitted() = verify {
        CalendarResolver.isReadCalendarPermitted(context)
    }

    // PREFERENCES

    protected fun mockSharedPreferences(appWidgetId: Int = this.appWidgetId) {
        every { context.getSharedPreferences(getConfigurationName(appWidgetId), Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns editor
        every { editor.clear() } returns editor
        every { editor.commit() } returns true
        justRun { editor.apply() }
    }

    protected fun verifySharedPreferencesAccess(appWidgetId: Int = this.appWidgetId) = verify {
        context.getSharedPreferences(getConfigurationName(appWidgetId), Context.MODE_PRIVATE)
    }

    protected fun verifySharedPreferencesEdit() = verify {
        sharedPreferences.edit()
    }

    private fun mockBooleanConfigurationItem(
        configurationItem: BooleanConfigurationItem,
        value: Boolean,
        appWidgetId: Int = this.appWidgetId
    ) {
        mockSharedPreferences(appWidgetId)
        every { sharedPreferences.getBoolean(configurationItem.key, configurationItem.defaultValue) } returns value
    }

    private fun verifyBooleanConfigurationItem(
        configurationItem: BooleanConfigurationItem,
        appWidgetId: Int = this.appWidgetId
    ) {
        verifySharedPreferencesAccess(appWidgetId)
        verify { sharedPreferences.getBoolean(configurationItem.key, configurationItem.defaultValue) }
    }

    private fun <E : Enum<E>> mockEnumConfigurationItem(
        configurationItem: EnumConfigurationItem<E>,
        value: E,
        appWidgetId: Int = this.appWidgetId
    ) {
        mockSharedPreferences(appWidgetId)
        every { sharedPreferences.getString(configurationItem.key, configurationItem.defaultValue.name) } returns value.name
    }

    private fun verifyEnumConfigurationItem(
        configurationItem: EnumConfigurationItem<*>,
        appWidgetId: Int = this.appWidgetId
    ) {
        verifySharedPreferencesAccess(appWidgetId)
        verify { sharedPreferences.getString(configurationItem.key, configurationItem.defaultValue.name) }
    }

    private fun <E : Percentage> mockPercentageConfigurationItem(
        configurationItem: PercentageConfigurationItem<E>,
        value: E,
        appWidgetId: Int = this.appWidgetId
    ) {
        mockSharedPreferences(appWidgetId)
        every { sharedPreferences.getInt(configurationItem.key, configurationItem.defaultValue.value) } returns value.value
    }

    private fun verifyPercentageConfigurationItem(
        configurationItem: PercentageConfigurationItem<*>,
        appWidgetId: Int = this.appWidgetId
    ) {
        verifySharedPreferencesAccess(appWidgetId)
        verify { sharedPreferences.getInt(configurationItem.key, configurationItem.defaultValue.value) }
    }

    protected fun mockCalendarVisibilitySelection(calendarId: Int, calendarVisibilitySelection: Boolean, appWidgetId: Int = this.appWidgetId) =
        mockBooleanConfigurationItem(BooleanConfigurationItem.CalendarVisibilitySelection(calendarId), calendarVisibilitySelection, appWidgetId)

    protected fun verifyCalendarVisibilitySelection(calendarId: Int, appWidgetId: Int = this.appWidgetId) =
        verifyBooleanConfigurationItem(BooleanConfigurationItem.CalendarVisibilitySelection(calendarId), appWidgetId)

    protected fun mockDefaultVisibleCalendars(defaultVisibleCalendars: Boolean, appWidgetId: Int = this.appWidgetId) =
        mockBooleanConfigurationItem(BooleanConfigurationItem.DefaultVisibleCalendars, defaultVisibleCalendars, appWidgetId)

    protected fun verifyDefaultVisibleCalendars(appWidgetId: Int = this.appWidgetId) =
        verifyBooleanConfigurationItem(BooleanConfigurationItem.DefaultVisibleCalendars, appWidgetId)

    protected fun mockShowDeclinedEvents(showDeclinedEvents: Boolean, appWidgetId: Int = this.appWidgetId) =
        mockBooleanConfigurationItem(BooleanConfigurationItem.ShowDeclinedEvents, showDeclinedEvents, appWidgetId)

    protected fun verifyShowDeclinedEvents(appWidgetId: Int = this.appWidgetId) =
        verifyBooleanConfigurationItem(BooleanConfigurationItem.ShowDeclinedEvents, appWidgetId)

    protected fun mockFocusOnCurrentWeek(enabled: Boolean, appWidgetId: Int = this.appWidgetId) =
        mockBooleanConfigurationItem(BooleanConfigurationItem.FocusOnCurrentWeek, enabled, appWidgetId)

    protected fun verifyFocusOnCurrentWeek(appWidgetId: Int = this.appWidgetId) =
        verifyBooleanConfigurationItem(BooleanConfigurationItem.FocusOnCurrentWeek, appWidgetId)

    protected fun mockOpenCalendarOnClickedDay(enabled: Boolean, appWidgetId: Int = this.appWidgetId) =
        mockBooleanConfigurationItem(BooleanConfigurationItem.OpenCalendarOnClickedDay, enabled, appWidgetId)

    protected fun verifyOpenCalendarOnClickedDay(appWidgetId: Int = this.appWidgetId) =
        verifyBooleanConfigurationItem(BooleanConfigurationItem.OpenCalendarOnClickedDay, appWidgetId)

    protected fun mockShowWeekNumber(enabled: Boolean, appWidgetId: Int = this.appWidgetId) =
        mockBooleanConfigurationItem(BooleanConfigurationItem.ShowWeekNumber, enabled, appWidgetId)

    protected fun verifyShowWeekNumber(appWidgetId: Int = this.appWidgetId) =
        verifyBooleanConfigurationItem(BooleanConfigurationItem.ShowWeekNumber, appWidgetId)

    protected fun mockWidgetTheme(widgetTheme: Theme, appWidgetId: Int = this.appWidgetId) =
        mockEnumConfigurationItem(EnumConfigurationItem.WidgetTheme, widgetTheme, appWidgetId)

    protected fun verifyWidgetTheme(appWidgetId: Int = this.appWidgetId) =
        verifyEnumConfigurationItem(EnumConfigurationItem.WidgetTheme, appWidgetId)

    protected fun mockWidgetCalendar(calendar: Calendar, appWidgetId: Int = this.appWidgetId) =
        mockEnumConfigurationItem(EnumConfigurationItem.WidgetCalendar, calendar, appWidgetId)

    protected fun verifyWidgetCalendar(appWidgetId: Int = this.appWidgetId) =
        verifyEnumConfigurationItem(EnumConfigurationItem.WidgetCalendar, appWidgetId)

    protected fun mockFirstDayOfWeek(dayOfWeek: DayOfWeek, appWidgetId: Int = this.appWidgetId) =
        mockEnumConfigurationItem(EnumConfigurationItem.FirstDayOfWeek, dayOfWeek, appWidgetId)

    protected fun verifyFirstDayOfWeek(appWidgetId: Int = this.appWidgetId) =
        verifyEnumConfigurationItem(EnumConfigurationItem.FirstDayOfWeek, appWidgetId)

    protected fun mockInstancesSymbolSet(symbolSet: SymbolSet, appWidgetId: Int = this.appWidgetId) =
        mockEnumConfigurationItem(EnumConfigurationItem.InstancesSymbolSet, symbolSet, appWidgetId)

    protected fun verifyInstancesSymbolSet(appWidgetId: Int = this.appWidgetId) =
        verifyEnumConfigurationItem(EnumConfigurationItem.InstancesSymbolSet, appWidgetId)

    protected fun mockInstancesColour(colour: Colour, appWidgetId: Int = this.appWidgetId) =
        mockEnumConfigurationItem(EnumConfigurationItem.InstancesColour, colour, appWidgetId)

    protected fun verifyInstancesColour(appWidgetId: Int = this.appWidgetId) =
        verifyEnumConfigurationItem(EnumConfigurationItem.InstancesColour, appWidgetId)

    protected fun mockWidgetTransparency(transparency: Transparency, appWidgetId: Int = this.appWidgetId) =
        mockPercentageConfigurationItem(PercentageConfigurationItem.WidgetTransparency, transparency, appWidgetId)

    protected fun verifyWidgetTransparency(appWidgetId: Int = this.appWidgetId) =
        verifyPercentageConfigurationItem(PercentageConfigurationItem.WidgetTransparency, appWidgetId)

    protected fun mockWidgetTextSize(textSize: TextSize, appWidgetId: Int = this.appWidgetId) =
        mockPercentageConfigurationItem(PercentageConfigurationItem.WidgetTextSize, textSize, appWidgetId)

    protected fun verifyWidgetTextSize(appWidgetId: Int = this.appWidgetId) =
        verifyPercentageConfigurationItem(PercentageConfigurationItem.WidgetTextSize, appWidgetId)

    // COLOUR TRANSPARENCY

    protected fun mockTransparency(
        resourceId: Int?,
        transparency: Transparency,
        transparencyRange: TransparencyRange
    ) = resourceId?.let {
        val transparentColourId = transparentColourId(resourceId)
        val transparentColourString = getTransparentColourString(transparency, transparencyRange, transparentColourId)
        every { GraphicResolver.getColourAsString(context, resourceId) } returns transparentColourId
        every { GraphicResolver.parseColour(transparentColourString) } returns resourceId
    }

    protected fun verifyTransparency(
        resourceId: Int?,
        transparency: Transparency,
        transparencyRange: TransparencyRange
    ) = resourceId?.let {
        val transparentColourId = transparentColourId(resourceId)
        val transparentColourString = getTransparentColourString(transparency, transparencyRange, transparentColourId)
        verify { GraphicResolver.getColourAsString(context, resourceId) }
        verify { GraphicResolver.parseColour(transparentColourString) }
    }

    private fun transparentColourId(resourceId: Int) = "transparentResource$resourceId"

    private fun getTransparentColourString(
        transparency: Transparency,
        transparencyRange: TransparencyRange,
        transparentColourId: String
    ) = "#${transparency.getAlphaInHex(transparencyRange)}${transparentColourId.takeLast(6)}"

    // INTENT

    protected fun mockIntent(action: String?) {
        every { intent.action } returns action
        every { intent.addFlags(any()) } returns intent
    }

    protected fun verifyIntentAction() = verify {
        intent.action
    }

    protected fun mockIntentAppWidgetId(appWidgetId: Int = this.appWidgetId) = every {
        intent.getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
    } returns appWidgetId

    protected fun verifyIntentAppWidgetId(times: Int = 1) = verify(exactly = times) {
        intent.getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
    }

    protected fun mockIntentPutAppWidgetId(appWidgetId: Int = this.appWidgetId) = every {
        intent.putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
    } returns intent

    protected fun verifyIntentPutAppWidgetId(appWidgetId: Int = this.appWidgetId, times: Int = 1) = verify(exactly = times) {
        intent.putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
    }

    // UTILS

    internal fun readTestResourceCsvFile(path: String) = this::class.java
        .getResourceAsStream(path)
        ?.bufferedReader(StandardCharsets.UTF_8)
        ?.readLines()
        ?.drop(1) // header
        ?.map { it.split(',').toTypedArray() }
        ?: fail("could not load $path")
}