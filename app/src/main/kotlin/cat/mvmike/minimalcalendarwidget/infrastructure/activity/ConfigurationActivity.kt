// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.activity

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cat.mvmike.minimalcalendarwidget.MonthWidget
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.Configuration
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver

class ConfigurationActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) = SystemResolver.get().startActivity(context, ConfigurationActivity::class.java)
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
        configurationItems().forEach {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it.getDisplayValues(applicationContext))
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            it.resource.getSpinner().adapter = adapter
        }
    }

    private fun loadPreviousConfig() {
        configurationItems().forEach {
            it.resource.getSpinner().setSelection(it.get(applicationContext).ordinal)
        }
    }

    private fun saveConfig() {
        configurationItems().forEach {
            it.set(applicationContext, it.get(it.resource.getSpinner().selectedItemPosition))
        }
        MonthWidget.forceRedraw(applicationContext)
    }

    private fun Int.getSpinner() = findViewById<Spinner>(this)
    private fun Int.getTextView() = findViewById<TextView>(this)
    private fun Int.getButton() = findViewById<Button>(this)

    private fun configurationItems() = setOf(
        Configuration.CalendarTheme,
        Configuration.FirstDayOfWeek,
        Configuration.InstancesSymbolSet,
        Configuration.InstancesColour

    )
}
