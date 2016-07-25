// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.activity;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import cat.mvmike.minimalcalendarwidget.MonthWidget;
import cat.mvmike.minimalcalendarwidget.R;
import cat.mvmike.minimalcalendarwidget.util.ConfigurationUtil;

public class ConfigurationActivity extends AppCompatActivity {

    private static final int BLANK_POSITION_DIFFERENCE = -1;

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.configuration);

        setAvailableValues();
        loadPreviousConfig();

        applyListener();
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

    private void setAvailableValues() {

        // get locale weekDays and remove blank initial value
        String[] localeWeekDays = DateFormatSymbols.getInstance(Locale.getDefault()).getWeekdays();
        String[] weekDays = Arrays.copyOfRange(localeWeekDays, 1, localeWeekDays.length);

        ArrayAdapter<String> adapterWeekDays = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weekDays);

        adapterWeekDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.startWeekDaySpinner)).setAdapter(adapterWeekDays);
    }

    private void loadPreviousConfig() {
        ((Spinner) findViewById(R.id.startWeekDaySpinner))
            .setSelection(ConfigurationUtil.getStartWeekDay(getApplicationContext()) + BLANK_POSITION_DIFFERENCE);
    }

    private void saveConfig() {

        int selectedPosition = ((Spinner) findViewById(R.id.startWeekDaySpinner)).getSelectedItemPosition();
        ConfigurationUtil.setStartWeekDay(getApplicationContext(), selectedPosition - BLANK_POSITION_DIFFERENCE);

        MonthWidget.forceRedraw(getApplicationContext());
    }
}
