// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import android.content.Context
import android.content.SharedPreferences
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
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
abstract class BaseTest {

    companion object {
        @JvmStatic
        val zoneId = ZoneId.of("Europe/Moscow")!!

        @JvmStatic
        val systemZoneOffset = zoneId.rules.getOffset(Instant.now())!!

        @JvmStatic
        val systemLocalDate = LocalDate.of(2018, 12, 4)!!
    }

    protected val context = mockk<Context>()
    protected val editor = mockk<SharedPreferences.Editor>()
    private val sharedPreferences = mockk<SharedPreferences>()

    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone(zoneId))
        clearAllMocks()
        unmockkAll()

        mockkObject(SystemResolver)
    }

    @AfterEach
    fun afterEach() {
        confirmVerified(
            SystemResolver,
            context,
            editor,
            sharedPreferences
        )
    }

    protected fun mockGetSystemInstant(instant: Instant) {
        every { SystemResolver.getInstant() } returns instant
    }

    protected fun mockGetSystemLocale(locale: Locale = Locale.ENGLISH) {
        every { SystemResolver.getLocale(context) } returns locale
    }

    protected fun mockGetSystemLocalDate() {
        every { SystemResolver.getSystemLocalDate() } returns systemLocalDate
    }

    protected fun mockGetSystemZoneId() {
        every { SystemResolver.getSystemZoneId() } returns zoneId
    }

    protected fun mockIsReadCalendarPermitted(permitted: Boolean) {
        every { SystemResolver.isReadCalendarPermitted(context) } returns permitted
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
