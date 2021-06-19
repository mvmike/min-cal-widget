// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import android.content.Context
import android.content.SharedPreferences
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import java.util.TimeZone

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


    protected val context: Context = mockk()
    private val sharedPreferences: SharedPreferences = mockk()
    protected val editor: SharedPreferences.Editor = mockk()
    protected val systemResolver: SystemResolver = mockk()

    @BeforeAll
    fun beforeAll() {
        // force a different timezone than UTC for testing
        TimeZone.setDefault(TimeZone.getTimeZone(zoneId))
    }

    @BeforeEach
    fun beforeEach() {
        clearMocks(context, sharedPreferences, editor, systemResolver)
        try {
            val instance = SystemResolver::class.java.getDeclaredField("instance")
            instance.isAccessible = true
            instance[instance] = systemResolver
        } catch (e: Exception) {
            fail(e)
        }
    }

    protected fun mockGetSystemInstant(instant: Instant) {
        every {systemResolver.getInstant()} returns instant
    }

    protected fun mockGetSystemLocale(locale: Locale) {
        every {systemResolver.getLocale(context)} returns locale
    }

    protected fun mockGetSystemLocalDate() {
        every {systemResolver.getSystemLocalDate()} returns systemLocalDate
    }

    protected fun mockGetSystemZoneId() {
        every {systemResolver.getSystemZoneId()} returns zoneId
    }

    protected fun mockIsReadCalendarPermitted(permitted: Boolean) {
        every {systemResolver.isReadCalendarPermitted(context)} returns permitted
    }

    protected fun mockSharedPreferences() {
        every {context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE)} returns sharedPreferences
        every {sharedPreferences.edit()} returns editor
        every {editor.putString(any(), any())} returns editor
        every {editor.clear()} returns editor
        every {editor.commit()} returns true
        justRun { editor.apply() }
    }

    protected fun mockCalendarTheme(theme: Theme) {
        every {
            sharedPreferences.getString(
                Configuration.CalendarTheme.key,
                Configuration.CalendarTheme.defaultValue.name
            )
        } returns theme.name
    }

    protected fun mockFirstDayOfWeek(dayOfWeek: DayOfWeek) {
        every {
            sharedPreferences.getString(
                Configuration.FirstDayOfWeek.key,
                Configuration.FirstDayOfWeek.defaultValue.name
            )
        } returns dayOfWeek.name
    }

    protected fun mockInstancesColour(colour: Colour) {
        every {
            sharedPreferences.getString(
                Configuration.InstancesColour.key,
                Configuration.InstancesColour.defaultValue.name
            )
        } returns colour.name
    }

    protected fun mockInstancesSymbolSet(symbolSet: SymbolSet) {
        every {
            sharedPreferences.getString(
                Configuration.InstancesSymbolSet.key,
                Configuration.InstancesSymbolSet.defaultValue.name
            )
        } returns symbolSet.name
    }
}
