// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.AlignmentSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.CellContent

object GraphicResolver {

    // ADD VISUAL COMPONENTS TO WIDGET

    fun addToWidget(
        widgetRemoteView: RemoteViews,
        remoteView: RemoteViews
    ) = widgetRemoteView.addView(R.id.calendar_days_layout, remoteView)

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
            setExclusiveSpan(
                what = RelativeSizeSpan(textRelativeSize),
                end = month.length
            )
            setExclusiveSpan(
                what = RelativeSizeSpan(textRelativeSize * headerYearRelativeSize),
                start = month.length,
                end = text.length
            )
        }
        widgetRemoteView.setTextViewText(R.id.month_and_year_header, monthAndYearSpSt)
        widgetRemoteView.setTextColor(R.id.month_and_year_header, getColour(context, textColour))
    }

    // DAY HEADER

    fun createDaysHeaderRow(context: Context) = getById(context, R.layout.row_header)

    fun addToDaysHeaderRow(
        layoutId: Int,
        context: Context,
        daysHeaderRowRemoteView: RemoteViews,
        viewId: Int,
        dayHeaderBackgroundColour: Int?,
        cellContent: CellContent
    ) {
        val dayRv = getById(context, layoutId)
        cellContent.addToRemoteView(context, dayRv, viewId)
        dayHeaderBackgroundColour?.setAsBackground(dayRv, viewId)
        daysHeaderRowRemoteView.addView(R.id.row_header, dayRv)
    }

    // DAY

    fun createDaysRow(context: Context) = getById(context, R.layout.row_week)

    fun createDayLayout(
        context: Context,
        dayLayout: Int
    ) = getById(context, dayLayout)

    fun addToDaysRow(
        context: Context,
        weekRowRemoteView: RemoteViews,
        viewId: Int,
        backgroundColour: Int?,
        cells: List<Pair<RemoteViews?, CellContent>>
    ) = cells.forEach {
        it.first?.let { cellRemoteView ->
            it.second.addToRemoteView(context, cellRemoteView, viewId)
            backgroundColour?.setAsBackground(cellRemoteView, viewId)
            weekRowRemoteView.addView(R.id.row_week, cellRemoteView)
        }
    }

    // COLOUR

    fun getColourAsString(
        context: Context,
        id: Int
    ) = context.resources.getString(id)

    fun parseColour(colourString: String) = Color.parseColor(colourString)

    fun Int.setAsBackground(
        remoteViews: RemoteViews,
        viewId: Int
    ) = remoteViews.setInt(viewId, "setBackgroundColor", this)

    // INTERNAL UTILS

    private fun getById(
        context: Context,
        layoutId: Int
    ) = RemoteViews(context.packageName, layoutId)

    private fun getColour(
        context: Context,
        id: Int
    ) = ContextCompat.getColor(context, id)

    private fun CellContent.addToRemoteView(
        context: Context,
        remoteView: RemoteViews,
        viewId: Int
    ) {
        val spannableString = SpannableString(text).apply {
            setExclusiveSpan(RelativeSizeSpan(relativeSize))
            style?.let { setExclusiveSpan(StyleSpan(it)) }
            alignment?.let { setExclusiveSpan(AlignmentSpan.Standard(it)) }
        }
        remoteView.setTextViewText(viewId, spannableString)
        remoteView.setTextColor(viewId, getColour(context, colour))
    }

    private fun SpannableString.setExclusiveSpan(
        what: Any,
        start: Int = 0,
        end: Int = length
    ) = setSpan(what, start, end, SPAN_EXCLUSIVE_EXCLUSIVE)
}