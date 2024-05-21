// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import androidx.preference.SeekBarPreference

@Suppress("unused")
class MultilinePreference : Preference {

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
        (holder.findViewById(android.R.id.title) as TextView?)?.setSingleLine(false)
        (holder.findViewById(android.R.id.summary) as TextView?)?.setSingleLine(false)
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
        (holder.findViewById(android.R.id.title) as TextView?)?.setSingleLine(false)
        (holder.findViewById(android.R.id.summary) as TextView?)?.setSingleLine(false)
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
        (holder.findViewById(android.R.id.title) as TextView?)?.setSingleLine(false)
        (holder.findViewById(android.R.id.summary) as TextView?)?.setSingleLine(false)
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
        (holder.findViewById(android.R.id.title) as TextView?)?.setSingleLine(false)
        (holder.findViewById(android.R.id.summary) as TextView?)?.setSingleLine(false)
    }
}