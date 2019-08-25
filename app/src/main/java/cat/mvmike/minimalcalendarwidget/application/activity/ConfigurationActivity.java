// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.application.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.time.DayOfWeek;

import cat.mvmike.minimalcalendarwidget.R;
import cat.mvmike.minimalcalendarwidget.application.MonthWidget;
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurableItem;
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Symbol;
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme;
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver;

public final class ConfigurationActivity extends AppCompatActivity {

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
        ArrayAdapter<String> adapterThemes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            SystemResolver.get().getThemeTranslatedValues(getApplicationContext()));
        adapterThemes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.themeSpinner)).setAdapter(adapterThemes);

        // WEEK DAYS
        ArrayAdapter<String> adapterWeekDays = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            SystemResolver.get().getDayOfWeekTranslatedValues(getApplicationContext()));
        adapterWeekDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.startWeekDaySpinner)).setAdapter(adapterWeekDays);

        // SYMBOLS
        ArrayAdapter<String> adapterSymbols = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            SystemResolver.get().getInstancesSymbolsTranslatedValues(getApplicationContext()));
        adapterSymbols.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.symbolsSpinner)).setAdapter(adapterSymbols);

        // SYMBOLS COLOUR
        ArrayAdapter<String> adapterSymbolsColour = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            SystemResolver.get().getInstancesSymbolsColourTranslatedValues(getApplicationContext()));
        adapterSymbolsColour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.symbolsColourSpinner)).setAdapter(adapterSymbolsColour);
    }

    private void loadPreviousConfig() {

        // THEMES
        ((Spinner) findViewById(R.id.themeSpinner))
            .setSelection(ConfigurationService.getTheme(getApplicationContext()).ordinal());

        // WEEK DAYS
        ((Spinner) findViewById(R.id.startWeekDaySpinner))
            .setSelection(ConfigurationService.getStartWeekDay(getApplicationContext()).ordinal());

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
        ConfigurationService.set(getApplicationContext(), ConfigurableItem.FIRST_DAY_OF_WEEK, DayOfWeek.values()[weekDaySelectedPosition]);

        // SYMBOLS
        int symbolsSelectedPosition = ((Spinner) findViewById(R.id.symbolsSpinner)).getSelectedItemPosition();
        ConfigurationService.set(getApplicationContext(), ConfigurableItem.INSTANCES_SYMBOLS, Symbol.values()[symbolsSelectedPosition]);

        // SYMBOLS COLOUR
        int symbolsColourSelectedPosition = ((Spinner) findViewById(R.id.symbolsColourSpinner)).getSelectedItemPosition();
        ConfigurationService.set(getApplicationContext(), ConfigurableItem.INSTANCES_SYMBOLS_COLOUR, Colour.values()[symbolsColourSelectedPosition]);

        MonthWidget.forceRedraw(getApplicationContext());
    }
}
