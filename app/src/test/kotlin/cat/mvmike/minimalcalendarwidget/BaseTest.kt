// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.PercentageConfigurationItem
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
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Random
import java.util.TimeZone

private const val PREFERENCES_ID: String = "mincal_prefs"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseTest {

    protected val systemZoneId = ZoneId.of("Europe/Moscow")!!
    protected val systemLocalDate = LocalDate.parse("2018-12-04")!!
    protected val systemInstant = systemLocalDate
        .atTime(16, 32, 14)
        .toInstant(systemZoneId.rules.getOffset(Instant.now()))!!

    protected val random = Random()

    protected val context = mockk<Context>()

    protected val editor = mockk<SharedPreferences.Editor>()
    private val sharedPreferences = mockk<SharedPreferences>()

    protected val intent = mockk<Intent>()

    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone(systemZoneId))
        clearAllMocks()
        unmockkAll()

        mockkObject(
            CalendarResolver,
            GraphicResolver,
            SystemResolver,
            CalendarActivity,
            ConfigurationActivity.Companion,
            PermissionsActivity.Companion
        )
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

    protected fun verifyGetSystemZoneId() = verify {
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

    protected fun mockSharedPreferences() {
        every { context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns editor
        every { editor.clear() } returns editor
        every { editor.commit() } returns true
        justRun { editor.apply() }
    }

    protected fun verifySharedPreferencesAccess() = verify {
        context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE)
    }

    protected fun verifySharedPreferencesEdit() = verify {
        sharedPreferences.edit()
    }

    protected fun mockWidgetTextSize(textSize: TextSize) {
        mockSharedPreferences()
        every {
            sharedPreferences.getInt(
                PercentageConfigurationItem.WidgetTextSize.key,
                PercentageConfigurationItem.WidgetTextSize.defaultValue.percentage
            )
        } returns textSize.percentage
    }

    protected fun verifyWidgetTextSize() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getInt(
                PercentageConfigurationItem.WidgetTextSize.key,
                PercentageConfigurationItem.WidgetTextSize.defaultValue.percentage
            )
        }
    }

    protected fun mockWidgetTransparency(transparency: Transparency) {
        mockSharedPreferences()
        every {
            sharedPreferences.getInt(
                PercentageConfigurationItem.WidgetTransparency.key,
                PercentageConfigurationItem.WidgetTransparency.defaultValue.percentage
            )
        } returns transparency.percentage
    }

    protected fun verifyWidgetTransparency() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getInt(
                PercentageConfigurationItem.WidgetTransparency.key,
                PercentageConfigurationItem.WidgetTransparency.defaultValue.percentage
            )
        }
    }

    protected fun mockShowDeclinedEvents(showDeclinedEvents: Boolean) {
        mockSharedPreferences()
        every {
            sharedPreferences.getBoolean(
                BooleanConfigurationItem.ShowDeclinedEvents.key,
                BooleanConfigurationItem.ShowDeclinedEvents.defaultValue
            )
        } returns showDeclinedEvents
    }

    protected fun verifyShowDeclinedEvents() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getBoolean(
                BooleanConfigurationItem.ShowDeclinedEvents.key,
                BooleanConfigurationItem.ShowDeclinedEvents.defaultValue
            )
        }
    }

    protected fun mockFocusOnCurrentWeek(enabled: Boolean) {
        mockSharedPreferences()
        every {
            sharedPreferences.getBoolean(
                BooleanConfigurationItem.FocusOnCurrentWeek.key,
                BooleanConfigurationItem.FocusOnCurrentWeek.defaultValue
            )
        } returns enabled
    }

    protected fun verifyFocusOnCurrentWeek() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getBoolean(
                BooleanConfigurationItem.FocusOnCurrentWeek.key,
                BooleanConfigurationItem.FocusOnCurrentWeek.defaultValue
            )
        }
    }

    protected fun mockOpenCalendarOnClickedDay(enabled: Boolean) {
        mockSharedPreferences()
        every {
            sharedPreferences.getBoolean(
                BooleanConfigurationItem.OpenCalendarOnClickedDay.key,
                BooleanConfigurationItem.OpenCalendarOnClickedDay.defaultValue
            )
        } returns enabled
    }

    protected fun verifyOpenCalendarOnClickedDay() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getBoolean(
                BooleanConfigurationItem.OpenCalendarOnClickedDay.key,
                BooleanConfigurationItem.OpenCalendarOnClickedDay.defaultValue
            )
        }
    }

    protected fun mockWidgetTheme(widgetTheme: Theme) {
        mockSharedPreferences()
        every {
            sharedPreferences.getString(
                EnumConfigurationItem.WidgetTheme.key,
                EnumConfigurationItem.WidgetTheme.defaultValue.name
            )
        } returns widgetTheme.name
    }

    protected fun verifyWidgetTheme() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getString(
                EnumConfigurationItem.WidgetTheme.key,
                EnumConfigurationItem.WidgetTheme.defaultValue.name
            )
        }
    }

    protected fun mockWidgetCalendar(calendar: Calendar) {
        mockSharedPreferences()
        every {
            sharedPreferences.getString(
                EnumConfigurationItem.WidgetCalendar.key,
                EnumConfigurationItem.WidgetCalendar.defaultValue.name
            )
        } returns calendar.name
    }

    protected fun verifyWidgetCalendar() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getString(
                EnumConfigurationItem.WidgetCalendar.key,
                EnumConfigurationItem.WidgetCalendar.defaultValue.name
            )
        }
    }

    protected fun mockFirstDayOfWeek(dayOfWeek: DayOfWeek) {
        mockSharedPreferences()
        every {
            sharedPreferences.getString(
                EnumConfigurationItem.FirstDayOfWeek.key,
                EnumConfigurationItem.FirstDayOfWeek.defaultValue.name
            )
        } returns dayOfWeek.name
    }

    protected fun verifyFirstDayOfWeek() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getString(
                EnumConfigurationItem.FirstDayOfWeek.key,
                EnumConfigurationItem.FirstDayOfWeek.defaultValue.name
            )
        }
    }

    protected fun mockInstancesSymbolSet(symbolSet: SymbolSet) {
        mockSharedPreferences()
        every {
            sharedPreferences.getString(
                EnumConfigurationItem.InstancesSymbolSet.key,
                EnumConfigurationItem.InstancesSymbolSet.defaultValue.name
            )
        } returns symbolSet.name
    }

    protected fun verifyInstancesSymbolSet() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getString(
                EnumConfigurationItem.InstancesSymbolSet.key,
                EnumConfigurationItem.InstancesSymbolSet.defaultValue.name
            )
        }
    }

    protected fun mockInstancesColour(colour: Colour) {
        mockSharedPreferences()
        every {
            sharedPreferences.getString(
                EnumConfigurationItem.InstancesColour.key,
                EnumConfigurationItem.InstancesColour.defaultValue.name
            )
        } returns colour.name
    }

    protected fun verifyInstancesColour() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getString(
                EnumConfigurationItem.InstancesColour.key,
                EnumConfigurationItem.InstancesColour.defaultValue.name
            )
        }
    }

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

    protected fun mockIntentWithLongExtra(
        action: String,
        systemInstant: Instant,
        extraInstant: Instant
    ) {
        mockIntent(action)
        every {
            intent.getLongExtra("startOfDayInEpochSeconds", systemInstant.epochSecond)
        } returns extraInstant.epochSecond
    }
}