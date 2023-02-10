// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import android.content.Context
import android.content.SharedPreferences
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfigurationItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Format
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
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
import java.util.TimeZone

private const val PREFERENCES_ID: String = "mincal_prefs"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseTest {

    protected val zoneId = ZoneId.of("Europe/Moscow")!!
    protected val systemZoneOffset = zoneId.rules.getOffset(Instant.now())!!
    protected val systemLocalDate = LocalDate.of(2018, 12, 4)!!

    protected val context = mockk<Context>()
    protected val editor = mockk<SharedPreferences.Editor>()
    private val sharedPreferences = mockk<SharedPreferences>()

    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone(zoneId))
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
            sharedPreferences
        )
    }

    protected fun mockGetRuntimeSDK(sdkVersion: Int) {
        every { SystemResolver.getRuntimeSDK() } returns sdkVersion
    }

    protected fun mockGetSystemInstant(instant: Instant) {
        every { SystemResolver.getSystemInstant() } returns instant
    }

    protected fun mockGetSystemLocalDate() {
        every { SystemResolver.getSystemLocalDate() } returns systemLocalDate
    }

    protected fun mockGetSystemZoneId() {
        every { SystemResolver.getSystemZoneId() } returns zoneId
    }

    protected fun mockIsReadCalendarPermitted(isPermitted: Boolean) {
        every { CalendarResolver.isReadCalendarPermitted(context) } returns isPermitted
    }

    protected fun mockSharedPreferences() {
        every { context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns editor
        every { editor.clear() } returns editor
        every { editor.commit() } returns true
        justRun { editor.apply() }
    }

    protected fun verifySharedPreferencesAccess() {
        verify { context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE) }
    }

    protected fun verifySharedPreferencesEdit() {
        verify { sharedPreferences.edit() }
    }

    protected fun mockWidgetFormat(format: Format, appWidgetId: Int) {
        every {
            sharedPreferences.getInt(
                "${ConfigurationItem.WidgetFormat.key}_${appWidgetId}",
                ConfigurationItem.WidgetFormat.defaultValue.width
            )
        } returns format.width
    }

    protected fun verifyWidgetFormat(appWidgetId: Int) {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getInt(
                "${ConfigurationItem.WidgetFormat.key}_${appWidgetId}",
                ConfigurationItem.WidgetFormat.defaultValue.width
            )
        }
    }

    protected fun mockWidgetTransparency(transparency: Transparency) {
        every {
            sharedPreferences.getInt(
                ConfigurationItem.WidgetTransparency.key,
                ConfigurationItem.WidgetTransparency.defaultValue.percentage
            )
        } returns transparency.percentage
    }

    protected fun verifyWidgetTransparency() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getInt(
                ConfigurationItem.WidgetTransparency.key,
                ConfigurationItem.WidgetTransparency.defaultValue.percentage
            )
        }
    }

    protected fun mockWidgetShowDeclinedEvents(widgetShowDeclinedEvents: Boolean = false) {
        every {
            sharedPreferences.getBoolean(
                BooleanConfigurationItem.WidgetShowDeclinedEvents.key,
                BooleanConfigurationItem.WidgetShowDeclinedEvents.defaultValue
            )
        } returns widgetShowDeclinedEvents
    }

    protected fun verifyWidgetShowDeclinedEvents() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getBoolean(
                BooleanConfigurationItem.WidgetShowDeclinedEvents.key,
                BooleanConfigurationItem.WidgetShowDeclinedEvents.defaultValue
            )
        }
    }

    protected fun mockWidgetFocusOnCurrentWeek(enabled: Boolean) {
        every {
            sharedPreferences.getBoolean(
                BooleanConfigurationItem.WidgetFocusOnCurrentWeek.key,
                BooleanConfigurationItem.WidgetFocusOnCurrentWeek.defaultValue
            )
        } returns enabled
    }

    protected fun verifyWidgetFocusOnCurrentWeek() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getBoolean(
                BooleanConfigurationItem.WidgetFocusOnCurrentWeek.key,
                BooleanConfigurationItem.WidgetFocusOnCurrentWeek.defaultValue
            )
        }
    }

    protected fun mockWidgetTheme(theme: Theme) {
        every {
            sharedPreferences.getString(
                EnumConfigurationItem.WidgetTheme.key,
                EnumConfigurationItem.WidgetTheme.defaultValue.name
            )
        } returns theme.name
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

    protected fun mockFirstDayOfWeek(dayOfWeek: DayOfWeek) {
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
}
