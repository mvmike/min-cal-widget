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
import android.graphics.Color
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
import cat.mvmike.minimalcalendarwidget.domain.entry.Instance
import cat.mvmike.minimalcalendarwidget.domain.intent.AutoUpdate
import java.time.Clock
import java.time.Instant
import java.time.Instant.ofEpochMilli
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

object SystemResolver {

    private val supportedLocales: Set<Locale> = setOf(
        Locale.ENGLISH,
        Locale("ca"), // catalan
        Locale("hr"), // croatian
        Locale("nl"), // dutch
        Locale("eo"), // esperanto
        Locale("fr"), // french
        Locale("de"), // german
        Locale("lt"), // lithuanian
        Locale("nb"), // norwegian
        Locale("pl"), // polish
        Locale("pt"), // portuguese
        Locale("ru"), // russian
        Locale("es") // spanish
    )

    // CLOCK

    fun getInstant() = Clock.systemUTC().instant()!!

    fun getSystemLocalDate() = LocalDate.now(Clock.systemDefaultZone())!!

    fun getSystemZoneId() = ZoneId.systemDefault()!!

    // LOCALE

    fun getLocale(context: Context): Locale = context.resources.configuration.locales
        .takeIf { !it.isEmpty }
        ?.let { supportedLocales.firstOrNull { sl -> sl.language == it[0].language } }
        ?: Locale.ENGLISH

    // INTENT

    fun setOnClickPendingIntent(
        context: Context,
        widgetRemoteView: RemoteViews,
        viewId: Int,
        code: Int,
        action: String
    ) = widgetRemoteView.setOnClickPendingIntent(
        viewId,
        PendingIntent.getBroadcast(
            context,
            code,
            Intent(context, MonthWidget::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )

    fun setRepeatingAlarm(
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
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )

    fun cancelRepeatingAlarm(context: Context, alarmId: Int) = context.getAlarmManager().cancel(
        PendingIntent.getBroadcast(
            context,
            alarmId,
            Intent(context, MonthWidget::class.java).setAction(AutoUpdate.ACTION_AUTO_UPDATE),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )

    // ACTIVITY

    fun <E> startActivity(context: Context, clazz: Class<E>) = context.startActivity(
        Intent(context, clazz)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )


    fun startCalendarActivity(context: Context, startInstant: Instant) {
        val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
        ContentUris.appendId(builder, startInstant.toEpochMilli())

        context.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setData(builder.build())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    // CALENDAR CONTRACT

    fun getInstances(context: Context, begin: Long, end: Long): Set<Instance> {
        val instances: MutableSet<Instance> = HashSet()
        val queryFields: Array<String> = arrayOf(
            Instances.EVENT_ID,
            Instances.BEGIN,
            Instances.END,
            Instances.EVENT_TIMEZONE
        )
        Instances.query(context.contentResolver, queryFields, begin, end).use { instanceCursor ->
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

    fun isReadCalendarPermitted(context: Context) =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED

    // ADD VISUAL COMPONENTS TO WIDGET

    fun addToWidget(widgetRemoteView: RemoteViews, remoteView: RemoteViews) = widgetRemoteView.addView(R.id.calendar_widget, remoteView)

    // MONTH YEAR HEADER

    fun createMonthAndYearHeader(
        widgetRemoteView: RemoteViews,
        monthAndYear: String,
        headerRelativeYearSize: Float
    ) {
        val ss = SpannableString(monthAndYear)
        ss.setSpan(RelativeSizeSpan(headerRelativeYearSize), monthAndYear.length - 4, monthAndYear.length, 0)
        widgetRemoteView.setTextViewText(R.id.month_year_label, ss)
    }

    // DAY HEADER

    fun createDaysHeaderRow(context: Context) = getById(context, R.layout.row_header)

    fun addToDaysHeaderRow(
        context: Context,
        daysHeaderRow: RemoteViews,
        text: String,
        layoutId: Int,
        viewId: Int,
        dayHeaderBackgroundColour: Int?
    ) {
        val dayRv = getById(context, layoutId)
        dayRv.setTextViewText(android.R.id.text1, text)
        dayHeaderBackgroundColour?.let {
            setBackgroundColor(dayRv, viewId, it)
        }
        daysHeaderRow.addView(R.id.row_container, dayRv)
    }

    // DAY

    fun createDaysRow(context: Context) = getById(context, R.layout.row_week)

    @SuppressWarnings("LongParameterList")
    fun addToDaysRow(
        context: Context,
        weekRow: RemoteViews,
        dayLayout: Int,
        viewId: Int,
        dayBackgroundColour: Int?,
        spanText: String,
        isToday: Boolean,
        isSingleDigitDay: Boolean,
        symbolRelativeSize: Float,
        instancesColour: Int
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
        dayBackgroundColour?.let {
            setBackgroundColor(dayRv, viewId, it)
        }
        weekRow.addView(R.id.row_container, dayRv)
    }

    // COLOUR

    fun getColour(context: Context, id: Int) = ContextCompat.getColor(context, id)

    fun getColourAsString(context: Context, id: Int) = context.resources.getString(id)

    fun parseColour(colourString: String) = Color.parseColor(colourString)

    // TRANSPARENCY

    fun setBackgroundColor(
        remoteViews: RemoteViews,
        viewId: Int,
        colour: Int
    ) = remoteViews.setInt(viewId, "setBackgroundColor", colour)

    // INTERNAL UTILS

    private fun getById(context: Context, layoutId: Int) = RemoteViews(context.packageName, layoutId)

    private fun Context.getAlarmManager() = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}
