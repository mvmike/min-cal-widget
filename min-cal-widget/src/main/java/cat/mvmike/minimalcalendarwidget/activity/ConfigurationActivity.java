// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.activity;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import cat.mvmike.minimalcalendarwidget.MonthWidget;
import cat.mvmike.minimalcalendarwidget.R;
import cat.mvmike.minimalcalendarwidget.util.ConfigurationUtil;
import cat.mvmike.minimalcalendarwidget.util.SymbolsUtil;
import cat.mvmike.minimalcalendarwidget.util.ThemesUtil;

public class ConfigurationActivity extends AppCompatActivity {

    private static final int BLANK_POSITION_DIFFERENCE = -1;

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

        setHyperlinkToTextView(R.id.source);
        setHyperlinkToTextView(R.id.donate);
    }

    private void setHyperlinkToTextView(final int id) {
        ((TextView) findViewById(id)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void applyListener() {

        Button dismissButton = (Button) findViewById(R.id.applyButton);
        dismissButton.setOnClickListener(v -> {
            saveConfig();
            this.finish();
        });
    }

    private void setAvailableValues() {

        // THEMES
        ArrayAdapter<String> adapterThemes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ThemesUtil.getAllThemeNames());

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
            new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SymbolsUtil.getAllSymbolNames());

        adapterSymbols.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.symbolsSpinner)).setAdapter(adapterSymbols);

        // SYMBOLS COLOUR
        ArrayAdapter<String> adapterSymbolsColour =
            new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SymbolsUtil.getAllSymbolColorNames());

        adapterSymbolsColour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.symbolsColourSpinner)).setAdapter(adapterSymbolsColour);
    }

    private void loadPreviousConfig() {

        // THEMES
        ((Spinner) findViewById(R.id.themeSpinner))
            .setSelection(ThemesUtil.Theme.valueOf(ConfigurationUtil.getThemeName(getApplicationContext())).ordinal());

        // WEEK DAYS
        ((Spinner) findViewById(R.id.startWeekDaySpinner))
            .setSelection(ConfigurationUtil.getStartWeekDay(getApplicationContext()) + BLANK_POSITION_DIFFERENCE);

        // SYMBOLS
        ((Spinner) findViewById(R.id.symbolsSpinner))
            .setSelection(SymbolsUtil.Symbol.valueOf(ConfigurationUtil.getInstancesSymbolName(getApplicationContext())).ordinal());

        // SYMBOLS COLOUR
        ((Spinner) findViewById(R.id.symbolsColourSpinner)).setSelection(
            SymbolsUtil.SymbolColor.valueOf(ConfigurationUtil.getInstancesSymbolColourName(getApplicationContext())).ordinal());
    }

    private void saveConfig() {

        // THEMES
        int themesSelectedPosition = ((Spinner) findViewById(R.id.themeSpinner)).getSelectedItemPosition();
        ConfigurationUtil.setTheme(getApplicationContext(), ThemesUtil.Theme.values()[themesSelectedPosition]);

        // WEEK DAYS
        int weekDaySelectedPosition = ((Spinner) findViewById(R.id.startWeekDaySpinner)).getSelectedItemPosition();
        ConfigurationUtil.setStartWeekDay(getApplicationContext(), weekDaySelectedPosition - BLANK_POSITION_DIFFERENCE);

        // SYMBOLS
        int symbolsSelectedPosition = ((Spinner) findViewById(R.id.symbolsSpinner)).getSelectedItemPosition();
        ConfigurationUtil.setInstancesSymbols(getApplicationContext(), SymbolsUtil.Symbol.values()[symbolsSelectedPosition]);

        // SYMBOLS COLOUR
        int symbolsColourSelectedPosition = ((Spinner) findViewById(R.id.symbolsColourSpinner)).getSelectedItemPosition();
        ConfigurationUtil.setInstancesSymbolColours(getApplicationContext(),
            SymbolsUtil.SymbolColor.values()[symbolsColourSelectedPosition]);

        MonthWidget.forceRedraw(getApplicationContext());
    }
}
