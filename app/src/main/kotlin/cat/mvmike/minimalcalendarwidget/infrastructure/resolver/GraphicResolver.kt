// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.text.Layout.Alignment
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.AlignmentSpan.Standard
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
        dayHeaderBackgroundColour?.setAsBackground(dayRv, viewId)
        daysHeaderRowRemoteView.addView(R.id.row_header, dayRv)
    }

    // DAY

    fun createDaysRow(context: Context) = getById(context, R.layout.row_week)

    fun createDayLayout(context: Context, dayLayout: Int) = getById(context, dayLayout)

    fun addToDaysRow(
        context: Context,
        weekRowRemoteView: RemoteViews,
        dayOfMonthRemoteView: RemoteViews,
        instancesSymbolRemoteView: RemoteViews?,
        viewId: Int,
        dayOfMonth: String,
        dayOfMonthColour: Int,
        dayOfMonthInBold: Boolean,
        instancesSymbol: Char,
        instancesSymbolColour: Int,
        instancesRelativeSize: Float,
        dayBackgroundColour: Int?,
        textRelativeSize: Float
    ) {
        val dayOfMonthSpSt = SpannableString(dayOfMonth).apply {
            if (dayOfMonthInBold) {
                setSpan(StyleSpan(BOLD), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            setSpan(RelativeSizeSpan(textRelativeSize), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
            instancesSymbolRemoteView?.let {
                setSpan(Standard(Alignment.ALIGN_OPPOSITE), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        dayOfMonthRemoteView.setTextViewText(viewId, dayOfMonthSpSt)
        dayOfMonthRemoteView.setTextColor(viewId, getColour(context, dayOfMonthColour))
        dayBackgroundColour?.setAsBackground(dayOfMonthRemoteView, viewId)
        weekRowRemoteView.addView(R.id.row_week, dayOfMonthRemoteView)

        instancesSymbolRemoteView?.let {
            val instancesSymbolSpSt = SpannableString("$instancesSymbol").apply {
                setSpan(StyleSpan(BOLD), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(ForegroundColorSpan(instancesSymbolColour), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(RelativeSizeSpan(instancesRelativeSize * textRelativeSize), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            it.setTextViewText(viewId, instancesSymbolSpSt)
            dayBackgroundColour?.setAsBackground(it, viewId)
            weekRowRemoteView.addView(R.id.row_week, it)
        }
    }

    // COLOUR

    fun getColour(context: Context, id: Int) = ContextCompat.getColor(context, id)

    fun getColourAsString(context: Context, id: Int) = context.resources.getString(id)

    fun parseColour(colourString: String) = Color.parseColor(colourString)

    fun Int.setAsBackground(
        remoteViews: RemoteViews,
        viewId: Int
    ) = remoteViews.setInt(viewId, "setBackgroundColor", this)

    // INTERNAL UTILS

    private fun getById(context: Context, layoutId: Int) = RemoteViews(context.packageName, layoutId)
}