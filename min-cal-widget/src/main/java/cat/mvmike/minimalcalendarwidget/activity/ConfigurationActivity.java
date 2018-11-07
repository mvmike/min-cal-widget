// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

import cat.mvmike.minimalcalendarwidget.MonthWidget;
import cat.mvmike.minimalcalendarwidget.R;
import cat.mvmike.minimalcalendarwidget.service.configuration.ConfigurationService;
import cat.mvmike.minimalcalendarwidget.service.enums.Colour;
import cat.mvmike.minimalcalendarwidget.service.enums.ConfigurableItem;
import cat.mvmike.minimalcalendarwidget.service.enums.Symbol;
import cat.mvmike.minimalcalendarwidget.service.enums.Theme;

public final class ConfigurationActivity extends AppCompatActivity {

    private static final int BLANK_POSITION_DIFFERENCE = -1;

    public static void start(final Context context) {

        Intent configurationIntent = new Intent(context, ConfigurationActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(configurationIntent);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.configuration);

        setHyperlinks();

        setAvailableValues();
        loadPreviousConfig();

        applyListener();
    }

    private void setHyperlinks() {
        ((TextView) findViewById(R.id.source)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void applyListener() {

        Button dismissButton = findViewById(R.id.applyButton);
        dismissButton.setOnClickListener(v -> {
            saveConfig();
            this.finish();
        });
    }

    private void setAvailableValues() {

        // THEMES
        ArrayAdapter<String> adapterThemes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Theme.getAllThemeNames());

        adapterThemes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.themeSpinner)).setAdapter(adapterThemes);

        // WEEK DAYS
        String[] localeWeekDays = DateFormatSymbols.getInstance(Locale.getDefault()).getWeekdays();
        String[] weekDays = Arrays.copyOfRange(localeWeekDays, 1, localeWeekDays.length);

        ArrayAdapter<String> adapterWeekDays = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weekDays);

        adapterWeekDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.startWeekDaySpinner)).setAdapter(adapterWeekDays);

        // SYMBOLS
        ArrayAdapter<String> adapterSymbols =
            new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Symbol.getAllSymbolNames());

        adapterSymbols.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.symbolsSpinner)).setAdapter(adapterSymbols);

        // SYMBOLS COLOUR
        ArrayAdapter<String> adapterSymbolsColour =
            new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Colour.getAllColorNames());

        adapterSymbolsColour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.symbolsColourSpinner)).setAdapter(adapterSymbolsColour);
    }

    private void loadPreviousConfig() {

        // THEMES
        ((Spinner) findViewById(R.id.themeSpinner))
            .setSelection(ConfigurationService.getTheme(getApplicationContext()).ordinal());

        // WEEK DAYS
        ((Spinner) findViewById(R.id.startWeekDaySpinner))
            .setSelection(ConfigurationService.getStartWeekDay(getApplicationContext()) + BLANK_POSITION_DIFFERENCE);

        // SYMBOLS
        ((Spinner) findViewById(R.id.symbolsSpinner))
            .setSelection(ConfigurationService.getInstancesSymbols(getApplicationContext()).ordinal());

        // SYMBOLS COLOUR
        ((Spinner) findViewById(R.id.symbolsColourSpinner))
            .setSelection(ConfigurationService.getInstancesSymbolsColours(getApplicationContext()).ordinal());
    }

    private void saveConfig() {

        // THEMES
        int themesSelectedPosition = ((Spinner) findViewById(R.id.themeSpinner)).getSelectedItemPosition();
        ConfigurationService.set(getApplicationContext(), ConfigurableItem.THEME, Theme.values()[themesSelectedPosition]);

        // WEEK DAYS
        int weekDaySelectedPosition = ((Spinner) findViewById(R.id.startWeekDaySpinner)).getSelectedItemPosition();
        ConfigurationService.set(getApplicationContext(), ConfigurableItem.START_WEEK_DAY, weekDaySelectedPosition - BLANK_POSITION_DIFFERENCE);

        // SYMBOLS
        int symbolsSelectedPosition = ((Spinner) findViewById(R.id.symbolsSpinner)).getSelectedItemPosition();
        ConfigurationService.set(getApplicationContext(), ConfigurableItem.INSTANCES_SYMBOLS, Symbol.values()[symbolsSelectedPosition]);

        // SYMBOLS COLOUR
        int symbolsColourSelectedPosition = ((Spinner) findViewById(R.id.symbolsColourSpinner)).getSelectedItemPosition();
        ConfigurationService.set(getApplicationContext(), ConfigurableItem.INSTANCES_SYMBOLS_COLOUR, Colour.values()[symbolsColourSelectedPosition]);

        MonthWidget.forceRedraw(getApplicationContext());
    }
}
