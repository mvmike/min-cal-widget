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
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail
import org.mockito.Mockito.*
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

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


    protected val context = mock(Context::class.java)!!
    private val sharedPreferences = mock(SharedPreferences::class.java)!!
    protected val editor = mock(SharedPreferences.Editor::class.java)!!
    protected val systemResolver = mock(SystemResolver::class.java)!!

    @BeforeAll
    fun beforeAll() {
        // force a different timezone than UTC for testing
        TimeZone.setDefault(TimeZone.getTimeZone(zoneId))
    }

    @BeforeEach
    fun beforeEach() {
        reset(context, sharedPreferences, editor, systemResolver)
        try {
            val instance = SystemResolver::class.java.getDeclaredField("instance")
            instance.isAccessible = true
            instance[instance] = systemResolver
        } catch (e: Exception) {
            fail(e)
        }
    }

    protected fun mockGetSystemInstant(instant: Instant) {
        `when`(systemResolver.getInstant()).thenReturn(instant)
    }

    protected fun mockGetSystemLocale(locale: Locale) {
        `when`(systemResolver.getLocale(context)).thenReturn(locale)
    }

    protected fun mockGetSystemLocalDate() {
        `when`(systemResolver.getSystemLocalDate()).thenReturn(systemLocalDate)
    }

    protected fun mockGetSystemZoneId() {
        `when`(systemResolver.getSystemZoneId()).thenReturn(zoneId)
    }

    protected fun mockIsReadCalendarPermitted(permitted: Boolean) {
        `when`(systemResolver.isReadCalendarPermitted(context)).thenReturn(permitted)
    }

    protected fun mockSharedPreferences() {
        `when`(context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE)).thenReturn(sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putString(any(), any())).thenReturn(editor)
        `when`(editor.clear()).thenReturn(editor)
        `when`(editor.commit()).thenReturn(true)
    }

    protected fun mockCalendarTheme(theme: Theme) {
        `when`(
            sharedPreferences.getString(
                Configuration.CalendarTheme.key,
                Configuration.CalendarTheme.defaultValue.name
            )
        ).thenReturn(theme.name)
    }

    protected fun mockFirstDayOfWeek(dayOfWeek: DayOfWeek) {
        `when`(
            sharedPreferences.getString(
                Configuration.FirstDayOfWeek.key,
                Configuration.FirstDayOfWeek.defaultValue.name
            )
        ).thenReturn(dayOfWeek.name)
    }

    protected fun mockInstancesColour(colour: Colour) {
        `when`(
            sharedPreferences.getString(
                Configuration.InstancesColour.key,
                Configuration.InstancesColour.defaultValue.name
            )
        ).thenReturn(colour.name)
    }

    protected fun mockInstancesSymbolSet(symbolSet: SymbolSet) {
        `when`(
            sharedPreferences.getString(
                Configuration.InstancesSymbolSet.key,
                Configuration.InstancesSymbolSet.defaultValue.name
            )
        ).thenReturn(symbolSet.name)
    }
}
