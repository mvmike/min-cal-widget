// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cat.mvmike.minimalcalendarwidget.MonthWidget
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.BooleanConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.domain.configuration.EnumConfiguration
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Transparency

class ConfigurationActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) = context.startActivity(
            Intent(context, ConfigurationActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.configuration)
        setHyperlinks()
        setAvailableValues()
        loadPreviousConfig()
        applyListener()
    }

    private fun setHyperlinks() {
        R.id.source.getTextView().movementMethod = LinkMovementMethod.getInstance()
    }

    private fun applyListener() =
        R.id.applyButton.getButton().setOnClickListener {
            saveConfig()
            finish()
        }

    private fun setAvailableValues() {
        enumConfigurationItems().forEach {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it.getDisplayValues(applicationContext))
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            it.resource.getSpinner().adapter = adapter
        }
    }

    private fun loadPreviousConfig() {
        enumConfigurationItems().forEach {
            it.resource.getSpinner().setSelection(it.get(applicationContext).ordinal)
        }
        booleanConfigurationItems().forEach{
            it.resource.getCheckBox().isChecked = it.get(applicationContext)
        }
        Configuration.WidgetTransparency.resource.getSeekBar().progress =
            Configuration.WidgetTransparency.get(applicationContext).percentage
    }

    private fun saveConfig() {
        enumConfigurationItems().forEach {
            it.set(
                applicationContext,
                it.resource.getSpinner().selectedItemPosition
            )
        }
        booleanConfigurationItems().forEach{
            it.set(
                applicationContext,
                it.resource.getCheckBox().isChecked
            )
        }
        Configuration.WidgetTransparency.set(
            applicationContext,
            Transparency(Configuration.WidgetTransparency.resource.getSeekBar().progress)
        )

        MonthWidget.redraw(applicationContext)
    }

    private fun Int.getSpinner() = findViewById<Spinner>(this)
    private fun Int.getCheckBox() = findViewById<CheckBox>(this)
    private fun Int.getSeekBar() = findViewById<SeekBar>(this)
    private fun Int.getTextView() = findViewById<TextView>(this)
    private fun Int.getButton() = findViewById<Button>(this)

    private fun enumConfigurationItems() = setOf(
        EnumConfiguration.WidgetTheme,
        EnumConfiguration.FirstDayOfWeek,
        EnumConfiguration.InstancesSymbolSet,
        EnumConfiguration.InstancesColour
    )

    private fun booleanConfigurationItems() = setOf(
        BooleanConfiguration.WidgetShowDeclinedEvents,
        BooleanConfiguration.WidgetFocusOnCurrentWeek
    )
}
