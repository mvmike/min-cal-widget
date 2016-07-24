// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.activity;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import cat.mvmike.minimalcalendarwidget.MonthWidget;
import cat.mvmike.minimalcalendarwidget.R;
import cat.mvmike.minimalcalendarwidget.util.ConfigurationUtil;

public class ConfigurationActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.configuration);

        setAvailableValues();
        loadPreviousConfig();

        applyListener();
        closeListener();
    }

    private void applyListener() {

        Button dismissButton = (Button) findViewById(R.id.applyButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfig();
                ConfigurationActivity.this.finish();
            }
        });
    }

    private void closeListener() {

        Button dismissButton = (Button) findViewById(R.id.closeButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigurationActivity.this.finish();
            }
        });
    }

    private void setAvailableValues() {

        ArrayAdapter<String> adapterWeekDays = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            DateFormatSymbols.getInstance(Locale.getDefault()).getWeekdays());

        adapterWeekDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.startWeekDaySpinner)).setAdapter(adapterWeekDays);
    }

    private void loadPreviousConfig() {
        ((Spinner) findViewById(R.id.startWeekDaySpinner)).setSelection(ConfigurationUtil.getStartWeekDay(getApplicationContext()));
    }

    private void saveConfig() {

        int selectedPosition = ((Spinner) findViewById(R.id.startWeekDaySpinner)).getSelectedItemPosition();

        if (selectedPosition != 0)
            ConfigurationUtil.setStartWeekDay(getApplicationContext(), selectedPosition);

        MonthWidget.forceRedraw(getApplicationContext());
    }
}
