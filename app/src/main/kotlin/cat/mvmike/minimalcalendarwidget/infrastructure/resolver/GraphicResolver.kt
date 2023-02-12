// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import cat.mvmike.minimalcalendarwidget.R

object GraphicResolver {

    // ADD VISUAL COMPONENTS TO WIDGET

    fun addToWidget(widgetRemoteView: RemoteViews, remoteView: RemoteViews) = widgetRemoteView.addView(R.id.calendar_days_layout, remoteView)

    // MONTH YEAR HEADER

    fun createMonthAndYearHeader(
        context: Context,
        widgetRemoteView: RemoteViews,
        text: String,
        textColour: Int,
        headerRelativeYearSize: Float,
        textRelativeSize: Float
    ) {
        val monthAndYearSpSt = SpannableString(text)
        monthAndYearSpSt.setSpan(RelativeSizeSpan(textRelativeSize), 0, monthAndYearSpSt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        monthAndYearSpSt.setSpan(RelativeSizeSpan(headerRelativeYearSize * textRelativeSize), text.length - 4, text.length, 0)
        monthAndYearSpSt.setSpan(ForegroundColorSpan(getColour(context, textColour)), 0, text.length, 0)
        widgetRemoteView.setTextViewText(R.id.month_and_year_header, monthAndYearSpSt)
    }

    // DAY HEADER

    fun createDaysHeaderRow(context: Context) = getById(context, R.layout.row_header)

    fun addToDaysHeaderRow(
        context: Context,
        daysHeaderRowRemoteView: RemoteViews,
        text: String,
        textColour: Int,
        layoutId: Int,
        viewId: Int,
        dayHeaderBackgroundColour: Int?,
        textRelativeSize: Float
    ) {
        val dayHeaderSpSt = SpannableString(text)
        dayHeaderSpSt.setSpan(RelativeSizeSpan(textRelativeSize), 0, dayHeaderSpSt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val dayRv = getById(context, layoutId)
        dayRv.setTextViewText(viewId, dayHeaderSpSt)
        dayRv.setTextColor(viewId, getColour(context, textColour))
        dayHeaderBackgroundColour?.let {
            setBackgroundColor(dayRv, viewId, it)
        }
        daysHeaderRowRemoteView.addView(R.id.row_header, dayRv)
    }

    // DAY

    fun createDaysRow(context: Context) = getById(context, R.layout.row_week)

    fun createDay(context: Context, dayLayout: Int) = getById(context, dayLayout)

    fun addToDaysRow(
        context: Context,
        weekRowRemoteView: RemoteViews,
        dayRemoteView: RemoteViews,
        viewId: Int,
        text: String,
        textColour: Int,
        dayOfMonthInBold: Boolean,
        instancesColour: Int,
        instancesRelativeSize: Float,
        dayBackgroundColour: Int?,
        textRelativeSize: Float
    ) {
        val daySpSt = SpannableString(text)
        if (dayOfMonthInBold) {
            daySpSt.setSpan(StyleSpan(Typeface.BOLD), 0, daySpSt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            daySpSt.setSpan(StyleSpan(Typeface.BOLD), daySpSt.length - 1, daySpSt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        daySpSt.setSpan(ForegroundColorSpan(instancesColour), daySpSt.length - 1, daySpSt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        daySpSt.setSpan(RelativeSizeSpan(textRelativeSize), 0, daySpSt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        daySpSt.setSpan(RelativeSizeSpan(instancesRelativeSize), daySpSt.length - 1, daySpSt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        dayRemoteView.setTextViewText(viewId, daySpSt)
        dayRemoteView.setTextColor(viewId, getColour(context, textColour))
        dayBackgroundColour?.let {
            setBackgroundColor(dayRemoteView, viewId, it)
        }
        weekRowRemoteView.addView(R.id.row_week, dayRemoteView)
    }

    // COLOUR

    fun getColour(context: Context, id: Int) = ContextCompat.getColor(context, id)

    fun getColourAsString(context: Context, id: Int) = context.resources.getString(id)

    fun parseColour(colourString: String) = Color.parseColor(colourString)

    fun setBackgroundColor(
        remoteViews: RemoteViews,
        viewId: Int,
        colour: Int
    ) = remoteViews.setInt(viewId, "setBackgroundColor", colour)

    // INTERNAL UTILS

    private fun getById(context: Context, layoutId: Int) = RemoteViews(context.packageName, layoutId)
}
