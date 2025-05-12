// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.activity

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.clearAllConfiguration
import cat.mvmike.minimalcalendarwidget.infrastructure.fragment.SettingsFragment
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver.isDarkThemeEnabled

class ConfigurationActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) = context.startActivity(
            Intent(context, ConfigurationActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(
            when (applicationContext.isDarkThemeEnabled()) {
                true -> R.style.Theme_AppCompat_DarkTheme
                else -> R.style.Theme_AppCompat_LightTheme
            }
        )

        super.onCreate(savedInstanceState)

        setContentView(R.layout.configuration)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.configuration_view, SettingsFragment())
            .commit()
    }

    @Suppress("unused", "unused_parameter")
    fun onClickResetSettingsButton(view: View) =
        clearAllConfiguration(applicationContext)

    @Suppress("unused", "unused_parameter")
    fun onClickCloseSettingsButton(view: View) {
        intent
            ?.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )?.takeIf {
                it != AppWidgetManager.INVALID_APPWIDGET_ID
            }?.let {
                setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, it))
            }
        finishAfterTransition()
    }
}