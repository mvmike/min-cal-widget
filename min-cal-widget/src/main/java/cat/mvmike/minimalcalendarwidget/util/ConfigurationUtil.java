package cat.mvmike.minimalcalendarwidget.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Map;

import cat.mvmike.minimalcalendarwidget.activity.ConfigurationActivity;

public abstract class ConfigurationUtil {

    public static final String PREFERENCES_ID = "mincal_prefs";

    public static final String START_WEEK_DAY = "start_week_day";

    private static final int START_WEEK_DAY_DEFAULT = Calendar.MONDAY;

    public static void startConfigurationView(final Context context) {
        launchConfigurationActivity(context);
    }

    public static void clearConfiguration(final Context context) {
        getConfiguration(context).edit().clear().commit();
    }

    public static int getStartWeekDay(final Context context){
        return getConfiguration(context).getInt(START_WEEK_DAY, START_WEEK_DAY_DEFAULT);
    }

    public static void setStartWeekDay(final Context context, final int startWeekDay){
        getConfiguration(context).edit().putInt(START_WEEK_DAY, startWeekDay).apply();
    }

    private static SharedPreferences getConfiguration(final Context context){
        return context.getSharedPreferences(PREFERENCES_ID, 0);
    }

    private static void launchConfigurationActivity(final Context context) {

        Intent configurationIntent = new Intent(context, ConfigurationActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(configurationIntent);
    }
}
