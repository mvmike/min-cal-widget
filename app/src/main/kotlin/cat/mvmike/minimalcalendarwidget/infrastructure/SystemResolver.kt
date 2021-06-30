// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.provider.CalendarContract
import android.provider.CalendarContract.Instances
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import cat.mvmike.minimalcalendarwidget.MonthWidget
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.entry.FIELDS
import cat.mvmike.minimalcalendarwidget.domain.entry.Instance
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate
import java.time.Clock
import java.time.Instant
import java.time.Instant.ofEpochMilli
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

open class SystemResolver private constructor() {

    private val supportedLocales: Set<Locale> = setOf(
        Locale.ENGLISH,
        Locale("ca"), // catalan
        Locale("hr"), // croatian
        Locale("nl"), // dutch
        Locale("eo"), // esperanto
        Locale("fr"), // french
        Locale("de"), // german
        Locale("nb"), // norwegian
        Locale("pl"), // polish
        Locale("pt"), // portuguese
        Locale("ru"), // russian
        Locale("es") // spanish
    )

    companion object {
        @Volatile
        private var instance: SystemResolver? = null

        @JvmStatic
        @Synchronized
        fun get(): SystemResolver {
            instance = instance ?: SystemResolver()
            return instance!!
        }
    }

    // CLOCK

    open fun getInstant() = Clock.systemUTC().instant()!!

    open fun getSystemLocalDate() = LocalDate.now(Clock.systemDefaultZone())!!

    open fun getSystemZoneId() = ZoneId.systemDefault()!!

    // LOCALE

    open fun getLocale(context: Context): Locale {
        return context.resources.configuration.locales
            .takeIf {
                !it.isEmpty
            }?.let {
                supportedLocales.firstOrNull { sl -> sl.language == it[0].language }
            } ?: Locale.ENGLISH
    }

    // INTENT

    open fun setOnClickPendingIntent(
        context: Context,
        remoteViews: RemoteViews,
        viewId: Int,
        code: Int,
        action: String
    ) =
        remoteViews.setOnClickPendingIntent(
            viewId,
            PendingIntent.getBroadcast(
                context,
                code,
                Intent(context, MonthWidget::class.java).setAction(action),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

    open fun setRepeatingAlarm(
        context: Context,
        alarmId: Int,
        firstTriggerMillis: Long,
        intervalMillis: Long
    ) = context.getAlarmManager().setRepeating(
        AlarmManager.RTC, // RTC does not wake the device up
        firstTriggerMillis,
        intervalMillis,
        PendingIntent.getBroadcast(
            context,
            alarmId,
            Intent(context, MonthWidget::class.java).setAction(AutoUpdate.ACTION_AUTO_UPDATE),
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    )

    open fun cancelRepeatingAlarm(context: Context, alarmId: Int) =
        context.getAlarmManager().cancel(
            PendingIntent.getBroadcast(
                context,
                alarmId,
                Intent(context, MonthWidget::class.java).setAction(AutoUpdate.ACTION_AUTO_UPDATE),
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        )

    // ACTIVITY

    open fun <E> startActivity(context: Context, clazz: Class<E>) = context.startActivity(
        Intent(context, clazz)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )


    open fun startCalendarActivity(context: Context, startInstant: Instant) {
        val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
        ContentUris.appendId(builder, startInstant.toEpochMilli())

        context.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setData(builder.build())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    // CALENDAR CONTRACT

    open fun getInstances(context: Context, begin: Long, end: Long): Set<Instance> {
        val instances: MutableSet<Instance> = HashSet()
        Instances.query(context.contentResolver, FIELDS, begin, end).use { instanceCursor ->
            while (instanceCursor.moveToNext()) {
                instances.add(
                    Instance(
                        eventId = instanceCursor.getInt(0),
                        start = ofEpochMilli(instanceCursor.getLong(1)),
                        end = ofEpochMilli(instanceCursor.getLong(2)),
                        zoneId = ZoneId.of(instanceCursor.getString(3))
                    )
                )
            }
        }
        return instances.toSet()
    }

    // CONTEXT COMPAT

    open fun isReadCalendarPermitted(context: Context) =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED

    // ADD VISUAL COMPONENTS TO WIDGET

    open fun addToWidget(widgetRv: RemoteViews, rv: RemoteViews) = widgetRv.addView(R.id.calendar_widget, rv)

    // MONTH YEAR HEADER

    open fun createMonthAndYearHeader(widgetRemoteView: RemoteViews, monthAndYear: String, headerRelativeYearSize: Float) {
        val ss = SpannableString(monthAndYear)
        ss.setSpan(RelativeSizeSpan(headerRelativeYearSize), monthAndYear.length - 4, monthAndYear.length, 0)
        widgetRemoteView.setTextViewText(R.id.month_year_label, ss)
    }

    // DAY HEADER

    open fun createDaysHeaderRow(context: Context) = getById(context, R.layout.row_header)

    open fun addToDaysHeaderRow(context: Context, daysHeaderRow: RemoteViews, text: String, layoutId: Int) {
        val dayRv = getById(context, layoutId)
        dayRv.setTextViewText(android.R.id.text1, text)
        daysHeaderRow.addView(R.id.row_container, dayRv)
    }

    // DAY

    open fun createDaysRow(context: Context) = getById(context, R.layout.row_week)

    open fun addToDaysRow(
        context: Context, rowRv: RemoteViews, dayLayout: Int, spanText: String, isToday: Boolean,
        isSingleDigitDay: Boolean, symbolRelativeSize: Float, instancesColour: Int
    ) {
        val daySpSt = SpannableString(spanText)
        daySpSt.setSpan(StyleSpan(Typeface.BOLD), spanText.length - 1, spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (isSingleDigitDay) {
            daySpSt.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.alpha)), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (isToday) {
            daySpSt.setSpan(StyleSpan(Typeface.BOLD), 0, spanText.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        daySpSt.setSpan(ForegroundColorSpan(instancesColour), spanText.length - 1, spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        daySpSt.setSpan(RelativeSizeSpan(symbolRelativeSize), spanText.length - 1, spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val dayRv = getById(context, dayLayout)
        dayRv.setTextViewText(android.R.id.text1, daySpSt)
        rowRv.addView(R.id.row_container, dayRv)
    }

    open fun getInstancesColorTodayId(context: Context) = ContextCompat.getColor(context, R.color.instances_today)

    open fun getInstancesColorId(context: Context, colour: Colour) = ContextCompat.getColor(context, colour.hexValue)

    // INTERNAL UTILS

    private fun getById(context: Context, layoutId: Int) = RemoteViews(context.packageName, layoutId)

    private fun Context.getAlarmManager() = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}
