// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.preference

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import androidx.preference.SeekBarPreference
import kotlin.math.roundToInt

@Suppress("unused")
open class MultilinePreference : Preference {

    constructor(context: Context) :
        super(context)
    constructor(context: Context, attrs: AttributeSet?) :
        super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
        super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        super(context, attrs, defStyleAttr, defStyleRes)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.allowMultilineTitleAndSummary()
    }
}

@Suppress("unused")
class MultilineListPreference : ListPreference {

    constructor(context: Context) :
        super(context)
    constructor(context: Context, attrs: AttributeSet?) :
        super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
        super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        super(context, attrs, defStyleAttr, defStyleRes)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.allowMultilineTitleAndSummary()
    }
}

@Suppress("unused")
class MultilineCheckBoxPreference : CheckBoxPreference {

    constructor(context: Context) :
        super(context)
    constructor(context: Context, attrs: AttributeSet?) :
        super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
        super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        super(context, attrs, defStyleAttr, defStyleRes)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.allowMultilineTitleAndSummary()
    }
}

@Suppress("unused")
class MultilineSeekBarPreference : SeekBarPreference {

    constructor(context: Context) :
        super(context)
    constructor(context: Context, attrs: AttributeSet?) :
        super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
        super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        super(context, attrs, defStyleAttr, defStyleRes)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.allowMultilineTitleAndSummary()
    }
}

private const val SWATCH_SIZE_DP = 18
private const val SWATCH_MARGIN_END_DP = 16
private const val SWATCH_STROKE_DP = 1
private const val SWATCH_STROKE_COLOUR = 0x40808080

@Suppress("unused")
class MultilineColourPreference : MultilinePreference {

    var swatchColour: Int? = null
        set(value) {
            if (field != value) {
                field = value
                notifyChanged()
            }
        }

    constructor(context: Context) :
        super(context)
    constructor(context: Context, attrs: AttributeSet?) :
        super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
        super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        super(context, attrs, defStyleAttr, defStyleRes)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.allowMultilineTitleAndSummary()
        val widgetFrame = holder.findViewById(android.R.id.widget_frame) as? ViewGroup ?: return
        val colour = swatchColour
        if (colour == null) {
            widgetFrame.visibility = View.GONE
            return
        }

        widgetFrame.visibility = View.VISIBLE
        val density = context.resources.displayMetrics.density
        val swatch = widgetFrame.getChildAt(0) ?: View(context).also { view ->
            val sizePx = (SWATCH_SIZE_DP * density).roundToInt()
            view.layoutParams = ViewGroup.MarginLayoutParams(sizePx, sizePx).apply {
                marginEnd = (SWATCH_MARGIN_END_DP * density).roundToInt()
            }
            widgetFrame.addView(view)
        }
        val drawable = (swatch.background as? GradientDrawable) ?: GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setStroke((SWATCH_STROKE_DP * density).roundToInt(), SWATCH_STROKE_COLOUR)
            swatch.background = this
        }
        drawable.setColor(colour)
    }
}

private fun PreferenceViewHolder.allowMultilineTitleAndSummary() {
    (findViewById(android.R.id.title) as TextView?)?.isSingleLine = false
    (findViewById(android.R.id.summary) as TextView?)?.isSingleLine = false
}