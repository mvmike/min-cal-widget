package cat.mvmike.minimalcalendarwidget.external;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.RemoteViews;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import cat.mvmike.minimalcalendarwidget.R;
import cat.mvmike.minimalcalendarwidget.service.dto.InstanceDto;
import cat.mvmike.minimalcalendarwidget.service.enums.Colour;

@SuppressWarnings({
    "PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal",
    "PMD.AvoidSynchronizedAtMethodLevel",
    "PMD.AvoidUsingVolatile"
})
public class SystemResolver {

    private static final Clock CLOCK_UTC_TZ = Clock.systemUTC();

    private static final Clock CLOCK_SYS_TZ = Clock.systemDefaultZone();

    private static volatile SystemResolver instance;

    private SystemResolver() {
        // only purpose is to defeat external instantiation
    }

    public static synchronized SystemResolver get() {

        if (instance == null) {
            instance = new SystemResolver();
        }
        return instance;
    }

    // CLOCK

    public Instant getInstant() {
        return CLOCK_UTC_TZ.instant();
    }

    public LocalDate getSystemLocalDate() {
        return LocalDate.now(CLOCK_SYS_TZ);
    }

    public LocalDateTime getSystemLocalDateTime() {
        return LocalDateTime.now(CLOCK_SYS_TZ);
    }

    // CALENDAR CONTRACT

    public Set<InstanceDto> getInstances(final Context context, final long begin, final long end) {

        Cursor instanceCursor = CalendarContract.Instances.query(context.getContentResolver(), InstanceDto.FIELDS, begin, end);

        if (instanceCursor == null || instanceCursor.getCount() == 0) {
            return null;
        }

        Set<InstanceDto> instances = new HashSet<>();
        while (instanceCursor.moveToNext()) {

            instances.add(new InstanceDto(
                instanceCursor.getLong(0),
                instanceCursor.getLong(1),
                instanceCursor.getInt(2),
                instanceCursor.getInt(3)
            ));
        }

        instanceCursor.close();
        return instances;
    }

    // CONTEXT COMPAT

    public boolean isReadCalendarPermitted(final Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    // MONTH YEAR HEADER

    public void createMonthYearHeader(final RemoteViews widgetRemoteView, final String monthAndYear, final float headerRelativeYearSize) {

        SpannableString ss = new SpannableString(monthAndYear);
        ss.setSpan(new RelativeSizeSpan(headerRelativeYearSize), monthAndYear.length() - 4, monthAndYear.length(), 0);

        widgetRemoteView.setTextViewText(R.id.month_year_label, ss);
    }

    // DAY HEADER

    public RemoteViews createHeaderRow(final Context context) {
        return getById(context, R.layout.row_header);
    }

    public void addHeaderDayToHeader(final Context context, final RemoteViews headerRowRv, final String text, final int layoutId) {

        RemoteViews dayRv = getById(context, layoutId);
        dayRv.setTextViewText(android.R.id.text1, text);

        headerRowRv.addView(R.id.row_container, dayRv);
    }

    public void addHeaderRowToWidget(final RemoteViews widgetRv, final RemoteViews headerRowRv) {
        widgetRv.addView(R.id.calendar_widget, headerRowRv);
    }

    // DAY

    public RemoteViews createRow(final Context context) {
        return getById(context, R.layout.row_week);
    }

    public RemoteViews createDay(final Context context, final int specificDayLayout) {
        return getById(context, specificDayLayout);
    }

    public void addRowToWidget(final RemoteViews widgetRv, final RemoteViews rowRv) {
        widgetRv.addView(R.id.calendar_widget, rowRv);
    }

    public int getColorInstancesTodayId(final Context context) {
        return ContextCompat.getColor(context, R.color.instances_today);
    }

    public int getColorInstancesId(final Context context, final Colour colour) {
        return ContextCompat.getColor(context, colour.getHexValue());
    }

    public void addDayCellRemoteView(final RemoteViews rowRv, final RemoteViews cellRv, final String spanText, final boolean isToday,
                                     final float symbolRelativeSize, final int instancesColor) {

        SpannableString daySpSt = new SpannableString(spanText);
        daySpSt.setSpan(new StyleSpan(Typeface.BOLD), spanText.length() - 1, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (isToday) {
            daySpSt.setSpan(new StyleSpan(Typeface.BOLD), 0, spanText.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        daySpSt.setSpan(new ForegroundColorSpan(instancesColor), spanText.length() - 1, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        daySpSt.setSpan(new RelativeSizeSpan(symbolRelativeSize), spanText.length() - 1, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        cellRv.setTextViewText(android.R.id.text1, daySpSt);

        rowRv.addView(R.id.row_container, cellRv);
    }

    // INTERNAL UTILS

    private static RemoteViews getById(final Context context, final int layoutId) {
        return new RemoteViews(context.getPackageName(), layoutId);
    }
}
