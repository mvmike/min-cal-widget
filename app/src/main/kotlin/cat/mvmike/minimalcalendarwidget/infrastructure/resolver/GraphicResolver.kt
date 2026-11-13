// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.resolver

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.AlignmentSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.Cell
import cat.mvmike.minimalcalendarwidget.domain.Colour

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
        textColour: Colour,
        headerYearRelativeSize: Float,
        textRelativeSize: Float,
        allowPagination: Boolean
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
        val colour = getColour(context, textColour)
        widgetRemoteView.setTextViewText(R.id.month_and_year_header, monthAndYearSpSt)
        widgetRemoteView.setTextColor(R.id.month_and_year_header, colour)
        val paginationIconsVisibility = when (allowPagination) {
            true -> View.VISIBLE
            else -> View.GONE
        }
        listOf(R.id.previous_icon, R.id.next_icon).forEach { viewId ->
            widgetRemoteView.setViewVisibility(viewId, paginationIconsVisibility)
            if (allowPagination) {
                colour.setAsColorFilter(widgetRemoteView, viewId)
            }
        }
    }

    // DAYS HEADER

    fun createDaysHeaderRow(context: Context) = getById(context, R.layout.row)

    fun addWeekNumberToDaysHeaderRow(
        context: Context,
        daysHeaderRowRemoteView: RemoteViews,
        cell: Cell
    ) {
        val dayHeaderRowRemoteView = getById(context, R.layout.row_week_number)
        val dayHeaderRemoteView = getById(context, R.layout.cell)
        cell.addToRemoteView(context, dayHeaderRemoteView, R.id.cell)
        dayHeaderRowRemoteView.addView(R.id.row_week_number, dayHeaderRemoteView)
        daysHeaderRowRemoteView.addView(R.id.row, dayHeaderRowRemoteView)
    }

    fun addDayToDaysHeaderRow(
        context: Context,
        daysHeaderRowRemoteView: RemoteViews,
        dayHeaderBackgroundColour: Int?,
        cell: Cell
    ) {
        val dayHeaderRowRemoteView = getById(context, R.layout.row)
        val dayHeaderRemoteView = getById(context, R.layout.cell)
        cell.addToRemoteView(context, dayHeaderRemoteView, R.id.cell)
        dayHeaderBackgroundColour?.setAsBackground(dayHeaderRowRemoteView, R.id.row)
        dayHeaderRowRemoteView.addView(R.id.row, dayHeaderRemoteView)
        daysHeaderRowRemoteView.addView(R.id.row, dayHeaderRowRemoteView)
    }

    // DAYs

    fun createDaysRow(context: Context) = getById(context, R.layout.row)

    fun createDayLayout(
        context: Context
    ) = getById(context, R.layout.cell)

    fun addWeekNumberToDaysRow(
        context: Context,
        weekRowRemoteView: RemoteViews,
        weekNumberRemoteView: RemoteViews,
        cell: Cell
    ) {
        val dayRowRemoteView = getById(context, R.layout.row_week_number)
        cell.addToRemoteView(context, weekNumberRemoteView, R.id.cell)
        dayRowRemoteView.addView(R.id.row_week_number, weekNumberRemoteView)
        weekRowRemoteView.addView(R.id.row, dayRowRemoteView)
    }

    fun addDayToDaysRow(
        context: Context,
        weekRowRemoteView: RemoteViews,
        backgroundColour: Int?,
        cells: List<Pair<RemoteViews?, Cell>>
    ) {
        val dayRowRemoteView = getById(context, R.layout.row)
        backgroundColour?.setAsBackground(dayRowRemoteView, R.id.row)
        cells.forEach { (cellRemoteView, cell) ->
            cellRemoteView?.let {
                cell.addToRemoteView(context, it, R.id.cell)
                dayRowRemoteView.addView(R.id.row, it)
            }
        }
        weekRowRemoteView.addView(R.id.row, dayRowRemoteView)
    }

    // COLOUR

    fun getColourAsString(
        context: Context,
        colour: Colour.Resource
    ) = context.resources.getString(colour.value)

    fun getColour(
        colour: Colour.Hex
    ) = colour.value.toColorInt()

    fun getColour(
        context: Context,
        colour: Colour
    ) = when (colour) {
        is Colour.Hex -> getColour(colour)
        is Colour.Resource -> ContextCompat.getColor(context, colour.value)
    }

    fun Int.setAsBackground(
        remoteViews: RemoteViews,
        viewId: Int
    ) = remoteViews.setInt(viewId, "setBackgroundColor", this)

    private fun Int.setAsBackgroundResource(
        remoteViews: RemoteViews,
        viewId: Int
    ) = remoteViews.setInt(viewId, "setBackgroundResource", this)

    private fun Int.setAsColorFilter(
        remoteViews: RemoteViews,
        viewId: Int
    ) = remoteViews.setInt(viewId, "setColorFilter", this)

    // INTERNAL UTILS

    private fun getById(
        context: Context,
        layoutId: Int
    ) = RemoteViews(context.packageName, layoutId)

    private fun Cell.addToRemoteView(
        context: Context,
        remoteView: RemoteViews,
        viewId: Int
    ) {
        val spannableString = SpannableString(text).apply {
            setExclusiveSpan(RelativeSizeSpan(relativeSize))
            alignment?.let { setExclusiveSpan(AlignmentSpan.Standard(it)) }
            if (bold) setExclusiveSpan(StyleSpan(Typeface.BOLD))
        }
        remoteView.setTextViewText(viewId, spannableString)
        remoteView.setTextColor(viewId, getColour(context, colour))
        highlightDrawable?.setAsBackgroundResource(remoteView, viewId)
    }

    private fun SpannableString.setExclusiveSpan(
        what: Any,
        start: Int = 0,
        end: Int = length
    ) = setSpan(what, start, end, SPAN_EXCLUSIVE_EXCLUSIVE)
}