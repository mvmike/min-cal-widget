// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import android.content.Context
import android.content.SharedPreferences
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import cat.mvmike.minimalcalendarwidget.infrastructure.config.ClockConfig
import cat.mvmike.minimalcalendarwidget.infrastructure.config.LocaleConfig
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.CalendarResolver
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import java.util.TimeZone
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance

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
            ClockConfig,
            LocaleConfig,
            CalendarResolver,
            SystemResolver
        )
    }

    @AfterEach
    fun afterEach() {
        confirmVerified(
            ClockConfig,
            LocaleConfig,
            CalendarResolver,
            SystemResolver,
            context,
            editor,
            sharedPreferences
        )
    }

    protected fun mockGetSystemInstant(instant: Instant) {
        every { ClockConfig.getInstant() } returns instant
    }

    protected fun mockGetSystemLocale(locale: Locale = Locale.ENGLISH) {
        every { LocaleConfig.getLocale(context) } returns locale
    }

    protected fun mockGetSystemLocalDate() {
        every { ClockConfig.getSystemLocalDate() } returns systemLocalDate
    }

    protected fun mockGetSystemZoneId() {
        every { ClockConfig.getSystemZoneId() } returns zoneId
    }

    protected fun mockIsReadCalendarPermitted(permitted: Boolean) {
        every { CalendarResolver.isReadCalendarPermitted(context) } returns permitted
    }

    protected fun mockSharedPreferences() {
        every { context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns  editor
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

    protected fun mockWidgetShowDeclinedEvents() {
        every {
            sharedPreferences.getBoolean(
                BooleanConfiguration.WidgetShowDeclinedEvents.key,
                BooleanConfiguration.WidgetShowDeclinedEvents.defaultValue
            )
        } returns false
    }

    protected fun verifyWidgetShowDeclinedEvents() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getBoolean(
                BooleanConfiguration.WidgetShowDeclinedEvents.key,
                BooleanConfiguration.WidgetShowDeclinedEvents.defaultValue
            )
        }
    }

    protected fun mockWidgetTransparency(transparency: Transparency) {
        every {
            sharedPreferences.getInt(
                Configuration.WidgetTransparency.key,
                Configuration.WidgetTransparency.defaultValue.percentage
            )
        } returns transparency.percentage
    }

    protected fun verifyWidgetTransparency() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getInt(
                Configuration.WidgetTransparency.key,
                Configuration.WidgetTransparency.defaultValue.percentage
            )
        }
    }

    protected fun mockWidgetTheme(theme: Theme) {
        every {
            sharedPreferences.getString(
                EnumConfiguration.WidgetTheme.key,
                EnumConfiguration.WidgetTheme.defaultValue.name
            )
        } returns theme.name
    }

    protected fun verifyWidgetTheme() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getString(
                EnumConfiguration.WidgetTheme.key,
                EnumConfiguration.WidgetTheme.defaultValue.name
            )
        }
    }

    protected fun mockFirstDayOfWeek(dayOfWeek: DayOfWeek) {
        every {
            sharedPreferences.getString(
                EnumConfiguration.FirstDayOfWeek.key,
                EnumConfiguration.FirstDayOfWeek.defaultValue.name
            )
        } returns dayOfWeek.name
    }

    protected fun verifyFirstDayOfWeek() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getString(
                EnumConfiguration.FirstDayOfWeek.key,
                EnumConfiguration.FirstDayOfWeek.defaultValue.name
            )
        }
    }

    protected fun mockFocusOnCurrentWeek(enabled: Boolean) {
        every {
            sharedPreferences.getBoolean(
                BooleanConfiguration.WidgetFocusOnCurrentWeek.key,
                BooleanConfiguration.WidgetFocusOnCurrentWeek.defaultValue
            )
        } returns enabled
    }

    protected fun verifyFocusOnCurrentWeek() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getBoolean(
                BooleanConfiguration.WidgetFocusOnCurrentWeek.key,
                BooleanConfiguration.WidgetFocusOnCurrentWeek.defaultValue
            )
        }
    }

    protected fun mockInstancesColour(colour: Colour) {
        every {
            sharedPreferences.getString(
                EnumConfiguration.InstancesColour.key,
                EnumConfiguration.InstancesColour.defaultValue.name
            )
        } returns colour.name
    }

    protected fun verifyInstancesColour() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getString(
                EnumConfiguration.InstancesColour.key,
                EnumConfiguration.InstancesColour.defaultValue.name
            )
        }
    }

    protected fun mockInstancesSymbolSet(symbolSet: SymbolSet) {
        every {
            sharedPreferences.getString(
                EnumConfiguration.InstancesSymbolSet.key,
                EnumConfiguration.InstancesSymbolSet.defaultValue.name
            )
        } returns symbolSet.name
    }

    protected fun verifyInstancesSymbolSet() {
        verifySharedPreferencesAccess()
        verify {
            sharedPreferences.getString(
                EnumConfiguration.InstancesSymbolSet.key,
                EnumConfiguration.InstancesSymbolSet.defaultValue.name
            )
        }
    }
}
