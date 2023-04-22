// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import cat.mvmike.minimalcalendarwidget.R

object GraphicResolver {

    // ADD VISUAL COMPONENTS TO WIDGET

    fun addToWidget(widgetRemoteView: RemoteViews, remoteView: RemoteViews) =
        widgetRemoteView.addView(R.id.calendar_days_layout, remoteView)

    // MONTH YEAR HEADER

    fun createMonthAndYearHeader(
        context: Context,
        widgetRemoteView: RemoteViews,
        month: String,
        year: String,
        textColour: Int,
        headerYearRelativeSize: Float,
        textRelativeSize: Float
    ) {
        val text = "$month $year"
        val monthAndYearSpSt = SpannableString(text).apply {
            setSpan(RelativeSizeSpan(textRelativeSize), 0, month.length, SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(RelativeSizeSpan(textRelativeSize * headerYearRelativeSize), month.length, text.length, 0)
            setSpan(ForegroundColorSpan(getColour(context, textColour)), 0, text.length, 0)
        }
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
        val dayHeaderSpSt = SpannableString(text).apply {
            setSpan(RelativeSizeSpan(textRelativeSize), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        }

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
        val daySpSt = SpannableString(text).apply {
            if (dayOfMonthInBold) {
                setSpan(StyleSpan(BOLD), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                setSpan(StyleSpan(BOLD), length - 1, length, SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            setSpan(ForegroundColorSpan(instancesColour), length - 1, length, SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(RelativeSizeSpan(textRelativeSize), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(RelativeSizeSpan(instancesRelativeSize), length - 1, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        }

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
